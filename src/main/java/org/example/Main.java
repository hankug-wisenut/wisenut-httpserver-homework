package org.example;

import sun.misc.Signal;
import sun.misc.SignalHandler;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws Exception{

        App app = new App(new HttpProtocalImp(),new ControllerImp());
        /*
        SignalHandler signalHandler = new SignalHandler() {
            @Override
            public void handle(Signal sig) {
                app.stop();
                System.exit(0);
            }
        };

        Signal.handle(new Signal("INT"),signalHandler);
        Signal.handle(new Signal("TERM"),signalHandler);
        Signal.handle(new Signal("HUP"),signalHandler);

        */
        app.loop();
    }
}