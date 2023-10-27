package org.example;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Function;

public class HttpProtocalImp implements HttpProtocal {

    public static void parseHead(Scanner scanner, Request request) throws Exception{

        if(scanner.hasNextLine()){
            String[] value = scanner.nextLine().split(" ");
            request.putHeader("Method",value[0]);
            request.putHeader("Paths",value[1]);
            request.putHeader("Version",value[2]);
        }

        while(scanner.hasNextLine()){
            String[] value = scanner.nextLine().split(":");
            if(value.length > 1){
                request.putHeader(value[0],value[1].trim());
            }else{
                break;
            }

        }
    }

    public static void parseBody(Scanner scanner,Request request) throws Exception{

        String[] contentType = request.getHeader("Content-Type").split("; ");
        String binary = contentType[1].split("=")[1];
        request.setBody(ContentParser.parse(contentType[0],binary));
    }

    public static Request requestParser(InputStream in) throws Exception {
        Request request = new Request();
        Scanner scanner = new Scanner(in,"UTF-8");

        parseHead(scanner,request);

        if(request.getHeader("Method").equals("POST")){
            parseBody(scanner,request);
        }

        return request;
    }

    @Override
    public void run(InputStream in, Controller controller) {

        try{
            Request request = HttpProtocalImp.requestParser(in);
        }catch(Exception e){

        }

    }
}
