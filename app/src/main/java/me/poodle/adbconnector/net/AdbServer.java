package me.poodle.adbconnector.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class AdbServer {

    private static Socket adb;
    private static final int SIZE = 8192;
    private static ExecutorService readAndWrite = Executors.newFixedThreadPool(2);

    static void start(Socket a) {
        try {
            adb = new Socket("127.0.0.1", 5555);
            readAndWrite.execute(() -> {
                try {
                    receive(adb, a.getOutputStream());
                } catch (IOException e) {
                    close();
                }
            });
            readAndWrite.execute(() -> {
                try {
                    send(adb, a.getInputStream());
                } catch (IOException e) {
                    close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void receive(Socket s, OutputStream out) throws IOException {
        if (s != null && s.isConnected() && !s.isClosed()) {
            InputStream in = s.getInputStream();
            int len;
            byte[] buffer = new byte[SIZE];
            while ((len = in.read(buffer, 0, SIZE)) != -1) {
                out.write(buffer, 0, len);
                out.flush();
            }
            close();
        }
    }

    private static void send(Socket s, InputStream in) throws IOException {
        if (s != null && s.isConnected() && !s.isClosed()) {
            OutputStream out = s.getOutputStream();
            int len;
            byte[] buffer = new byte[SIZE];
            while ((len = in.read(buffer, 0, SIZE)) != -1) {
                out.write(buffer, 0, len);
                out.flush();
            }
            close();
        }
    }

    private synchronized static void close() {
        if (adb != null) {
            try {
                adb.close();
                adb = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ProxyServer.rmListener();
        AcceptServer.connecting = true;
    }

}
