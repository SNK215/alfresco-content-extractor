package org.example.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.commons.io.FileUtils;
import org.example.model.Credentials;
import org.json.simple.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

/**
 * Class that will extract the content of the Alfresco repository and inject it on the users device
 */
@Getter
@Setter
@Log4j2
@SuppressWarnings({"unused", "unchecked"})
public class Extractor {

    /**
     * Where the extracted data will be injected
     */
    private String destinationFolder;

    /**
     * Counts the number of extracted files
     */
    private int countExtractedFiles;

    /**
     * Counts the number of extracted folders
     */
    private int countExtractedFolders;

    /**
     * Counts the number of files/folders that couldn't be extracted
     */
    private int countErrors;

    /**
     * Stores the path of the current file/folder being extracted, in order to log it in case an error occurs
     */
    private String tempPathError = "";


    public Extractor(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    /**
     * Extracts all subfolders from a parent folder
     * @param folder parent folder
     * @throws IOException in case there is a problem with local folder creation
     */
    public void extractFolders(Folder folder) throws IOException {
        try {

            extractFiles(folder);

            for (CmisObject object : folder.getChildren()) {

                if (object instanceof Folder) {

                    //Mapping CmisObject to Folder in order to have access to specific methods
                    Folder childFolder = (Folder) object;

                    //Local folder creation
                    File newDir = new File(destinationFolder + "/" + childFolder.getPath());
                    if (!newDir.exists()) {
                        newDir.mkdirs();
                        countExtractedFolders++;
                        log.info("Directory created : " + newDir.getPath());
                    }
                    tempPathError = newDir.getPath();

                    generateMetadataFile(object, newDir.getName());

                    addAttributesToFile(newDir, childFolder.getCreationDate().getTimeInMillis(), childFolder.getLastModificationDate().getTimeInMillis());

                    extractFiles(childFolder);
                    extractFolders(childFolder);
                }
            }
        }
        catch (NullPointerException e) {
            countErrors++;
            log.error("Cannot extract directory : "  + tempPathError);
            for (StackTraceElement s : e.getStackTrace()) {
                log.error("StackTrace : " + s);
            }
        }
    }

    /**
     * Extracts all files from a parent folder
     * @param folder parent folder
     * @throws IOException in case there is a problem with local file creation
     */
    public void extractFiles(Folder folder) throws IOException {

        for (CmisObject object : folder.getChildren()) {

            if (object instanceof Document) {

                //Mapping CmisObject to Document in order to have access to specific methods
                Document childDocument = (Document) object;

                //Local file creation
                File newFile = new File(destinationFolder + childDocument.getPaths().get(0));
                log.info("File created : " + newFile.getPath());

                generateMetadataFile(object, newFile.getName());

                //Inserting content into the new file
                InputStream inputStream = childDocument.getContentStream().getStream();
                FileUtils.writeByteArrayToFile(newFile, inputStream.readAllBytes());
                log.info("Data stream inserted into file : " + newFile.getName());

                addAttributesToFile(newFile, childDocument.getCreationDate().getTimeInMillis(), childDocument.getLastModificationDate().getTimeInMillis());

                if (newFile.exists()) {
                    countExtractedFiles++;
                } else {
                    countErrors++;
                    log.error("Cannot extract file : "  + newFile.getPath());
                }
            }
        }
    }

    /**
     * Method that adds attributes to a given file
     * @param file file that needs its attributes to be modified
     * @param creationDateMs creation date in milliseconds
     * @param lastModifDateMs last modification date in milliseconds
     * @throws IOException in case there is a problem with local file manipulation
     */
    public void addAttributesToFile(File file, long creationDateMs, long lastModifDateMs) throws IOException {
        FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
        Files.setAttribute(file.toPath(),"creationTime",creationDateFileTime);
        file.setLastModified(lastModifDateMs);
        log.info("attributes added to : " + file.getName());
    }

    /**
     * @param object CmisObject from which the metadata will be extracted
     * @param filename Used for the name of the metadata file (name : filename_properties.json)
     * @throws IOException in case there is a problem with local file creation
     */
    public void generateMetadataFile(CmisObject object, String filename) throws IOException {

        //Generating a new session in order to get the ACLs (permissions)
        Session session = new SessionGenerator().generate(Credentials.getInstance());
        OperationContext oc = session.createOperationContext();
        oc.setIncludeAcls(true);
        oc.setIncludeRelationships(IncludeRelationships.BOTH);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        //Creating a new JSON entry for each property of the CmisObject
        for (Property<?> property : object.getProperties()) {
            JSONObject propertyObject = new JSONObject();
            propertyObject.put("id", property.getId());
            propertyObject.put("displayName", property.getDisplayName());
            propertyObject.put("localName", property.getLocalName());
            propertyObject.put("queryName", property.getQueryName());

            //Creating empty Json object if there is no value for a property, and replacing the "[]" that are automatically inserted
            if (property.getValues().isEmpty()) {
                propertyObject.put("values", new JSONObject());
            } else {
                JSONArray valuesArray = new JSONArray();
                for (Object value : property.getValues()) {
                    valuesArray.add(value.toString().replaceAll("\\[\\]",""));
                }
                propertyObject.put("values", valuesArray);
            }

            jsonArray.add(propertyObject);
        }

        jsonObject.put("properties", jsonArray);

        //Inserting Aces/Acls (permissions) in the JSON file
        
        JSONArray jsonArrayAce = new JSONArray();

        if (object instanceof Document) {

            Document document = (Document) object;

            for (Ace ace : session.getObjectByPath(document.getPaths().get(0),oc).getAcl().getAces()) {
                JSONObject jsonObjectAce = new JSONObject();
                jsonObjectAce.put("principalId",ace.getPrincipalId());
                jsonObjectAce.put("permission", ace.getPermissions().get(0));

                jsonArrayAce.add(jsonObjectAce);
            }

            jsonObject.put("permissions",jsonArrayAce);
        }

        //Local file creation
        FileWriter JsonFile = new FileWriter(filename + "_properties.json");
        JsonFile.write(jsonObject.toJSONString());
        JsonFile.flush();

        log.info("Metadata file created : " + filename + "_properties.json");
    }

}
