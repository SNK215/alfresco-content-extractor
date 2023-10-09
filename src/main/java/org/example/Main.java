package org.example;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.example.utils.*;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

        SessionGenerator sessionGenerator = new SessionGenerator();
        DirectoryManager directoryManager = new DirectoryManager();
        FileConverter fileConverter = new FileConverter();
        Extractor extractor = new Extractor();

        directoryManager.cleanOrMake(extractor.getDestinationFolder());

        File newDir = new File(extractor.getDestinationFolder() + extractor.getTargetPath());
        if (!newDir.exists()){
            newDir.mkdirs();
        }

        Session session = sessionGenerator.generate();
        OperationContext oc = session.createOperationContext();
        oc.setIncludeAcls(true);
        fileConverter.setOc(oc);
        fileConverter.setSession(session);

        Folder folder = (Folder) session.getObjectByPath(extractor.getTargetPath());

        extractor.extractContent(folder);

        System.out.println("--- Extraction de " + extractor.getCountExtractedFiles() + " fichiers et " + extractor.getCountExtractedFolders() + " dossiers terminée ---");

        System.out.println(extractor.getCountErrors() == 0 ?
                "--- Aucune erreur rencontrée ---" :
                "--- " + extractor.getCountErrors() + " fichier ou dossier n'a pas pu être extrait ---"
                );

    }
}