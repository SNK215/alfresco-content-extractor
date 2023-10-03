package org.example;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.example.exceptions.DirectoryNotFoundException;
import org.example.utils.DirectoryCleaner;
import org.example.utils.Extractor;
import org.example.utils.JsonObjectCreator;
import org.example.utils.SessionGenerator;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException, DirectoryNotFoundException {

        SessionGenerator sessionGenerator = new SessionGenerator();
        DirectoryCleaner directoryCleaner = new DirectoryCleaner();
        JsonObjectCreator jsonObjectCreator = new JsonObjectCreator();
        Extractor extractor = new Extractor(directoryCleaner, jsonObjectCreator, sessionGenerator);

        directoryCleaner.clean(extractor.getDestinationFolder());
        File newDir = new File(extractor.getDestinationFolder() + extractor.getTargetPath());
        if (!newDir.exists()){
            newDir.mkdirs();
        }

        Session session = sessionGenerator.generate();
        Folder folder = (Folder) session.getObjectByPath(extractor.getTargetPath());
        extractor.extractContent(folder);

        System.out.println("--- Extraction de " + extractor.getCountExtractedFiles() + " fichiers et " + extractor.getCountExtractedFolders() + " dossiers terminée ---");
        System.out.println("--- " + extractor.getCountErrors() + " fichier ou dossier n'a pas pu être extrait ---");




    }
}