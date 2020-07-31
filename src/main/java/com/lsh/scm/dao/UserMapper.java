package com.lsh.scm.dao;

import org.apache.ibatis.annotations.Insert;

import com.lsh.scm.entity.UserEntity;

public interface UserMapper {
    @Insert("INSERT INTO user(id,name,sex,address,zip) VALUES(#{id},#{name},#{sex},#{address},#{zip})")
    void insert(UserEntity user);
}
