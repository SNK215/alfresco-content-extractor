package org.example.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.example.model.Credentials;

import java.io.File;
import java.util.Scanner;

@Log4j2
public class TotalSizeCalculator {
    private long totalSize;
    private long availableDiskSpace;
    private String prefixMultipliers;

    public TotalSizeCalculator() {
        Session session = new SessionGenerator().generate(new Credentials());
        String rootId = findRootNodeId(session);
        if (rootId!=null) {
            totalSize = calculateTotalSizeAndCount(session, rootId);
            startPermission();
        } else {
            log.error("Root folder not found");
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
        Folder rootFolder;
        try {
            rootFolder = (Folder) session.getObject(rootId);
        } catch (CmisObjectNotFoundException e) {
            log.error("Root folder not found"+ e.getMessage());
            return 0;
        }
        long calcSize = 0;
        ItemIterable<CmisObject> children = rootFolder.getChildren();
        for (CmisObject child : children) {
            if (child instanceof Document) {
                Document document = (Document) child;
                calcSize += document.getContentStreamLength();
            } else if (child instanceof Folder) {
                Folder folder = (Folder) child;
                calcSize += calculateTotalSizeAndCount(session, folder.getId());
            }
        }
        return calcSize;
    }
    public void calculateAvailableDiskSpace() {
        String partition = Credentials.getInstance().getDestinationDirectory().substring(0,2);
        log.info("Chosen partition : " + partition);
        File file = new File(partition);
        availableDiskSpace = file.getFreeSpace();
    }
    public void startPermission(){
        calculateAvailableDiskSpace();
        if (totalSize >= availableDiskSpace) {
            log.error("Insufficient memory. Available: " + availableDiskSpace + " bytes(" + sizeConverter(availableDiskSpace) + " " + prefixMultipliers+ "). Minimum expected: " + totalSize + " bytes("+ sizeConverter(totalSize) + " " + prefixMultipliers +").");
            System.out.println("Not enough memory space on your disk, please free up some space or change destination directory.");
            System.out.println("Do you want to restart ?");
            if (getUserChoice().equals("y")){
                startPermission();
            }
        } else {
            log.info("Available space on chosen partition : " + sizeConverter(availableDiskSpace) + " " + prefixMultipliers +".");
            log.info("Total size of folders and files to extract : " + sizeConverter(totalSize) + " " + prefixMultipliers +".");
            System.out.println("Do you want to start extracting ?");
            getUserChoice();
        }
    }
    public String getUserChoice() {
        Scanner sc = new Scanner(System.in);
        System.out.println("YES : press [y]");
        System.out.println("NO  : press another key");
        String choice = sc.nextLine().toLowerCase();
        if (!choice.equals("y")) {
            System.exit(0);
        }
        return choice;
    }

    public double sizeConverter(long numberToConvert) {
        double adaptedTotalSize = 0;
        if (numberToConvert < 1e3) {
            adaptedTotalSize = numberToConvert;
            prefixMultipliers = "bytes";
        } else if (numberToConvert < 1e6) {
            adaptedTotalSize = (double) numberToConvert / 1e3;
            prefixMultipliers = "KB";
        } else if (numberToConvert < 1e9) {
            adaptedTotalSize = (double) numberToConvert / 1e6;
            prefixMultipliers = "MB";
        } else if (numberToConvert < 1e12) {
            adaptedTotalSize = (double) numberToConvert / 1e9;
            prefixMultipliers = "GB";
        } else {
            adaptedTotalSize = (double) numberToConvert / 1e12;
            prefixMultipliers = "TB";
        }
        return Math.round(adaptedTotalSize*100.0)/100.0;
    }
}
