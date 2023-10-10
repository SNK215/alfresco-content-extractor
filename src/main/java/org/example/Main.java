package org.example;

import org.apache.chemistry.opencmis.client.api.*;
import org.example.utils.*;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

        String targetPath = "/";
        String destinationDirectory = "C:\\Users\\"+System.getProperty("user.name")+"\\Documents\\extraction";
        SessionGenerator sessionGenerator = new SessionGenerator();
        DirectoryManager directoryManager = new DirectoryManager();
        Extractor extractor = new Extractor(targetPath, destinationDirectory);

        directoryManager.prepareDestinationDirectory(destinationDirectory);

        //Utile uniquement si on extrait un dossier autre que le dossier root
        File newDir = new File(destinationDirectory + targetPath);
        if (!newDir.exists()){
            newDir.mkdirs();
        }

        Session session = sessionGenerator.generate();
        Folder alfrescoRootFolder = (Folder) session.getObjectByPath(targetPath);
        extractor.extractFolders(alfrescoRootFolder);

        System.out.println("--- Extraction de " + extractor.getCountExtractedFiles() + " fichiers et " + extractor.getCountExtractedFolders() + " dossiers terminée ---");

        System.out.println(extractor.getCountErrors() == 0 ?
                "--- Aucune erreur rencontrée ---" :
                "--- " + extractor.getCountErrors() + " fichier ou dossier n'a pas pu être extrait ---"
                );

    }
}