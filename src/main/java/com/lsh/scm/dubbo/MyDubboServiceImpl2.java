package com.lsh.scm.dubbo;

import org.springframework.stereotype.Component;

@Component
public class MyDubboServiceImpl2 implements MyDubboService {
    @Override
    public String getName() {
        return "MyDubboServiceImpl2";
    }

    @Override
    public String longTimeget() {
        return "MyDubboServiceImpl2";
    }
}
