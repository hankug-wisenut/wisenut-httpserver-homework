package org.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Response {
    private Map<String,String> header;
    private List<byte[]> body;

    public Response(){
        this.header = new HashMap<>();
        this.body = new LinkedList<>();
    }

    public long getBodyLength(){
        long result = 0;
        for(byte[] bytes : this.body){
            result += bytes.length;
        }
        return result;
    }

    public Map<String,String> getHeader(){
        return this.header;
    }
    public void putHeader(String key, String value){
        this.header.put(key,value);
    }
    public void addPacket(byte[] packet){
        this.body.add(packet);
    }

    public List<byte[]> getBody(){
        return this.body;
    }

}
