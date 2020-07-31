package com.lsh.scm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.Somain;

public interface SomainMapper {
    void insert(Somain somain);

    int deleteBySoId(Long soId);

    int update(Somain somain);

    List<Somain> selectCondition(Map map);

    List<Somain> selectSomain(Map map);

    Somain selectBySoId(Long soId);

    int outstock(Map map);

    int pay(Map map);

    int prepay(Map map);

    int end(Map map);

    HashMap<String, Object> report(String time);
}
