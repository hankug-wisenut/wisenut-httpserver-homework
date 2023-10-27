package org.example;

public enum Method {
    Get("get"),Post("post"),Head("head");
    String value;

    Method(String value){
        this.value = value;
    }

    String getValue(){
        return this.value;
    }

}
