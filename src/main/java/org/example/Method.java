package org.example;

public enum Method {
    Get("GET"),Post("POST"),Head("HEAD");
    String value;

    Method(String value){
        this.value = value;
    }

    String getValue(){
        return this.value;
    }

}
