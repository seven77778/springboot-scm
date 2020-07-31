package com.lsh.scm.dao;

import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.Scmuser;

public interface ScmuserMapper {
    public void insertUser(Scmuser user);

    public int deleteUser(String account);

    public Scmuser selectByAccount(String account);

    public int updateScmuser(Scmuser user);

    public Scmuser login(Map<String, Object> map);

    public List<Scmuser> select();

}
