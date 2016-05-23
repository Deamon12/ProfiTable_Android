package supportclasses;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MenuItem implements Serializable {

    private String name = "";
    private JSONObject jsonItem;
    private JSONArray additions = null;


    public MenuItem(JSONObject menuItem){
        jsonItem = menuItem;
        try {
            if(menuItem.has("menuName"))
                name = jsonItem.getString("menuName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        additions = new JSONArray();
    }

    public MenuItem(String name){
        this.name = name;
        additions = new JSONArray();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public JSONObject getJsonItem() throws JSONException {
        if(jsonItem != null) {
            return jsonItem;
        }
        else
            return new JSONObject("JSON NULL");
    }

    public void setAdditions(JSONArray additions){
        this.additions = additions;
    }

    public JSONArray getAdditions(){
        return additions;
    }


}
