package utils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.example.model.Credentials;
import org.example.utils.IHM;
import org.example.utils.SessionGenerator;
import org.example.utils.SizeCalculator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SizeCalculatorTest {
    private SizeCalculator sizeCalculator;

    @BeforeEach
    public void setUp() {
        sizeCalculator = new SizeCalculator();
    }
    @Test
    public void givenRootIdNotNull_whenFindRootNodeId_thenReturnCorrectPath () {
        // Création session
        Session session = mock(Session.class);
        Folder rootFolder = mock(Folder.class);
        when(session.getRootFolder()).thenReturn(rootFolder);
        when(rootFolder.getId()).thenReturn("rootId");

        String result = sizeCalculator.findRootNodeId(session);

        assertEquals("rootId", result);
    }

    @Test
    public void givenRootIdNull_whenFindRootNodeId_thenReturnFolderId() {
        Session session = mock(Session.class);
        Folder rootFolder = mock(Folder.class);

        // Mocker ItemIterable
        ItemIterable<CmisObject> itemIterable = mock(ItemIterable.class);

        List<CmisObject> cmisObjectList = new ArrayList<>();

        // Créer enfant fictif de type folder
        Folder folder = mock(Folder.class);
        when(folder.getId()).thenReturn("childFolderId");

        // Ajouter folder à liste
        cmisObjectList.add(folder);

        // Configurer comportement de l'ItemIterable
        when(itemIterable.iterator()).thenReturn(cmisObjectList.iterator());

        // Configurer le comportement avec Mockito
        when(session.getRootFolder()).thenReturn(rootFolder);

        // Configurer le comportement de getChildren() pour renvoyer l'ItemIterable simulé
        when(rootFolder.getChildren()).thenReturn(itemIterable);

        // Appel de la méthode à tester
        String result = sizeCalculator.findRootNodeId(session);

        // Vérification
        assertEquals(folder.getId(), result);
    }
    @Test
    public void givenFile_whenCalculateExtractionSize_thenReturnSize(){
        Session session = mock(Session.class);
        Folder rootFolder = mock(Folder.class);

        ItemIterable<CmisObject> itemIterable = mock(ItemIterable.class);

        // Créer faux fichier
        Document document = mock(Document.class);
        when(document.getContentStreamLength()).thenReturn(10L);

        List<CmisObject> cmisObjectList = new ArrayList<>();
        cmisObjectList.add(document);
        when(itemIterable.iterator()).thenReturn(cmisObjectList.iterator());

        when(session.getObject("rootId")).thenReturn(rootFolder);

        when(rootFolder.getChildren()).thenReturn(itemIterable);

        long result = sizeCalculator.calculateExtractionSize(session, "rootId");
        assertEquals(10L, result);
    }

    @Test
    public void givenRootFolderNotFound_whenCalculateExtractionSize_thenReturn0() {
        Session session = mock(Session.class);

        when(session.getObject("rootId")).thenThrow(new CmisObjectNotFoundException("Root folder not found"));

        long extractionSize = sizeCalculator.calculateExtractionSize(session, "rootId");

        assertEquals(0L, extractionSize);
    }

    @Test
    public void givenFile_whenCalculateAvailableDiskSpace_thenReturnIfAvailableDiskSpaceMoreThan0() {
        Credentials credentials = Credentials.getInstance();
        credentials.setDestinationDirectory("C:\\example\\directory");

        long availableDiskSpace = sizeCalculator.calculateAvailableDiskSpace();

        assertTrue(availableDiskSpace > 0);
    }
    @Test
    public void verifySizeConverterWithValidNumber() {
        List<Object> convertedSize1 = sizeCalculator.sizeConverter(1000L);
        List<Object> convertedSize2 = sizeCalculator.sizeConverter(1000000L);
        List<Object> convertedSize3 = sizeCalculator.sizeConverter(1000000000L);

        assertEquals(1.0, convertedSize1.get(0));
        assertEquals("Kb", convertedSize1.get(1));

        assertEquals(1.0, convertedSize2.get(0));
        assertEquals("Mb", convertedSize2.get(1));

        assertEquals(1.0, convertedSize3.get(0));
        assertEquals("Gb", convertedSize3.get(1));
    }

    @Disabled
    @Test
    public void testGetSizesAndPrefixMultipliersSuccess() {
        // Créez des mocks pour les dépendances nécessaires
        SessionGenerator sessionGenerator = mock(SessionGenerator.class);
        IHM ihm = mock(IHM.class);
        SizeCalculator sizeCalculator = new SizeCalculator();

        // Configurez le comportement attendu de vos mocks
        Session session = mock(Session.class);
        when(sessionGenerator.generate(any(Credentials.class))).thenReturn(session);

        // Assurez-vous que rootId est non nul
        when(sizeCalculator.findRootNodeId(session)).thenReturn("validRootId"); // Ici, nous configurons findRootNodeId pour renvoyer "validRootId"

        // Configurez le comportement pour la méthode calculateExtractionSize
        when(sizeCalculator.calculateExtractionSize(session, "validRootId")).thenReturn(1000L);

        // Configurez le comportement pour la méthode calculateAvailableDiskSpace
        when(sizeCalculator.calculateAvailableDiskSpace()).thenReturn(2000L);

        // Configurez le comportement pour la méthode sizeConverter
        when(sizeCalculator.sizeConverter(1000L)).thenReturn(Arrays.asList(1.0, "Kb"));
        when(sizeCalculator.sizeConverter(2000L)).thenReturn(Arrays.asList(2.0, "Kb"));

        // Appelez la méthode que vous testez
        sizeCalculator.getSizesAndPrefixMultipliers();

        // Vérifiez que les méthodes appropriées ont été appelées avec les arguments attendus
        Mockito.verify(ihm).startPermission(1000L, 1.0, "Kb", 2000L, 2.0, "Kb");
    }
}
