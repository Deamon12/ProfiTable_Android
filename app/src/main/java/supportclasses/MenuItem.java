package supportclasses;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuItem {

    private String name;
    private JSONObject jsonItem;
    private ArrayList<String> attributes;


    public MenuItem(JSONObject menuItem){
        jsonItem = menuItem;
        attributes = new ArrayList<>();
    }

    public MenuItem(String name){
        this.name = name;
        attributes = new ArrayList<>();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public JSONObject getJsonItem() throws JSONException {
        if(jsonItem != null)
            return jsonItem;
        else
            return new JSONObject("JSON NULL");
    }

}
