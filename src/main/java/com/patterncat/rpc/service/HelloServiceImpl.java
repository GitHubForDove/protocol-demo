package com.patterncat.rpc.service;

/**
 * Created by patterncat on 2016/4/6.
 */
public class HelloServiceImpl implements HelloService{
    public String say(String name) {
        return "hi:"+name;
    }
}
