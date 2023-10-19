package org.example.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;


public class DestinationDirectoryManager {

    private static final Logger logger = LogManager.getLogger();

    public DestinationDirectoryManager() {
    }

    public void prepare(String destinationDirectory) throws IOException {


        Path directoryPath = Paths.get(destinationDirectory);
        File directory = new File(destinationDirectory);

        //Si le dossier de destination n'existe pas, alors on le crée
        if (!Files.exists(directoryPath)) {

            directory.mkdir();
            logger.warn("Destination directory created at " + destinationDirectory);

        //Sinon on vide le contenu du dossier
        } else {

            FileUtils.cleanDirectory(directory);
            logger.warn("Destination directory cleaned at " + destinationDirectory);

        }

    }


}
