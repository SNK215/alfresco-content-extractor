package org.example.utils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.GregorianCalendar;

public class Extractor {

    private String targetPath = "/";
    private int countExtractedFiles = 0;
    private int countExtractedFolders = 0;
    private int countErrors = 0;
    String username = System.getProperty("user.name");
    private String destinationFolder = "C:\\Users\\"+username+"\\Documents\\extraction";
    private DirectoryManager directoryManager;
    private JsonObjectCreator jsonObjectCreator;
    private SessionGenerator sessionGenerator;
    private String tempPathError = "0";
    public Extractor() {
    }

    public Extractor(DirectoryManager directoryManager, JsonObjectCreator jsonObjectCreator, SessionGenerator sessionGenerator) {
        this.directoryManager = directoryManager;
        this.jsonObjectCreator = jsonObjectCreator;
        this.sessionGenerator = sessionGenerator;
    }

    public void extractContent(Folder folder) throws IOException {
        try {
            //Extraction des fichiers situés dans le Folder qui est fourni en paramètre
            extractFiles(folder);
            for (CmisObject object : folder.getChildren()) {
                if (object instanceof Folder) {
                    //Objet CmisObject converti en object Folder
                    Folder childFolder = (Folder) object;
                    //Création du dossier
                    File newDir = new File(destinationFolder + "/" + childFolder.getPath());
                    countExtractedFolders++;
                    if (!newDir.exists()) {
                        newDir.mkdirs();
                    }
                    tempPathError = newDir.getPath();
                    //Création d'un fichier JSON contenant les propriétés du fichier
                    FileWriter JsonFile = new FileWriter(newDir.getPath() + "_properties.json");
                    JsonFile.write(jsonObjectCreator.create(object).toJSONString());
                    JsonFile.flush();
                    //Ajout des attributs
                    GregorianCalendar creationDate = object.getCreationDate();
                    long creationDateMs = creationDate.getTimeInMillis();
                    FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
                    Files.setAttribute(newDir.toPath(), "creationTime", creationDateFileTime);
                    newDir.setLastModified(object.getLastModificationDate().getTimeInMillis());
                    //Log de confirmation
                    if (newDir.exists()) {
                        System.out.println("\u001B[32m" + "Directory created : " + newDir.getPath() + "\u001B[0m");
                    }
                    //On extrait à nouveau les dossiers et fichiers enfants dans chaque dossier enfant
                    extractFiles(childFolder);
                    extractContent(childFolder);
                }
            }
        }
        catch (NullPointerException e) {
            System.out.println("\u001B[31m" + "Fichier ou dossier impossible à extraire : "  + tempPathError + "\u001B[0m");
            countErrors++;
        }
        catch (CmisObjectNotFoundException e) {
            if (e.getMessage() != null) System.out.println(e.getMessage());
            System.out.println("targetPath incorrect, veuillez le changer et redémarrer l'application");
        }
    }

    public void extractFiles(Folder folder) throws IOException {
        //On itère sur chaque enfant dans le dossier qui est fourni en paramètre
        for (CmisObject object : folder.getChildren()) {
            if (object instanceof Document) {
                //Object CmisObject converti en object Document
                Document childDocument = (Document) object;
                //Création du fichier
                File newFile = new File(destinationFolder + childDocument.getPaths().get(0));
                countExtractedFiles++;
                tempPathError = newFile.getPath();
                //Création d'un fichier JSON contenant les propriétés du fichier
                FileWriter JsonFile = new FileWriter(newFile.getPath() + "_properties.json");
                JsonFile.write(jsonObjectCreator.create(object).toJSONString());
                JsonFile.flush();
                //Insertion du contenu dans le fichier
                ContentStream contentStream = childDocument.getContentStream();
                InputStream inputStream = contentStream.getStream();
                FileUtils.writeByteArrayToFile(newFile, inputStream.readAllBytes());
                //Ajout des attributs
                GregorianCalendar creationDate = childDocument.getCreationDate();
                long creationDateMs = creationDate.getTimeInMillis();
                FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
                Files.setAttribute(newFile.toPath(),"creationTime",creationDateFileTime);
                newFile.setLastModified(childDocument.getLastModificationDate().getTimeInMillis());
                //Log de confirmation
                if (newFile.exists()) {
                    System.out.println("\u001B[32m" + "File created : " + newFile.getPath() + "\u001B[0m");
                }
            }
        }
    }

    public int getCountErrors() {
        return countErrors;
    }

    public void setCountErrors(int countErrors) {
        this.countErrors = countErrors;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public int getCountExtractedFiles() {
        return countExtractedFiles;
    }

    public void setCountExtractedFiles(int countExtractedFiles) {
        this.countExtractedFiles = countExtractedFiles;
    }

    public int getCountExtractedFolders() {
        return countExtractedFolders;
    }

    public void setCountExtractedFolders(int countExtractedFolders) {
        this.countExtractedFolders = countExtractedFolders;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }
}
