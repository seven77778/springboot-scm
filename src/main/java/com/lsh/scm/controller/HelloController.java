package com.lsh.scm.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/scm")
public class HelloController {

    @RequestMapping("/cross")
    public String hello(String lsh,HttpServletRequest request, HttpServletResponse response){
        Cookie[] getC = request.getCookies();
        System.out.println("拿到前端cookie-" + (getC==null));
        Cookie cookie = new Cookie("123-cookie-key","123-cookie-value");
        response.addCookie(cookie);
        return "加了跨域的注解";
    }

    //火狐成功了

    /**
     *
     server {
     listen       9091;
     server_name  127.0.0.1;
     location /scm {
     proxy_pass http://127.0.0.1:5000/scm;
     proxy_cache_methods POST;
     }
     }
     */
    @RequestMapping("/noCross")
    public String noCross(String lsh,HttpServletRequest request, HttpServletResponse response){
        Cookie[] getC = request.getCookies();
        System.out.println("拿到前端cookie-" + (getC==null));
        Cookie cookie = new Cookie("cross_cookie_key","cookie_value");
//        response.addHeader("Set-Cookie","flavor=choco; SameSite=None;Secure");
        response.addCookie(cookie);
        String url = request.getHeader("Origin");
        response.addHeader("Access-Control-Allow-Origin", url);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        return "没加跨域的注解";
    }


    @RequestMapping("/huohu")
    public String huohu(HttpServletRequest request, HttpServletResponse response){
        Cookie[] getC = request.getCookies();
        System.out.println(getC == null);
        Cookie cookie = new Cookie("huohu-cross-123-cookie-key","huohu-cookie-value");
        response.addCookie(cookie);
        String url = request.getHeader("Origin");
        response.addHeader("Access-Control-Allow-Origin", url);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        return "没加跨域的注解";
    }

    @RequestMapping("/ccc")
    @CrossOrigin(origins = "*")
    public String ccc(String lsh,HttpServletRequest request){
        Cookie[] getC = request.getCookies();
        System.out.println("拿到前端cookie-" + (getC==null));
        return "ccc";
    }
}
