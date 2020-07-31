package com.lsh.scm.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.annotation.Order;

import com.lsh.scm.entity.Scmuser;

//@Component
@WebFilter(urlPatterns = "/main/*", filterName = "LoginFilter")
@Order(2)
public class LoginFilter implements Filter {


    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse resp = (HttpServletResponse) arg1;
        String token = req.getHeader("token");
        HttpSession session = req.getSession();
        Scmuser user = (Scmuser) session.getAttribute("login");
        String sessionToken = (String) session.getAttribute("token");
        if (user != null && sessionToken != null && sessionToken.equals(token)) {
            arg2.doFilter(arg0, arg1);
        } else {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("text/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            if (token != null && !token.equals(sessionToken))
                out.print("{\"code\": 1,\"message\":\"会话过期，请重新登录\"}");
            else
                out.print("{\"code\": 1,\"message\":\"请先登录\"}");
            out.flush();
            out.close();
        }
    }

}
