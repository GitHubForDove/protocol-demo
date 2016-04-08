package com.patterncat.rpc.client;

import com.patterncat.rpc.dto.RpcRequest;
import com.patterncat.rpc.dto.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by patterncat on 2016/4/7.
 */
public interface IClient {
    void connect(InetSocketAddress socketAddress);
    public RpcResponse syncSend(RpcRequest request) throws InterruptedException;
    public RpcResponse asyncSend(RpcRequest request,TimeUnit timeUnit,long timeout) throws InterruptedException;
    InetSocketAddress getRemoteAddress();
    void close();
}
