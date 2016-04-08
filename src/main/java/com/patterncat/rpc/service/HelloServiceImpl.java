package com.patterncat.rpc.service;

import com.patterncat.rpc.scan.ServiceExporter;

/**
 * Created by patterncat on 2016/4/6.
 */
@ServiceExporter(value = "demoSvr",debugAddress = "127.0.0.1:9090")
public class HelloServiceImpl implements HelloService{
    public String say(String name) {
        return "hi:"+name;
    }
}
