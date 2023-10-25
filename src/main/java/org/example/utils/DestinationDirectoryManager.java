package org.example.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.example.model.Credentials;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Prepare destination directory
 *
 * @author Lucas Langowski
 */
@Log4j2
public class DestinationDirectoryManager {


    public DestinationDirectoryManager() {
    }

    /**
     * Prepare the destination directory of the extraction.
     * The destination directory is created if it doesn't exist, and is cleaned if it does exist.
     * @param destinationDirectory where the extracted files will be injected
     * @throws IOException when the destination directory cannot be cleaned
     *
     */
    public void prepare(String destinationDirectory) throws IOException {


        Path directoryPath = Paths.get(destinationDirectory);
        File directory = new File(destinationDirectory);

        if (!Files.exists(directoryPath)) {

            directory.mkdirs();
            log.warn("Destination directory created at " + destinationDirectory);

        } else {

            FileUtils.cleanDirectory(directory);
            log.warn("Destination directory cleaned at " + destinationDirectory);

        }
    }

}
