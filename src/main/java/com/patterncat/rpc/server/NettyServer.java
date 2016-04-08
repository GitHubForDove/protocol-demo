package com.patterncat.rpc.server;

import com.patterncat.rpc.codec.RpcDecoder;
import com.patterncat.rpc.codec.RpcEncoder;
import com.patterncat.rpc.dto.RpcRequest;
import com.patterncat.rpc.dto.RpcResponse;
import com.patterncat.rpc.exception.ServerStopException;
import com.patterncat.rpc.service.HelloServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by patterncat on 2016/4/6.
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private int ioThreadNum;

    //内核为此套接口排队的最大连接个数，对于给定的监听套接口，内核要维护两个队列，未链接队列和已连接队列大小总和最大值
    private int backlog;

    private int port;

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(int ioThreadNum, int backlog, int port) {
        this.ioThreadNum = ioThreadNum;
        this.backlog = backlog;
        this.port = port;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(this.ioThreadNum);
        final Map<String,Object> demoService = new HashMap<String, Object>();
        demoService.put("com.patterncat.rpc.service.HelloService", new HelloServiceImpl());

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backlog)
                //注意是childOption
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                //真实数据最大字节数为Integer.MAX_VALUE，解码时自动去掉前面四个字节
                                //io.netty.handler.codec.DecoderException: java.lang.IndexOutOfBoundsException: readerIndex(900) + length(176) exceeds writerIndex(1024): UnpooledUnsafeDirectByteBuf(ridx: 900, widx: 1024, cap: 1024)
                                .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast("encoder", new LengthFieldPrepender(4, false))
                                .addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new ServerRpcHandler(demoService));
                    }
                });

        channel = serverBootstrap.bind("127.0.0.1",port).sync().channel();

        logger.info("NettyRPC server listening on port "+ port + " and ready for connections...");

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                //do shutdown staff
            }
        });
    }

    public void stop() {
        if (null == channel) {
            throw new ServerStopException();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
    }
}
