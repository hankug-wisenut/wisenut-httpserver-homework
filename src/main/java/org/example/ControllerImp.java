package org.example;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;

import static org.example.Method.*;

public class ControllerImp implements Controller{
    @Override
    public Response doControl(Request request) {

        String method = request.getHeader("Method");

        if(method.equals(Post.getValue())){
            return post(request);
        }else if(method.equals(Get.getValue())){
            return get(request);
        }else if(method.equals(Head.getValue())){
            return header(request);
        }
        request.putHeader("Paths","notSupport");
        return get(request);
    }
    public Response header(Request request){
        Response response = new Response();
        response.putHeader("Content-Type","text/plain");
        return response;
    }
    public Response post(Request request){
        Response response = new Response();
        response.putHeader("Content-Type",request.getHeader("Content-Type"));
        for(byte[] b : response.getBody()){
            response.addPacket(b);
        }
        return response;
    }
    public Response get(Request request){
        String filePath = "/home/hankug/gradleTest/wisenut-httpserver-homework/src/main/asset/html";
        String urlPath = request.getHeader("Paths");
        String htmlPath = filePath+urlPath+".html";

        Response response = new Response();

        File inFile = new File(htmlPath);
        try{
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            StringBuilder result = new StringBuilder();

            String str;
            while ((str = reader.readLine()) != null) {
                result.append(str);
            }
            reader.close();
            response.addPacket(result.toString().getBytes("UTF-8"));
        }catch(Exception e){

            try{
                String errorPath = filePath + "/error.html";
                BufferedReader reader = new BufferedReader(new FileReader(new File(errorPath)));
                StringBuilder result = new StringBuilder();

                String str;
                while ((str = reader.readLine()) != null) {
                    result.append(str);
                }

                reader.close();
                response.addPacket(result.toString().getBytes("UTF-8"));

            }catch(Exception ee){
                ee.printStackTrace();
            }

        }
        response.putHeader("Content-Type","text/html; charset=UTF-8");
        return response;
    }

}
