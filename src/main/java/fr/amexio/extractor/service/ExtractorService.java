package fr.amexio.extractor.service;

import fr.amexio.extractor.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;

import fr.amexio.extractor.model.Credentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;


/**
 * Where the extraction process takes place
 *
 * @author Lucas Langowski
 */
@Log4j2
public class ExtractorService {



    public ExtractorService() {
    }

    /**
     * Call all the classes that are necessary to extract the Alfresco repository successfully
     * @throws IOException in case there is a problem with local file manipulation
     */
    public void startExtraction() throws IOException {

        Instant start = Instant.now();

        IHM ihm = new IHM();
        ihm.credentialsRequest();

        Credentials credentials = Credentials.getInstance();
        credentials.init();

        SessionGenerator sessionGenerator = new SessionGenerator();

        Session session = sessionGenerator.generate(credentials);

        new SizeCalculator().getSizesAndPrefixMultipliers();

        DestinationDirectoryManager destinationDirectoryManager = new DestinationDirectoryManager();

        Extractor extractor = new Extractor(credentials.getDestinationDirectory());

        destinationDirectoryManager.prepare(credentials.getDestinationDirectory());

        // If the user chooses a custom folder to be extracted, it needs to be created first before extraction
        File selectiveFolder = new File(credentials.getDestinationDirectory() + credentials.getSelectiveExtractorPath());
        if (!selectiveFolder.exists()) {
            selectiveFolder.mkdir();
            extractor.setCountExtractedFolders(extractor.getCountExtractedFolders() + 1);
        }

        Folder alfrescoRootFolder = (Folder) session.getObjectByPath(credentials.getSelectiveExtractorPath());

        log.info("Connected to Alfresco through " + credentials.getServiceUrl());

        extractor.extractFolders(alfrescoRootFolder);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toSeconds();

        // Logging of extraction stats
        log.info("Extracted files : " + extractor.getCountExtractedFiles());
        log.info("Extracted folders : " + extractor.getCountExtractedFolders());
        log.info("Elapsed time : " + timeElapsed/60 + " minutes and " + timeElapsed % 60 + " seconds");

        // Displaying info about extraction to user and app shutdown
        System.out.println("\n" +
                extractor.getCountExtractedFiles() + " files and " +
                extractor.getCountExtractedFolders() + " directories extracted successfully in " +
                timeElapsed / 60 + " minutes and " +
                timeElapsed % 60 + " seconds");
        System.out.println(( extractor.getCountErrors() == 0 ) ?
                "No errors detected" :
                extractor.getCountErrors() + " files or directories couldn't be extracted, please check logs");
        System.out.println("\n\nPress enter to exit...\n\n");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // used to shut down app on "enter" press
        in.readLine(); // used to shut down app on "enter" press
    }
}