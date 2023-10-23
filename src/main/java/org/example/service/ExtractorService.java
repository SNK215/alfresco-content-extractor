package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;

import org.example.model.Credentials;
import org.example.utils.DestinationDirectoryManager;
import org.example.utils.Extractor;
import org.example.utils.SessionGenerator;
import org.example.utils.SizeCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Log4j2
public class ExtractorService {

    public ExtractorService() {
    }

    /**
     * Call all the classes that are necessary to extract the Alfresco repository successfully
     * @throws IOException in case there is a problem with local file manipulation
     */
    public void startExtraction() throws IOException {
        Credentials credentials = new Credentials();
        new SizeCalculator().getSizesAndPrefixMultipliers();
        String destinationDirectory = credentials.getDestinationDirectory();
        SessionGenerator sessionGenerator = new SessionGenerator();
        DestinationDirectoryManager destinationDirectoryManager = new DestinationDirectoryManager();

        Extractor extractor = new Extractor(destinationDirectory);

        destinationDirectoryManager.prepare(destinationDirectory);

        Session session = sessionGenerator.generate(credentials);

        // "/" refers to the root of the Alfresco repository
        Folder alfrescoRootFolder = (Folder) session.getObjectByPath("/");

        log.info("Connected to Alfresco through " + credentials.getServiceUrl());
        log.warn("Extraction started");

        extractor.extractFolders(alfrescoRootFolder);

        System.out.println("\n" + extractor.getCountExtractedFiles() + " files and " + extractor.getCountExtractedFolders() + " directories extracted successfully");

        System.out.println(extractor.getCountErrors() == 0 ?
                "No errors detected" :
                extractor.getCountErrors() + " files or directories couldn't be extracted, please check logs"
        );

        System.out.println("\n\nPress enter to exit...\n\n");

        //Reads user input
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        in.readLine();
    }
}
