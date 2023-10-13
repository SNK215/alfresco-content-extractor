package org.example.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryManager {

    protected static final Logger logger = LogManager.getLogger();

    public DirectoryManager() {
    }

    public void prepareDestinationDirectory(String destinationDirectory) throws IOException {


        Path directoryPath = Paths.get(destinationDirectory);
        File directory = new File(destinationDirectory);

        //Si le dossier de destination n'existe pas, alors on le crée
        //Sinon on vide le contenu du dossier
        if (!Files.exists(directoryPath)) {

            directory.mkdir();
            logger.warn("Destination directory created at " + destinationDirectory);

        } else {

            FileUtils.cleanDirectory(directory);
            logger.warn("Destination directory cleaned at " + destinationDirectory);

        }

    }


}
