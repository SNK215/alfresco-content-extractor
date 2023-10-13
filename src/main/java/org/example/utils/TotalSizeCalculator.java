package org.example.utils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Credentials;

import java.util.Scanner;


public class TotalSizeCalculator {
    private long totalSize;
    private long fileCount;
    private long folderCount;
    private String prefixMultipliers;
    protected static final Logger logger = LogManager.getLogger();

    public TotalSizeCalculator() {
        Session session = new SessionGenerator().generate(new Credentials());
        String rootId = findRootNodeId(session);
        if (rootId!=null) {
            totalSize = calculateTotalSizeAndCount(session, rootId);
            System.out.println("La taille totale de tous les fichiers et dossiers est : " + totalSize + " octets");
            System.out.println("Nombre total de dossiers : " + folderCount);
            //Compte de fichier à revoir
            System.out.println("Nombre total de fichiers 1 : " + fileCount);
            startPermission();
        } else {
            logger.error("Root folder not found");
        }
    }
    public String findRootNodeId (Session session) {
        Folder rootFolder = session.getRootFolder();
        String rootNodeId = rootFolder.getId();

        if (rootNodeId != null) {
            return rootNodeId;
        } else {
            ItemIterable<CmisObject> children = rootFolder.getChildren();

            for (CmisObject child : children) {
                if (child instanceof Folder) {
                    String parentId = child.getPropertyValue(PropertyIds.PARENT_ID);
                    if (parentId == null) {
                        return child.getId();
                    }
                }
            }
        }
        return null;
    }
    public long calculateTotalSizeAndCount(Session session, String rootId) {
        Folder rootFolder = null;
        try {
            rootFolder = (Folder) session.getObject(rootId);
        } catch (CmisObjectNotFoundException e) {
            logger.error("Root folder not found"+ e.getMessage());
            return 0;
        }
        long calcSize = 0;
        ItemIterable<CmisObject> children = rootFolder.getChildren();
        for (CmisObject child : children) {
            if (child instanceof Document) {
                Document document = (Document) child;
                calcSize += document.getContentStreamLength();
                fileCount++;
            } else if (child instanceof Folder) {
                Folder folder = (Folder) child;
                calcSize += calculateTotalSizeAndCount(session, folder.getId());
                folderCount++;
            }
        }
        return calcSize;
    }
    public void startPermission(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please make sure your computer's available disk space is greater than "+ sizeConverter() + " " + prefixMultipliers +".");
        System.out.println("Do you want to start extracting ?");
        System.out.println("YES : press [y]");
        System.out.println("NO : press another key");
        String choice = sc.nextLine();
        if (!choice.equalsIgnoreCase("y")) {
            System.exit(0);
        }
    }
    public double sizeConverter() {
        double adaptedTotalSize = 0;
        if (totalSize < 1e3) {
            prefixMultipliers = "bytes";
        } else if (totalSize < 1e6) {
            adaptedTotalSize = (double) totalSize / 1e3;
            prefixMultipliers = "MB";
        } else if (totalSize < 1e9) {
            adaptedTotalSize = (double) totalSize / 1e6;
            prefixMultipliers = "MB";
        } else if (totalSize < 1e12) {
            adaptedTotalSize = (double) totalSize / 1e9;
            prefixMultipliers = "GB";
        } else {
            adaptedTotalSize = (double) totalSize / 1e12;
            prefixMultipliers = "TB";
        }
        return Math.round(adaptedTotalSize*100.0)/100.0+0.01;
    }
}
