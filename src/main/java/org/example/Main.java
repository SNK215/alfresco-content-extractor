package org.example;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.*;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    protected static final Logger logger = LogManager.getLogger();

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
        logger.info("Connected to Alfresco through " + sessionGenerator.getServiceUrl());
        logger.warn("Extraction started");
        extractor.extractFolders(alfrescoRootFolder);

        System.out.println("\n" + extractor.getCountExtractedFiles() + " files and " + extractor.getCountExtractedFolders() + " directories extracted successfully");
        System.out.println(extractor.getCountErrors() == 0 ?
                "No errors detected" :
                extractor.getCountErrors() + " files or directories couldn't be extracted"
        );

        System.out.println("\n\nPress enter to exit...\n\n");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        in.readLine();

    }
}