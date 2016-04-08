package com.patterncat.rpc;

import com.patterncat.rpc.client.NettyClient;
import com.patterncat.rpc.dto.RpcRequest;
import com.patterncat.rpc.dto.RpcResponse;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by patterncat on 2016/4/7.
 */
public class ClientMain {
    public static void main(String args[]) throws InterruptedException, ExecutionException {
        final NettyClient client = new NettyClient(5/*worker group threads*/);
        client.connect(new InetSocketAddress("127.0.0.1",9090));


//        for(int i=0;i<3;i++){
//            RpcRequest request = new RpcRequest();
//            request.setTraceId(UUID.randomUUID().toString());
//            request.setAppName("client" + i);
//            request.setClassName("com.patterncat.service.HelloService");
//            request.setMethodName("say");
//            request.setParameters(new Object[]{"from client " + i});
//            request.setParameterTypes(new Class[]{String.class});
//            client.syncSend(request);
//        }

        ExecutorService pool = Executors.newFixedThreadPool(5);
        int batch = 100;
        List<Callable<Object>> callables = new ArrayList<Callable<Object>>(batch);
        for(int i=0;i<batch;i++){
            final int idx = i;
            callables.add(new Callable<Object>() {
                public Object call() throws Exception {
                    RpcRequest request = new RpcRequest();
                    request.setTraceId(UUID.randomUUID().toString());
                    request.setAppName("client" + idx);
                    request.setClassName("com.patterncat.service.HelloService");
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
        pool.awaitTermination(10,TimeUnit.SECONDS);
        client.close();
    }
}
