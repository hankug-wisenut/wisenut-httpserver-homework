package org.example;

import java.net.Socket;


public class Do implements Runnable{

    private Socket socket;
    private HttpProtocal protocal;

    private Controller controller;
    public Do(Socket socket,HttpProtocal protocal,Controller controller){
        this.socket = socket;
        this.protocal = protocal;
        this.controller = controller;
    }

    public void run(){
        System.out.println("client : "+socket.getRemoteSocketAddress().toString() + " start do");

    }
}
