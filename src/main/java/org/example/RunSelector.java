package org.example;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class RunSelector implements Runnable{

    private Selector selector;

    public RunSelector(Selector selector){
        this.selector = selector;
    }


    @Override
    public void run() {
        while(true){
            try{
                System.out.println("run selector");
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();

                for(SelectionKey key : keys){
                    if(key.isReadable()){

                        Session session = (Session) key.attachment();

                        System.out.println("selecting start on"+selector.toString()+" at "+session.getChannel().toString());

                        Request request = session.getProtocal().getRequest(session);

                        System.out.println("-------"+session.getChannel().toString()+"----------");
                        System.out.println(request.toString());
                        System.out.println("----------------------------------------------------");

                        Response response = session.getController().doControl(request);
                        session.getProtocal().sendResponse(session,response);
                        System.out.println("send to "+session.getChannel().getRemoteAddress());
                    }
                }


                int count = 0;
                for(SelectionKey key : selector.keys()){
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    if(!socketChannel.isConnected()){
                        key.cancel();
                        System.out.println(selector.toString()+" key cancel "+key.toString());
                        count +=1;
                    }
                }

                if(count == selector.keys().size()){
                    System.out.println(selector.toString()+" is closed");
                    break;
                }

            }catch(Exception e){
               e.printStackTrace();

            }
        }
    }
}
