package me.poodle.adbconnector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author pood1e
 */
class AcceptServer {

    private static Socket adb;
    private static final int SIZE = 8192;
    private static ThreadFactory namedThreadFactory = Executors.defaultThreadFactory();
    private static ExecutorService service = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), namedThreadFactory);
    private static ServerSocket server;


    private synchronized static void close() {
        if (adb != null) {
            try {
                adb.close();
                adb = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (server != null) {
            try {
                server.close();
                server = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static void start(Socket a) {
        try {
            if (server == null) {
                server = new ServerSocket(10114);
            }
            adb = server.accept();
            service.execute(() -> {
                try {
                    receive(adb, a.getOutputStream());
                } catch (IOException e) {
                    close();
                }
            });
            service.execute(() -> {
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


}

