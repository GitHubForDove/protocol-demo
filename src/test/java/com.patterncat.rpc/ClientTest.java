package com.patterncat.rpc;

import com.patterncat.rpc.client.NettyClient;
import com.patterncat.rpc.dto.RpcRequest;
import com.patterncat.rpc.dto.RpcResponse;
import com.patterncat.rpc.server.NettyServer;
import com.patterncat.rpc.service.HelloService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by patterncat on 2016/4/8.
 */
public class ClientTest {

    NettyClient client = null;

    @Before
    public void setup() throws InterruptedException {
        client = new NettyClient(5/*worker group threads*/);
        client.connect(new InetSocketAddress("127.0.0.1",9090));
    }

    @After
    public void destroy(){
        client.close();
    }

    @Test
    public void proxy(){
        HelloService helloService = client.rpcProxy(HelloService.class, Pair.of(500L, TimeUnit.MILLISECONDS));
        System.out.println(helloService.say("proxy demo"));
    }

    @Test
    public void bench() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(50);
        int batch = 1000;
        List<Callable<Object>> callables = new ArrayList<Callable<Object>>(batch);
        for(int i=0;i<batch;i++){
            final int idx = i;
            callables.add(new Callable<Object>() {
                public Object call() throws Exception {
                    RpcRequest request = new RpcRequest();
                    request.setTraceId(UUID.randomUUID().toString());
                    request.setAppName("client" + idx);
                    request.setClassName("com.patterncat.rpc.service.HelloService");
                    request.setMethodName("say");
                    request.setParameters(new Object[]{"from client " + idx});
                    request.setParameterTypes(new Class[]{String.class});
                    RpcResponse response = client.syncSend(request);
                    if(response == null){
                        return null;
                    }
                    return response.getResult();
                }
            });
        }

        List<Future<Object>> futures = pool.invokeAll(callables);
        for(Future<Object> future:futures){
            System.out.println(future.get());
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
