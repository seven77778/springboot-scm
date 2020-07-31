package com.lsh.scm.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.ScmuserMapper;
import com.lsh.scm.dao.UserModelMapper;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.UserModel;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.service.IUserService;
import com.lsh.scm.util.DateUtil;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/system")
public class ScmuserController {
    @Autowired
    private ScmuserMapper scmuserMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserModelMapper userModelMapper;

    private ResponseMessage rm;

    public boolean checkUser(Scmuser user, String[] modelcodes) {
        if (StringUtil.isEmpty(user)) {
            rm.setMessage("用户信息不能为空");
            return false;
        }
        String account = user.getAccount();
        if (StringUtil.isEmpty(account)) {
            rm.setMessage("账号不能为空");
            return false;
        }
        String password = user.getPassWord();
        if (StringUtil.isEmpty(password)) {
            rm.setMessage("密码不能为空");
            return false;
        }
        String name = user.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setMessage("姓名不能为空");
            return false;
        }
        String createDate = user.getCreateDate();
        if (StringUtil.isEmpty(createDate)) {
            rm.setMessage("添加日期不能为空");
            return false;
        } else if (!DateUtil.isValid(createDate)) {
            rm.setMessage("日期格式不正确:yyy-MM-dd HH:mm:ss");
            return false;
        }
        Integer status = user.getStatus();
        if (StringUtil.isEmpty(status)) {
            rm.setMessage("锁定状态不能为空");
            return false;
        } else if (!status.toString().matches("[01]")) {
            rm.setMessage("锁定状态必须为数字：1（表示锁定）或0（表示不锁定）");
            return false;
        }

        if (modelcodes == null || modelcodes.length == 0) {
            rm.setMessage("权限不能为空");
            return false;
        }
        return true;
    }

    public boolean checkUpdateUser(Scmuser user, String[] modelcodes) {
        if (StringUtil.isEmpty(user)) {
            rm.setMessage("用户信息不能为空");
            return false;
        }
        String account = user.getAccount();
        if (StringUtil.isEmpty(account)) {
            rm.setMessage("账号不能为空");
            return false;
        }
        String password = user.getPassWord();
        if (StringUtil.isEmpty(password)) {
            rm.setMessage("密码不能为空");
            return false;
        }
        String name = user.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setMessage("姓名不能为空");
            return false;
        }
        Integer status = user.getStatus();
        if (StringUtil.isEmpty(status)) {
            rm.setMessage("锁定状态不能为空");
            return false;
        } else if (!status.toString().matches("[01]")) {
            rm.setMessage("锁定状态必须为数字：1（表示锁定）或0（表示不锁定）");
            return false;
        }

        if (modelcodes == null || modelcodes.length == 0) {
            rm.setMessage("权限不能为空");
            return false;
        }
        return true;
    }

    @RequestMapping("/user/add")
    public ResponseMessage scmuserAdd(Scmuser user, String[] modelcodes) {
        System.out.println(modelcodes);
        rm = new ResponseMessage();
        boolean flag = checkUser(user, modelcodes);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }

        Scmuser u = scmuserMapper.selectByAccount(user.getAccount());
        if (u != null) {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("账号" + user.getAccount() + "已经被注册了");
            return rm;
        }

        userService.save(user, modelcodes);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("添加用户成功");
        return rm;
    }

    @RequestMapping("/user/delete")
    public ResponseMessage delete(String account, Integer page, HttpSession session) {
        rm = new ResponseMessage();
        if (account == null) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("缺少必要的参数：account");
        } else {
            Scmuser user = (Scmuser) session.getAttribute("login");
            if (user == null) {
                rm.setCode(BusinessStatus.SYS_ERROR);
                rm.setMessage("请先登录");
                return rm;
            }
            if (account.equals(user.getAccount())) {
                rm.setCode(BusinessStatus.FAIL);
                rm.setMessage("当前用户正在使用中，不能删除");
                return rm;
            }
            userService.delete(account);
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("删除成功");
            rm.setData(userService.showPage(page));
        }
        return rm;
    }

    @RequestMapping("/user/update")
    public ResponseMessage update(Scmuser user, String[] modelcodes) {
        rm = new ResponseMessage();
        boolean flag = checkUpdateUser(user, modelcodes);
        if (!flag)
            return rm;
        userService.update(user, modelcodes);

        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("修改用户成功");

        return rm;
    }

    @RequestMapping("/user/show")
    public PageInfo<Scmuser> select(Integer page) {
        PageInfo<Scmuser> info = userService.showPage(page);

        return info;
    }

    @RequestMapping("/user/model")
    public ResponseMessage showUserModels(String account) {
        rm = new ResponseMessage();
        if (account == null) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("缺少必要的参数：account");
        } else {
            List<UserModel> userModels = userModelMapper.selectUserModel(account);
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("成功获取用户的权限");
            rm.setData(userModels);
        }
        return rm;
    }
}
