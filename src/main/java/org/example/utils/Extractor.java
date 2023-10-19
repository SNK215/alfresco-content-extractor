package org.example.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;
import org.example.model.Credentials;
import org.json.simple.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

@Getter
@Setter
@SuppressWarnings({"unused", "unchecked"})
public class Extractor {

    private String targetPath;
    private String destinationFolder;
    private int countExtractedFiles;
    private int countExtractedFolders;
    private int countErrors;
    private String tempPathError = "";
    private static final Logger logger = LogManager.getLogger();


    public Extractor() {
    }

    public Extractor(String targetPath, String destinationFolder) {
        this.targetPath = targetPath;
        this.destinationFolder = destinationFolder;
    }

    public void extractFolders(Folder folder) throws IOException {
        try {
            //Extraction des fichiers situés dans le Folder qui est fourni en paramètre
            extractFiles(folder);

            for (CmisObject object : folder.getChildren()) {
                if (object instanceof Folder) {
                    //Objet CmisObject converti en object Folder
                    Folder childFolder = (Folder) object;

                    //Création du dossier
                    File newDir = new File(destinationFolder + "/" + childFolder.getPath());
                    if (!newDir.exists()) {
                        newDir.mkdirs();
                        countExtractedFolders++;
                        logger.info("Directory created : " + newDir.getPath());
                    }
                    tempPathError = newDir.getPath();

                    //Création d'un fichier JSON contenant les propriétés du fichier
                    generateMetadataFile(object, newDir);

                    //Ajout des attributs
                    addAttributesToFile(newDir, childFolder.getCreationDate().getTimeInMillis(), childFolder.getLastModificationDate().getTimeInMillis());

                    //On extrait à nouveau les dossiers et fichiers enfants dans chaque dossier enfant
                    extractFiles(childFolder);
                    extractFolders(childFolder);
                }
            }
        }
        catch (NullPointerException e) {
            countErrors++;
            logger.error("Cannot extract directory : "  + tempPathError);
            for (StackTraceElement s : e.getStackTrace()) logger.error("StackTrace : " + s);
        }
    }

    public void extractFiles(Folder folder) throws IOException {
        //On itère sur chaque enfant dans le dossier qui est fourni en paramètre
        for (CmisObject object : folder.getChildren()) {
            if (object instanceof Document) {
                //Object CmisObject converti en object Document
                Document childDocument = (Document) object;

                //Création du fichier
                File newFile = new File(destinationFolder + childDocument.getPaths().get(0));
                logger.info("File created : " + newFile.getPath());

                //Création d'un fichier JSON contenant les propriétés du fichier
                generateMetadataFile(object, newFile);


                //Insertion du contenu dans le fichier
                InputStream inputStream = childDocument.getContentStream().getStream();
                FileUtils.writeByteArrayToFile(newFile, inputStream.readAllBytes());
                logger.info("Data stream inserted into file : " + newFile.getName());

                //Ajout des attributs
                addAttributesToFile(newFile, childDocument.getCreationDate().getTimeInMillis(), childDocument.getLastModificationDate().getTimeInMillis());

                //Log de confirmation
                if (newFile.exists()) {
                    countExtractedFiles++;
                } else {
                    countErrors++;
                    logger.error("Cannot extract file : "  + newFile.getPath());
                }
            }
        }
    }

    public void addAttributesToFile(File file, long creationDateMs, long lastModifDateMs) throws IOException {
        FileTime creationDateFileTime = FileTime.fromMillis(creationDateMs);
        Files.setAttribute(file.toPath(),"creationTime",creationDateFileTime);
        file.setLastModified(lastModifDateMs);
        logger.info("Metadata file created for : " + file.getName());
    }

    public void generateMetadataFile(CmisObject object, File file) throws IOException {
        Session session = new SessionGenerator().generate(Credentials.getInstance());
        OperationContext oc = session.createOperationContext();
        oc.setIncludeAcls(true);
        oc.setIncludeRelationships(IncludeRelationships.BOTH);

        //Insertion des propriétés dans un JSON
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (Property<?> property : object.getProperties()) {
            JSONObject propertyObject = new JSONObject();
            propertyObject.put("id", property.getId());
            propertyObject.put("displayName", property.getDisplayName());
            propertyObject.put("localName", property.getLocalName());
            propertyObject.put("queryName", property.getQueryName());

            //S'il n'y a pas de valeur pour la propriété, alors on insère un JSON vide
            //Sinon on insère un tableau avec les valeurs, et on retire les [] qui sont insérés automatiquement
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
        logger.info("Properties inserted in JSON File");


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
            logger.info("Permissions inserted in JSON File");
        }

        FileWriter JsonFile = new FileWriter(file.getPath() + "_properties.json");
        JsonFile.write(jsonObject.toJSONString());
        JsonFile.flush();
        logger.info("JSON file created : " + file.getName() + "_properties.json");
    }

}
