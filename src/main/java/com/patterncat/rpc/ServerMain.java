package com.patterncat.rpc;

import com.patterncat.rpc.server.NettyServer;

/**
 * Created by patterncat on 2016/4/6.
 */
public class ServerMain {

    public static void main(String args[]) throws InterruptedException {
        NettyServer server = new NettyServer(10,1024,9090);
        server.start();
    }
}
