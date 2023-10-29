package org.example;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static org.example.Method.*;

public class HttpProtocalImp implements HttpProtocal {

    public static void readHeader(Session session, String charSetName) throws Exception{

        SocketChannel channel = session.getChannel();

        System.out.println("parsing header : "+channel.toString());

        byte[] array = new byte[4096];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Charset charset = Charset.forName(charSetName);

        List<String> result = new LinkedList<>();
        byte[] preRemain = session.getRemain();
        String remain = "";

        if(preRemain != null){
            ByteBuffer remainByte = ByteBuffer.wrap(preRemain).position(0).limit(preRemain.length);
            remain = charset.decode(remainByte).toString();
        }

        boolean headerEnd = false;

        while(channel.read(buffer) >=0 && !headerEnd){
            buffer.flip();
            CharBuffer charBuffer = charset.decode(buffer);
            int start = 0;
            while(charBuffer.position() < charBuffer.limit()){
                if(charBuffer.get() == '\n'){
                  String line =  remain + charBuffer.slice(start,charBuffer.position()-start-1).toString().trim();

                  result.add(line);
                  start = charBuffer.position();
                  remain = "";

                  if(line.equals("")){
                      headerEnd = true;
                      break;
                  }

                }
            }
            if(start < charBuffer.limit() && headerEnd){
                session.setRemain(charset.encode(charBuffer.slice(start,charBuffer.limit()-start)).array());
            }else{
                remain = charBuffer.slice(start,charBuffer.limit()-start).toString();
            }
        }

        Request request = session.getRequest();
        request.headerInit();

        int count = 0;
        for(String s : result){

            if(count == 0){
                String[] value = s.split(" ");
                request.putHeader("Method",value[0]);
                request.putHeader("Paths",value[1]);
                request.putHeader("Version",value[2]);
            }else{
                if(s.indexOf(':') > 0){
                    String[] value = s.split(":");
                    request.putHeader(value[0],value[1].trim());
                }
            }
            count +=1;
        }

    }

    public static void readBody(Session session){
        Request request = session.getRequest();
        request.bodyInit();
        Charset charset = Charset.forName("UTF-8");
        byte[] preRemain = session.getRemain();
        if(preRemain != null){
            request.addPacket(preRemain);
        }

        String method = request.getHeader("Method");

        SocketChannel channel = session.getChannel();

        if(method.equals(Post.getValue())){
          long contentLength = Long.valueOf(request.getHeader("Content-Length"));
          long currentLength = request.getBodyLength();
          long remain = contentLength - currentLength;

          System.out.println("total : "+contentLength +" remain : "+ remain);

            byte[] array = new byte[4096];
            ByteBuffer buffer = ByteBuffer.wrap(array);
            while(remain > 0){
                try{
                    channel.read(buffer);
                    buffer.flip();
                    if(remain >= buffer.limit()){
                        byte[] bytes = new byte[buffer.limit()];
                        for(int i=0; i<buffer.limit(); i++){
                            bytes[i] = buffer.get();
                        }
                        request.addPacket(bytes);
                    }else{
                        byte[] bytes = new byte[(int) remain];
                        byte[] remainBytes = new byte[(int)(buffer.limit() - remain)];
                        for(int i=0; i<(int) remain; i++){
                            bytes[i] = buffer.get();
                        }
                        for(int i= (int) remain; i<buffer.limit(); i++){
                            remainBytes[i-(int)remain] = buffer.get();
                        }
                        request.addPacket(bytes);
                        if(remainBytes.length > 0){
                            session.setRemain(remainBytes);
                        }

                    }
                    remain -= buffer.limit();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
          System.out.println("body-length : "+request.getBodyLength());
        }
    }

    @Override
    public Request getRequest(Session session) {
        try{
            readHeader(session,"UTF-8");
            readBody(session);

            Request request = session.getRequest();
            return request;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void sendResponse(Session session, Response response) {

        try{
            response.putHeader("Content-Length",String.valueOf(response.getBodyLength()));

            SocketChannel channel = session.getChannel();
            List<byte[]> bytes = new LinkedList<>();

            String first = "HTTP/1.1 200 ok\n";

            bytes.add(first.getBytes("UTF-8"));

            for(Map.Entry<String,String> e : response.getHeader().entrySet()){
                String headerParam = e.getKey()+": "+e.getValue()+"\n";
                bytes.add(headerParam.getBytes("UTF-8"));

            }
            String blank = "\r\n";
            bytes.add(blank.getBytes("UTF-8"));

            bytes.addAll(response.getBody());

            int length  = bytes.stream().mapToInt(b->b.length).sum();
            byte[] result = new byte[length];

            ByteBuffer buffer = ByteBuffer.wrap(result);
            for(byte[] b : bytes){
                buffer.put(b);
            }
            buffer.flip();

            channel.write(buffer);

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
