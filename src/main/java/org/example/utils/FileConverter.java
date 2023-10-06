package org.example.utils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings({"unchecked"})
public class FileConverter {

    public static String convertDataToJSONObject(CmisObject object) {

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

        return jsonObject.toJSONString();
    }
}

