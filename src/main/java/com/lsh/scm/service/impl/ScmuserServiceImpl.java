package com.lsh.scm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.transaction.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.dao.ScmuserMapper;
import com.lsh.scm.dao.UserModelMapper;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.service.IUserService;

@Service
public class ScmuserServiceImpl implements IUserService {

    @Autowired
    private ScmuserMapper scmuserMapper;
    @Autowired
    private UserModelMapper userModelMapper;

    @Override
    @Transactional
    public void save(Scmuser user, String[] modelcodes) {
        scmuserMapper.insertUser(user);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", user.getAccount());
        map.put("modelcodes", modelcodes);
        System.out.println(map);
        userModelMapper.insertUserModel(map);
    }

    @Override
    @Transactional
    public void delete(String account) {
        int umn = userModelMapper.deleteUserModel(account);
        if (umn <= 0) throw new TransactionException("用户权限删除失败");

        int smn = scmuserMapper.deleteUser(account);
        if (smn <= 0) throw new TransactionException("用户删除失败：用户不存在");
    }

    @Override
    @Transactional
    public void update(Scmuser user, String[] modelcodes) {

        int n = scmuserMapper.updateScmuser(user);
        if (n == 0) throw new TransactionException("用户修改失败");
        n = userModelMapper.deleteUserModel(user.getAccount());
//		if(n==0)throw new TransactionException("用户修改失败");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", user.getAccount());
        map.put("modelcodes", modelcodes);
        userModelMapper.insertUserModel(map);

    }

    @Override
    public PageInfo<Scmuser> showPage(Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<Scmuser> users = scmuserMapper.select();
        PageInfo<Scmuser> info = new PageInfo<>(users);
        return info;
    }
}
