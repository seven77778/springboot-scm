package com.lsh.scm.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.lsh.scm.dao.UserModelMapper;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.UserModel;

//@Component
@WebFilter(urlPatterns = "/main/*", filterName = "PermissionFilter")
@Order(3)
public class PermissionFilter implements Filter {
    @Autowired
    private UserModelMapper userModelMapper;

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        String uri = req.getRequestURI().substring(1);
        // 取剩下 路径的前两级目录
        String[] str = uri.split("/");
        StringBuilder modelUri = new StringBuilder("/");
        if (str.length >= 2) {
            modelUri.append(str[0]).append("/").append(str[1]);
            Scmuser user = (Scmuser) req.getSession().getAttribute("login");
            HashMap<String, Object> map = new HashMap<>();
            map.put("account", user.getAccount());
            map.put("modelUri", modelUri.toString());
            UserModel um = userModelMapper.isAllow(map);
            System.out.println("9999" + map + um);
            if (um != null) {
                arg2.doFilter(arg0, arg1);
                return;
            }
            arg1.setCharacterEncoding("utf-8");
            arg1.setContentType("text/json;charset=utf-8");
            PrintWriter out = arg1.getWriter();
            out.print("{\"status\": 1, \"statusInfo\": \"您当前没有权限访问，请联系管理员\"");
            System.out.println("{\"status\": 1, \"statusInfo\": \"您当前没有权限访问，请联系管理员\"");
            out.flush();
            out.close();
        } else {
            arg2.doFilter(arg0, arg1);
        }

    }

}
