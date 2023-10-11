package org.example.utils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

@SuppressWarnings({"unused"})
public class Extractor {

    private String targetPath;
    private String destinationFolder;
    private int countExtractedFiles;
    private int countExtractedFolders;
    private int countErrors;
    private String tempPathError = "";
    private JsonCreator jsonCreator = new JsonCreator();
    protected static final Logger logger = LogManager.getLogger();


    public Extractor() {
    }

    public Extractor(String targetPath, String destinationFolder) {
        this.targetPath = targetPath;
        this.destinationFolder = destinationFolder;
    }

    public void extractFolders(Folder folder) throws IOException {
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
                        countExtractedFolders++;
                        logger.info("Directory created : " + newDir.getPath());
                    }
                    tempPathError = newDir.getPath();

                    //Création d'un fichier JSON contenant les propriétés du fichier
                    FileWriter JsonFile = new FileWriter(newDir.getPath() + "_properties.json");
                    JsonFile.write(jsonCreator.convertDataToJSONObject(object));
                    JsonFile.flush();
                    logger.info("JSON file created : " + newDir.getName() + "_properties.json");

                    //Ajout des attributs
                    long creationDateMs = object.getCreationDate().getTimeInMillis();
                    FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
                    Files.setAttribute(newDir.toPath(), "creationTime", creationDateFileTime);
                    newDir.setLastModified(object.getLastModificationDate().getTimeInMillis());
                    logger.info("Metadata added to directory : " + newDir.getName());

                    //On extrait à nouveau les dossiers et fichiers enfants dans chaque dossier enfant
                    extractFiles(childFolder);
                    extractFolders(childFolder);
                }
            }
        }
        catch (NullPointerException e) {
            countErrors++;
            logger.error("Cannot extract directory/file : "  + tempPathError);
            for (StackTraceElement s : e.getStackTrace()) logger.error("StackTrace : " + s);
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
                logger.info("File created : " + newFile.getPath());


                //Création d'un fichier JSON contenant les propriétés du fichier
                FileWriter JsonFile = new FileWriter(newFile.getPath() + "_properties.json");
                JsonFile.write(jsonCreator.convertDataToJSONObject(object));
                JsonFile.flush();
                logger.info("JSON file created : " + newFile.getName() + "_properties.json");

                //Insertion du contenu dans le fichier
                InputStream inputStream = childDocument.getContentStream().getStream();
                FileUtils.writeByteArrayToFile(newFile, inputStream.readAllBytes());
                logger.info("Data stream inserted into file : " + newFile.getName());

                //Ajout des attributs
                long creationDateMs = childDocument.getCreationDate().getTimeInMillis();
                FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
                Files.setAttribute(newFile.toPath(),"creationTime",creationDateFileTime);
                newFile.setLastModified(childDocument.getLastModificationDate().getTimeInMillis());
                logger.info("Metadata added to file : " + newFile.getName());

                //Log de confirmation
                if (newFile.exists()) {
                    countExtractedFiles++;
                } else {
                    countErrors++;
                    logger.error("Cannot extract directory/file : "  + newFile.getPath());
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
