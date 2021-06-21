package com.lsh.scm.dubbo;

import org.springframework.stereotype.Component;

@Component
public class DubboMock implements MyDubboService {

    @Override
    public String getName() {
        return "this is mock";
    }

    @Override
    public String longTimeget() {
        return "this is mock";
    }
}
