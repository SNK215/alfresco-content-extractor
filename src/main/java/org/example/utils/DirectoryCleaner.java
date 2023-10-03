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

    public void clean(String destinationFolder) throws IOException, DirectoryNotFoundException {
        Path dirPath = Paths.get(destinationFolder);
        if (!Files.exists(dirPath)) {
             throw new DirectoryNotFoundException("Cannot find directory for clean operation at \n" + destinationFolder);
        }
        File directory = new File(destinationFolder);
        FileUtils.cleanDirectory(directory);
    }


}
