package org.example.utils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonObjectCreator {

    public JsonObjectCreator() {
    }

    //Permet d'ajouter UNIQUEMENT les propriétés "objectId" et "secondaryObjectId" dans un JSONObject
    public JSONObject create(CmisObject object) {

        JSONObject propertiesJson = new JSONObject();

        if (object instanceof Document) {

            Document document = (Document) object;
            String objectId = document.getProperties().get(3).getId();
            String objectIdValue = document.getProperties().get(3).getValueAsString();
            propertiesJson.put(objectId,objectIdValue);

            JSONArray secondaryProperties = new JSONArray();
            for (Object o :  document.getProperties().get(16).getValues()) {
                secondaryProperties.add(o);
            }
            propertiesJson.put(document.getProperties().get(16).getId(), secondaryProperties);

        } else if (object instanceof Folder) {

            Folder folder = (Folder) object;
            String objectId = folder.getProperties().get(6).getId();
            String objectIdValue = folder.getProperties().get(6).getValueAsString();
            propertiesJson.put(objectId,objectIdValue);

            JSONArray secondaryProperties = new JSONArray();
            for (Object o :  folder.getProperties().get(5).getValues()) {
                secondaryProperties.add(o);
            }
            propertiesJson.put(folder.getProperties().get(5).getId(), secondaryProperties);
        }



        return propertiesJson;
    }
}
