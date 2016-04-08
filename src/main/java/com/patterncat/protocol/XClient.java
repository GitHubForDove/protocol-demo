package com.patterncat.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by patterncat on 2016-02-19.
 */
public class XClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",9090);
        OutputStream out = socket.getOutputStream();
        XMessage outMsg = new XMessage();
        outMsg.writeUtf8("hello server");
        XProtocol.encode(out,outMsg);
        InputStream inputStream = socket.getInputStream();
        XMessage inMsg = XProtocol.decode(inputStream);
        System.out.println("receive from server:"+inMsg);
    }
}
