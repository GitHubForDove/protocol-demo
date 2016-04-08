package com.patterncat.rpc.client;

import com.google.common.base.Optional;
import com.patterncat.rpc.codec.RpcDecoder;
import com.patterncat.rpc.codec.RpcEncoder;
import com.patterncat.rpc.dto.RpcRequest;
import com.patterncat.rpc.dto.RpcResponse;
import com.patterncat.rpc.exception.ClientCloseException;
import com.patterncat.rpc.proxy.RemoteServiceProxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyClient implements IClient {

    private EventLoopGroup workerGroup;
    private Channel channel;

    private int workerGroupThreads;

    private ClientRpcHandler clientRpcHandler;



    public NettyClient(int workerGroupThreads) {
        this.workerGroupThreads = workerGroupThreads;
    }

    public void connect(InetSocketAddress socketAddress) {
        workerGroup = new NioEventLoopGroup(workerGroupThreads);
        clientRpcHandler = new ClientRpcHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                //处理分包传输问题
                                .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast("encoder", new LengthFieldPrepender(4, false))
                                .addLast(new RpcDecoder(RpcResponse.class))
                                .addLast(new RpcEncoder(RpcRequest.class))
                                .addLast(clientRpcHandler);
                    }
                });
        channel = bootstrap.connect(socketAddress.getAddress().getHostAddress(), socketAddress.getPort())
                .syncUninterruptibly()
                .channel();
    }

    public RpcResponse syncSend(RpcRequest request) throws InterruptedException {
        System.out.println("send request:"+request);
        channel.writeAndFlush(request).sync();
        return clientRpcHandler.send(request,null);
    }

    public RpcResponse asyncSend(RpcRequest request,Pair<Long,TimeUnit> timeout) throws InterruptedException {
        channel.writeAndFlush(request);
        return clientRpcHandler.send(request,timeout);
    }

    public <T> T rpcProxy(Class<?> interfaceClass,Pair<Long,TimeUnit> timeout){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},new RemoteServiceProxy(this,timeout));
    }

    public InetSocketAddress getRemoteAddress() {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!(remoteAddress instanceof InetSocketAddress)) {
            throw new RuntimeException("Get remote address error, should be InetSocketAddress");
        }
        return (InetSocketAddress) remoteAddress;
    }

    public void close() {
        if (null == channel) {
            throw new ClientCloseException();
        }
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        workerGroup = null;
        channel = null;
    }
}
