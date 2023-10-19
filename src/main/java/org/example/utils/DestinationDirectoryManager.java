package org.example.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Log4j2
public class DestinationDirectoryManager {

    public DestinationDirectoryManager() {
    }

    public void prepare(String destinationDirectory) throws IOException {


        Path directoryPath = Paths.get(destinationDirectory);
        File directory = new File(destinationDirectory);

        //Si le dossier de destination n'existe pas, alors on le crée
        if (!Files.exists(directoryPath)) {

            directory.mkdir();
            log.warn("Destination directory created at " + destinationDirectory);

        //Sinon on vide le contenu du dossier
        } else {

            FileUtils.cleanDirectory(directory);
            log.warn("Destination directory cleaned at " + destinationDirectory);

        }

    }


}
