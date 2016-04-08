package com.patterncat.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by patterncat on 2016-02-19.
 */
public class XServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("server running");
        while(true){
            Socket socket = server.accept();
            InputStream inputStream = socket.getInputStream();
            XMessage inMsg =XProtocol.decode(inputStream);
            System.out.println("receive from client:"+inMsg);
            OutputStream outputStream = socket.getOutputStream();
            XMessage outMsg = new XMessage();
            outMsg.writeUtf8("got our msg");
            XProtocol.encode(outputStream,outMsg);
            socket.close();
        }
    }
}
