package org.example;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Request {

    private Map<String,String> header;
    private List<byte[]> body;


    public String toString(){
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String,String> e : header.entrySet()){
            result.append(e.getKey()+" : "+e.getValue()+"\n");
        }
        Charset charset = Charset.forName("UTF-8");
        for(byte[] b : body){
            result.append(charset.decode(ByteBuffer.wrap(b)).toString());
        }
        result.append("total-length : "+this.getBodyLength());

        return result.toString();
    }

    public void headerInit(){
        this.header = new HashMap<>();
    }

    public void bodyInit(){
        this.body = new LinkedList<>();
    }
    public Request(){
        this.header = new HashMap<>();
        this.body = new LinkedList<>();
    }

    public void putHeader(String key, String value){
        this.header.put(key,value);
    }

    public String getHeader(String key){
        return this.header.get(key);
    }
    public List<byte[]> getBody(){return this.body;}
    public long getBodyLength(){
        long result = 0;
        for(byte[] bytes : this.body){
            result += bytes.length;
        }
        return result;
    }

    public void addPacket(byte[] packet){
        this.body.add(packet);
    }


}
