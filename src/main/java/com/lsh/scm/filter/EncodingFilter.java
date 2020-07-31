package com.lsh.scm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {
        //设置请求和相应的编码格式
        arg0.setCharacterEncoding("UTF-8");
//		arg1.setCharacterEncoding("UTF-8");
        arg2.doFilter(arg0, arg1);
    }

}
