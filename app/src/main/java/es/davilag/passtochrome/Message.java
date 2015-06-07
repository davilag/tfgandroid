package es.davilag.passtochrome;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by davilag on 22/10/14.
 */
public class Message {
    private HashMap<String,Object> data;

    public Message(){
        data = new HashMap<String,Object>();
    }


    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addData(String key, Object value){
        this.data.put(key, value);
    }

    public Set<String> keySet(){
        return this.data.keySet();
    }

    public Object value(Object key){
        return this.data.get(key);
    }


    public HashMap<String, Object> getData() {
        return data;
    }
}