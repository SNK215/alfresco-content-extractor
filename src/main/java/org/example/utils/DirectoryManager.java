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

    public void cleanOrMake(String destinationFolder) throws IOException {

        Path dirPath = Paths.get(destinationFolder);
        File directory = new File(destinationFolder);

        //Si le dossier de destination n'existe pas, alors on le crée
        //Sinon on vide le contenu du dossier
        if (!Files.exists(dirPath)) {
            System.out.println(directory.mkdir() ?
                    "\u001B[33m" + "Destination directory created at " + destinationFolder + "\u001B[0m" :
                    "\u001B[31m" + "Could not create destination directory at " + destinationFolder + "\u001B[0m");
        } else {
            FileUtils.cleanDirectory(directory);
            System.out.println("\u001B[33m" + "Destination directory cleaned at " + destinationFolder + "\u001B[0m");
        }

    }


}
