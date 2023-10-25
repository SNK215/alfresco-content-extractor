package org.example.utils;

import lombok.extern.log4j.Log4j2;
import org.example.model.Credentials;

import java.util.Scanner;

@Log4j2
public class IHM {
    /**
     * Tells the user if he has enough free space on his computer and asks if he wants to proceed with the extraction
     */
    public void startPermission(long extractionSize, double convertExtractionSize, String extractionSizePrefixMultiplier, long availableDiskSpace, double convertAvailableDiskSpace, String availableDiskSpacePrefixMultiplier){
        if (extractionSize >= availableDiskSpace) {
            log.error("Insufficient disk space. Available: " + availableDiskSpace + " bytes(" + convertAvailableDiskSpace + " " + availableDiskSpacePrefixMultiplier + "). Minimum expected: " + extractionSize + " bytes("+ convertExtractionSize + " " + extractionSizePrefixMultiplier +").");
            System.out.println("Not enough free space on your disk, please free up some space or change destination directory.");
            System.exit(0);
        } else {
            log.info("Available space on chosen partition : " + convertAvailableDiskSpace + " " + availableDiskSpacePrefixMultiplier +".");
            log.info("Total size of folders and files to extract : " + convertExtractionSize + " " + extractionSizePrefixMultiplier +".");
            log.info("Destination directory : " + Credentials.getInstance().getDestinationDirectory());
            System.out.println("\nDo you want to start extracting ? (check available disk space and destination directory)");
            getUserChoice();
        }
    }

    public void getUserChoice() {
        Scanner sc = new Scanner(System.in);
        System.out.println("YES : press [y] then enter");
        System.out.println("NO  : press any other key then enter");
        String choice = sc.nextLine().toLowerCase();
        if (!choice.equals("y")) {
            System.exit(0);
        }
    }
}
