package me.poodle.adbconnector.net;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AcceptServer {
    // server <-> porxy
    private static final int PORT = 10114;
    private static final String ADDRESS = "";
    private static final ExecutorService service = Executors.newSingleThreadExecutor();
    static boolean connecting = true;

    public static void start() {
        service.execute(()-> {
            while (true){
                try {
                    if (connecting){
                        Socket cli = new Socket(ADDRESS, PORT);
                        connecting = false;
                        ProxyServer.addListener(cli);
                    }
                } catch (IOException e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

}
