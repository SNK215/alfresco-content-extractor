package fr.amexio.extractor.utils;

import fr.amexio.extractor.model.Credentials;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 *  Used to display information about free space to the user and to confirm the extraction
 *
 * @author Alexandre Lecoeur
 */

@Log4j2
@Getter
@Setter
public class IHM {
    Credentials credentials = Credentials.getInstance();
    public static int connectionTryCounter = 3;

    public void credentialsRequest () {
        do {
            Scanner sc = new Scanner(System.in);

            System.out.println("This program needs admin login and password to connect to Alfresco. " + connectionTryCounter + " try left");
            System.out.println("Admin login :");
            credentials.setUser(sc.nextLine());

            System.out.println("Admin password :");
            credentials.setPassword(sc.nextLine());
        }
        while(credentials.getPassword().isEmpty() || credentials.getUser().isEmpty());
    }

    public void startPermission(long extractionSize, double convertExtractionSize, String extractionSizePrefixMultiplier, long availableDiskSpace, double convertAvailableDiskSpace, String availableDiskSpacePrefixMultiplier){
        if (extractionSize >= availableDiskSpace) {
            log.error("Insufficient disk space. Available: " + availableDiskSpace + " bytes(" + convertAvailableDiskSpace + " " + availableDiskSpacePrefixMultiplier + "). Minimum expected: " + extractionSize + " bytes("+ convertExtractionSize + " " + extractionSizePrefixMultiplier +").");
            System.out.println("Not enough free space on your disk, please free up some space or change destination directory.");
            System.exit(1);
        } else {
            log.info("Available space on chosen partition : " + convertAvailableDiskSpace + " " + availableDiskSpacePrefixMultiplier +".");
            log.info("Total size of folders and files to extract : " + convertExtractionSize + " " + extractionSizePrefixMultiplier +".");
            log.info("Destination directory : " + credentials.getDestinationDirectory());

            System.out.println("\nDo you want to start extracting ? (Check available disk space and destination directory)");
            getUserChoice();
        }
    }

    public void getUserChoice() {
        Scanner sc = new Scanner(System.in);

        System.out.println("YES : press [y] then enter");
        System.out.println("NO  : press any other key then enter");

        String choice = sc.nextLine().toLowerCase();

        if (choice.length() > 1 && choice.charAt(0) == 'y') {
            getUserChoice();
        } else if (!choice.equals("y")) {
            System.exit(0);
        }
    }

    public void endProgram() throws IOException {
        System.out.println("\n\nPress enter to exit...\n\n");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        in.readLine();
        System.exit(1);
    }
}
