package org.example;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.exit;

public class Poller implements Runnable{
    private static class SelectorWrap{

        private final Selector selector;
        private long channelNum;
        public SelectorWrap(Selector selector){
            this.selector = selector;
            this.channelNum = 0;
        }

        public Selector getSelector(){
            return this.selector;
        }

        public void addSession(Session session) throws Exception{
            session.getChannel().register(this.selector, SelectionKey.OP_READ,session);
            this.channelNum++;
        }

        public void updateState(){
            for(SelectionKey k : selector.selectedKeys()){
                if(!k.isValid()){
                    this.channelNum--;
                }
            }

        }

        public long getChannelNum(){
            return this.channelNum;
        }


    }
    private ExecutorService executor;
    private PriorityQueue<SelectorWrap> selectors;

    private LinkedBlockingQueue<Session> sessions;
    public Poller(LinkedBlockingQueue<Session> sessions){
        int cores = Runtime.getRuntime().availableProcessors();
        this.executor =  Executors.newFixedThreadPool(10);
        this.selectors = new PriorityQueue<>(Comparator.comparing(SelectorWrap::getChannelNum));
        this.sessions = sessions;

        for(int i=0; i<10; i++){
            try{
                Selector selector = Selector.open();
                SelectorWrap wrap = new SelectorWrap(selector);
                selectors.add(wrap);
            }catch(Exception e){
                exit(-1);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("poller start");
        while(true){
            try{
                Session session = this.sessions.take();
                System.out.println("session-received : "+session.getChannel().toString());
                SelectorWrap wrap = selectors.poll();
                wrap.updateState();
                if(wrap.getChannelNum() == 0){
                    System.out.println("execute : "+session.getChannel().toString());
                    executor.execute(new RunSelector(wrap.getSelector()));
                }
                wrap.addSession(session);
                selectors.add(wrap);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
