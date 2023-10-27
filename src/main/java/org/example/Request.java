package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private Map<String,String> header;
    private Object body;

    public Request(){
        this.header = new HashMap<>();
    }

    public void putHeader(String key, String value){
        this.header.put(key,value);
    }

    public String getHeader(String key){
        return this.header.get(key);
    }

    public void setBody(Object body){
        this.body = body;
    }


}
