package me.poodle.adbconnector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author pood1e
 */
public class ServerTest {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(10115);
        while (true) {
            Socket client = server.accept();
            System.out.println(client.getInetAddress());
            AcceptServer.start(client);
        }
    }
}
