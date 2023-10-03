package org.example.utils;

import org.apache.commons.io.FileUtils;
import org.example.exceptions.DirectoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryCleaner {

    public DirectoryCleaner() {
    }

    public void clean(String destinationFolder) throws IOException {
        Path dirPath = Paths.get(destinationFolder);

        //Si le dossier de destination n'existe pas, alors on le crée
        if (!Files.exists(dirPath)) {
             File newDestinationFolder = new File(destinationFolder);
             newDestinationFolder.mkdir();
             System.out.println("Destination folder created at \n" + destinationFolder);
        }

        File directory = new File(destinationFolder);
        FileUtils.cleanDirectory(directory);
    }


}
