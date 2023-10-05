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

        //Si le dossier de destination n'existe pas, alors on le crée
        //Sinon on vide le contenu du dossier
        if (!Files.exists(dirPath)) {
             File newDestinationFolder = new File(destinationFolder);
             newDestinationFolder.mkdir();
             System.out.println("\u001B[33m" + "Destination folder created at \n" + destinationFolder + "\u001B[0m");
        } else {
            File directory = new File(destinationFolder);
            FileUtils.cleanDirectory(directory);
            System.out.println("\u001B[33m" + "Destination folder cleaned at \n" + destinationFolder + "\u001B[0m");
        }

    }


}
