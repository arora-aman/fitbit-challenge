package com.aroraaman.fitbitchallenge.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientSocketWrapper {
    public Socket openSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    public InputStream getSocketInputStream(Socket socket) throws IOException {
        return socket.getInputStream();
    }
}
