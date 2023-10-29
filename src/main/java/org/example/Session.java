package org.example;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class Session{

    private SocketChannel channel;
    private HttpProtocal protocal;

    private Controller controller;

    private Request request;

    private byte[] remain = null;
    public Session(SocketChannel channel, HttpProtocal protocal, Controller controller){
        this.channel = channel;
        this.protocal = protocal;
        this.controller = controller;
        this.request = new Request();
    }

    public byte[] getRemain(){
        byte[] result = this.remain; this.remain = null;
        return result;
    }

    public void setRemain(byte[] remain){
        this.remain = remain;
    }
    public SocketChannel getChannel(){
        return this.channel;
    }

    public HttpProtocal getProtocal(){
        return this.protocal;
    }

    public Controller getController(){
        return this.controller;
    }

    public Request getRequest(){return this.request;}

}
