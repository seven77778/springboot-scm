package com.lsh.scm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.Pomain;

public interface PomainMapper {
    void insert(Pomain pomain);

    int deleteByPoId(Long poId);

    int update(Pomain pomain);

    List<Pomain> selectCondition(Map map);

    List<Pomain> selectPomain(Map map);

    Pomain selectByPoId(Long poId);

    int instock(Map map);

    int pay(Map map);

    int prepay(Map map);

    int end(Map map);

    HashMap<String, Object> report(String time);

}
