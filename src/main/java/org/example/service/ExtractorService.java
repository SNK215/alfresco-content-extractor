package org.example.service;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Credentials;
import org.example.utils.DestinationDirectoryManager;
import org.example.utils.Extractor;
import org.example.utils.SessionGenerator;
import org.example.utils.TotalSizeCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExtractorService {
    private static final Logger logger = LogManager.getLogger();

    public ExtractorService() {
    }

    public void startExtraction() throws IOException {
        Credentials credentials = new Credentials();
        new TotalSizeCalculator();
        String destinationDirectory = credentials.getDestinationDirectory();
        SessionGenerator sessionGenerator = new SessionGenerator();
        DestinationDirectoryManager destinationDirectoryManager = new DestinationDirectoryManager();
        String targetPath = "/";
        Extractor extractor = new Extractor(targetPath, destinationDirectory);

        destinationDirectoryManager.prepare(destinationDirectory);

        Session session = sessionGenerator.generate(credentials);
        Folder alfrescoRootFolder = (Folder) session.getObjectByPath(targetPath);

        logger.info("Connected to Alfresco through " + credentials.getServiceUrl());
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
