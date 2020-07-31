package com.lsh.scm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsh.scm.util.GenerateToken;
import com.alibaba.fastjson.JSON;
import com.lsh.scm.constants.AccountStatus;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.ScmuserMapper;
import com.lsh.scm.dao.UserModelMapper;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.UserModel;
import com.lsh.scm.entity.response.ResponseMessage;

@RestController
@RequestMapping("/sys")
public class LoginController {
    @Resource
    private ScmuserMapper scmuserMapper;
    @Resource
    private UserModelMapper userModelMapper;


    @RequestMapping("/login")
    public ResponseMessage login(String username, String password, String role, HttpSession session) {
        ResponseMessage rm = new ResponseMessage();
        System.out.println("登录的用户名" + username + "," + password);
        // 数据校验
        if (username == null || password == null || username.matches("\\s*") || password.matches("\\s*")) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("账号或密码不能为空");
            return rm;
        }

        if ("customer".equals(role)) {
            rm.setCode(BusinessStatus.SYS_ERROR);
            rm.setMessage("暂不支持客户登录");
            return rm;
        }

        // 登录验证
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        map.put("password", password);
        Scmuser user = scmuserMapper.login(map);
        if (user != null) {
            if (user.getStatus() == AccountStatus.UNLOCK) {
                List<UserModel> models = userModelMapper.selectUserModel(username);
                user.setModels(models);
                session.setAttribute("login", user);
                System.out.println(user + "登录成功");
                String token = GenerateToken.geneToken();
                session.setAttribute("token", token);
                rm.setCode(BusinessStatus.SUCCESS);
                rm.setMessage("登录成功");
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("token", token);
                hm.put("user", user);
                rm.setData(hm);
            } else {
                rm.setCode(BusinessStatus.FAIL);
                rm.setMessage("账户被锁定，请联系管理员！");
                System.out.println(user + "登录失败，账号被锁定");
            }
        } else {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("账号或密码错误");
            System.out.println(user + "登录失败，账号密码错误");
        }
        return rm;
    }

    @RequestMapping("/logout")
    public ResponseMessage logout(HttpSession session) {
        session.removeAttribute("login");
        session.removeAttribute("token");
        ResponseMessage rm = new ResponseMessage();
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("退出成功");
        return rm;
    }
}
