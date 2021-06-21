package com.lsh.scm.dubbo;

import org.springframework.stereotype.Service;

@Service("dubboServiceImpl")
public class DubboServiceImpl implements MyDubboService {
    @Override
    public String getName() {
        return "业务耗时0毫秒";
    }

    @Override
    public String longTimeget() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "业务耗时500毫秒";
    }
}
