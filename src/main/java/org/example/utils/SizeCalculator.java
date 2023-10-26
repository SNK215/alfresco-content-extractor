package org.example.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.example.model.Credentials;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Calculate free disk space and extraction size
 *
 * @author Alexandre Lecoeur
 */
@Log4j2
public class SizeCalculator {

    public SizeCalculator() {

    }


    /**
     * Calls different classes to get available free space and extraction size
     *
     * @see IHM
     */
    public void getSizesAndPrefixMultipliers() {
        Session session = new SessionGenerator().generate(Credentials.getInstance());
        String rootId = findRootNodeId(session);

        if (rootId != null) {
            long extractionSize = calculateExtractionSize(session, rootId);
            long availableDiskSpace = calculateAvailableDiskSpace();
            new IHM().startPermission(extractionSize, (Double) sizeConverter(extractionSize).get(0), (String) sizeConverter(extractionSize).get(1), availableDiskSpace, (Double) sizeConverter(availableDiskSpace).get(0), (String) sizeConverter(availableDiskSpace).get(1));
        } else {
            log.error("Root folder not found");
        }
    }


    /**
     * @param session Alfresco session that was generated through SessionGenerator Class
     * @return NodeId of the Alfresco repository
     */
    public String findRootNodeId (Session session) {
        Folder rootFolder = (Folder) session.getObjectByPath(Credentials.getInstance().getSelectiveExtractorPath());
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


    /**
     * @param session Alfresco session that was generated through SessionGenerator Class
     * @param rootId nodeId of the Alfresco repository
     * @return Total size of the Alfresco repository to extract
     */
    public long calculateExtractionSize(Session session, String rootId) {
        Folder rootFolder;

        try {
            rootFolder = (Folder) session.getObject(rootId);
        } catch (CmisObjectNotFoundException e) {
            log.error("Root folder not found"+ e.getMessage());
            return 0;
        }

        long calcSize = 0;

        for (CmisObject child : rootFolder.getChildren()) {
            if (child instanceof Document) {
                Document document = (Document) child;
                calcSize += document.getContentStreamLength();
            } else if (child instanceof Folder) {
                Folder folder = (Folder) child;
                calcSize += calculateExtractionSize(session, folder.getId());
            }
        }

        return calcSize;
    }


    /**
     * @return Amount of free space on the user's partition. Partition is defined in extractor_application.properties
     */
    public long calculateAvailableDiskSpace() {
        Credentials credentials = Credentials.getInstance();
        String partition = credentials.getDestinationDirectory().substring(0,2);

        log.info("Chosen partition : " + partition);

        File file = new File(partition);

        return file.getFreeSpace();
    }


    /**
     * @param numberToConvert number to convert
     * @return A file/partition size and its prefix (Mb / Gb / Tb)
     */
    public List<Object> sizeConverter(long numberToConvert) {
        double adaptedTotalSize;

        String prefixMultipliers;

        List<Object> convertedSize = new ArrayList<>();

        if (numberToConvert < 1e3) {
            adaptedTotalSize = numberToConvert;
            prefixMultipliers = "bytes";
        } else if (numberToConvert < 1e6) {
            adaptedTotalSize = (double) numberToConvert / 1e3;
            prefixMultipliers = "Kb";
        } else if (numberToConvert < 1e9) {
            adaptedTotalSize = (double) numberToConvert / 1e6;
            prefixMultipliers = "Mb";
        } else if (numberToConvert < 1e12) {
            adaptedTotalSize = (double) numberToConvert / 1e9;
            prefixMultipliers = "Gb";
        } else {
            adaptedTotalSize = (double) numberToConvert / 1e12;
            prefixMultipliers = "Tb";
        }

        convertedSize.add(Math.round(adaptedTotalSize * 100.0) / 100.0);
        convertedSize.add(prefixMultipliers);

        return convertedSize;
    }
}
