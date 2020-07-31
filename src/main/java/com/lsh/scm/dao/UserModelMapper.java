package com.lsh.scm.dao;

import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.UserModel;

public interface UserModelMapper {
    public int insertUserModel(Map map);

    public int deleteUserModel(String account);

    public List<UserModel> selectUserModel(String account);

    public UserModel isAllow(Map map);
}
