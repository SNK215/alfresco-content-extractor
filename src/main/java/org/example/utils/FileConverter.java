package org.example.utils;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings({"unchecked"})
public class FileConverter {

    private SessionGenerator sessionGenerator = new SessionGenerator();

    public String convertDataToJSONObject(CmisObject object) {
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

        //Insertion des permissions dans un JSON
        Session session = sessionGenerator.generate();
        OperationContext oc = session.createOperationContext();
        oc.setIncludeAcls(true);

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

        return jsonObject.toJSONString();
    }


}

