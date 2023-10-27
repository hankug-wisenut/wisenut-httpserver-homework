package org.example;

public class ContentParser {

    public static Object parse(String contentType, String binary){
        if(contentType.equals("multipart/form-data")){

        }else if(contentType.equals("text/plain")){

        }else if(contentType.equals("Application/json")){

        }

        return null;
    }

}
