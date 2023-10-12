package org.example.utils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TotalSizeCalculator {
    private Session session;
    private long fileCount;
    private long folderCount;
    protected static final Logger logger = LogManager.getLogger();

    public TotalSizeCalculator() {
        session = new SessionGenerator().generate();
        String rootId = findRootNodeId(session);
        if (rootId!=null) {
            long totalSize = calculateTotalSizeAndCount(session, rootId);
            System.out.println("La taille totale de tous les fichiers et dossiers est : " + totalSize + " octets");
            System.out.println("Nombre total de dossiers : " + folderCount);
            //Compte de fichier à revoir
            System.out.println("Nombre total de fichiers 1 : " + fileCount);
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
        long totalSize = 0;
        ItemIterable<CmisObject> children = rootFolder.getChildren();
        for (CmisObject child : children) {
            if (child instanceof Document) {
                Document document = (Document) child;
                totalSize += document.getContentStreamLength();
                fileCount++;
            } else if (child instanceof Folder) {
                Folder folder = (Folder) child;
                totalSize += calculateTotalSizeAndCount(session, folder.getId());
                folderCount++;
            }
        }
        return totalSize;
    }
}
