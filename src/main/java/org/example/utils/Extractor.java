package org.example.utils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

@SuppressWarnings({"unused"})
public class Extractor {

    private String targetPath = "/";
    private int countExtractedFiles;
    private int countExtractedFolders;
    private int countErrors;
    private String destinationFolder = "C:\\Users\\"+System.getProperty("user.name")+"\\Documents\\extraction";
    private String tempPathError = "";
    private FileConverter fileConverter = new FileConverter();

    public Extractor() {
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
                    if (!newDir.exists()) {
                        newDir.mkdirs();
                    }
                    tempPathError = newDir.getPath();

                    //Création d'un fichier JSON contenant les propriétés du fichier
                    FileWriter JsonFile = new FileWriter(newDir.getPath() + "_properties.json");
                    JsonFile.write(fileConverter.convertDataToJSONObject(object));
                    JsonFile.flush();

                    //Ajout des attributs
                    long creationDateMs = object.getCreationDate().getTimeInMillis();
                    FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
                    Files.setAttribute(newDir.toPath(), "creationTime", creationDateFileTime);
                    newDir.setLastModified(object.getLastModificationDate().getTimeInMillis());

                    //Log de confirmation
                    if (newDir.exists()) {
                        countExtractedFolders++;
                        System.out.println("\u001B[32m" + "Directory created : " + newDir.getPath() + "\u001B[0m");
                    }

                    //On extrait à nouveau les dossiers et fichiers enfants dans chaque dossier enfant
                    extractFiles(childFolder);
                    extractContent(childFolder);
                }
            }
        }
        catch (NullPointerException e) {
            countErrors++;
            System.out.println("\u001B[31m" + "Fichier ou dossier impossible à extraire : "  + tempPathError + "\u001B[0m");
            for (StackTraceElement s : e.getStackTrace()) {
                System.out.println("\u001B[31m" + s + "\u001B[0m");
            }
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

                //Création d'un fichier JSON contenant les propriétés du fichier
                FileWriter JsonFile = new FileWriter(newFile.getPath() + "_properties.json");
                JsonFile.write(fileConverter.convertDataToJSONObject(object));
                JsonFile.flush();

                //Insertion du contenu dans le fichier
                InputStream inputStream = childDocument.getContentStream().getStream();
                FileUtils.writeByteArrayToFile(newFile, inputStream.readAllBytes());

                //Ajout des attributs
                long creationDateMs = childDocument.getCreationDate().getTimeInMillis();
                FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
                Files.setAttribute(newFile.toPath(),"creationTime",creationDateFileTime);
                newFile.setLastModified(childDocument.getLastModificationDate().getTimeInMillis());

                //Log de confirmation
                if (newFile.exists()) {
                    countExtractedFiles++;
                    System.out.println("\u001B[32m" + "File created : " + newFile.getPath() + "\u001B[0m");
                } else {
                    countErrors++;
                    System.out.println("\u001B[31m" + "Fichier ou dossier impossible à extraire : "  + newFile.getPath() + "\u001B[0m");
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
