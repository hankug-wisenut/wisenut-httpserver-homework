package org.example;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

   private ExecutorService executor;
   private ServerSocket serverSocket;
   private InetAddress address;

   private HttpProtocal protocal;
   private Controller controller;

   private Map<String, Socket> socketMap;

    int port;

    public App(HttpProtocal protocal,Controller controller) throws Exception{
        this.address = InetAddress.getLocalHost();
        this.port = 8080;
        this.protocal = protocal;
        this.controller = controller;

        System.out.println("server : "+address.toString() + " : "+port +" start");

        this.serverSocket = new ServerSocket(port,100,address);

        init();
    }

    public App(String host,int port, HttpProtocal protocal,Controller controller) throws Exception{
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.protocal = protocal;
        this.controller = controller;

        System.out.println("server : "+address.toString() + " : "+port +" start");

        this.serverSocket = new ServerSocket(port,100,address);

        init();
    }

    private void init(){
        int cores = Runtime.getRuntime().availableProcessors();
        this.executor =  Executors.newFixedThreadPool(2*cores);
        this.socketMap = new HashMap<>();
    }

    private void close(){
        executor.shutdown();
        socketMap.values().stream().forEach(sock -> {
                try{
                    sock.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }

    public void loop(){

        try{

            while(true){
                Socket socket = this.serverSocket.accept();
                System.out.println("client : "+socket.getRemoteSocketAddress().toString() + " accept");
                socketMap.put(socket.getRemoteSocketAddress().toString(),socket);
                this.executor.execute(new Do(socket,protocal,controller));
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            close();
        }


    }


}
