package org.example;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.exit;

public class App {
   private ServerSocketChannel serverSocket;
   private InetAddress host;
   private int port;

   private InetSocketAddress address;
   private HttpProtocal protocal;
   private Controller controller;

   private Selector selector;
   private LinkedBlockingQueue<Session> sessions;

   private Thread poller;

    public App(HttpProtocal protocal,Controller controller) throws Exception{
        this.host = InetAddress.getLocalHost();
        this.port = 8080;
        this.address = new InetSocketAddress(host,port);
        this.protocal = protocal;
        this.controller = controller;
        init();
    }

    public App(String host,int port, HttpProtocal protocal,Controller controller) throws Exception{
        this.host = InetAddress.getByName(host);
        this.port = port;
        this.address = new InetSocketAddress(host,port);
        this.protocal = protocal;
        this.controller = controller;
        init();
    }

    private void init(){
        try{
            this.sessions = new LinkedBlockingQueue<>();
            this.selector = Selector.open();
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(this.address);
            this.serverSocket.configureBlocking(false);

            System.out.println("server : "+address.toString() +" start");

            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
            this.poller = new Thread(new Poller(this.sessions));
        }catch(Exception e){
            e.printStackTrace();
            exit(-1);
        }
    }

    private void close(){
        try{
            serverSocket.close();
            selector.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handler(SelectionKey key) throws Exception{
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            if(client != null){
                client.configureBlocking(false);
                this.sessions.put(new Session(client,protocal,controller));
                System.out.println("client : "+client.getRemoteAddress().toString() +" accepted!");
            }

        }
    }

    public void loop(){

        try{
            poller.start();

            while(true){
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                for(SelectionKey key : keys){
                    handler(key);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
    }

    public synchronized void stop(){
        close();
    }

}
