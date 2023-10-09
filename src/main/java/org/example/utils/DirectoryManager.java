package org.example.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryManager {

    public DirectoryManager() {
    }

    public void prepareDestinationDirectory(String destinationDirectory) throws IOException {

        Path directoryPath = Paths.get(destinationDirectory);
        File directory = new File(destinationDirectory);

        //Si le dossier de destination n'existe pas, alors on le crée
        //Sinon on vide le contenu du dossier
        if (!Files.exists(directoryPath)) {

            System.out.println(directory.mkdir() ?
                    "\u001B[33m" + "Destination directory created at " + destinationDirectory + "\u001B[0m" :
                    "\u001B[31m" + "Could not create destination directory at " + destinationDirectory + "\u001B[0m");

        } else {

            FileUtils.cleanDirectory(directory);
            System.out.println("\u001B[33m" + "Destination directory cleaned at " + destinationDirectory + "\u001B[0m");

        }

    }


}
