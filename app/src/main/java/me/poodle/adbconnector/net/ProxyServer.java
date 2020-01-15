package me.poodle.adbconnector.net;

import java.io.IOException;
import java.net.Socket;

class ProxyServer {

    private static Socket listener;

    static void addListener(Socket s) {
        AdbServer.start(s);
        listener = s;
    }

    synchronized static void rmListener() {
        if (listener != null) {
            try {
                listener.close();
                listener = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
