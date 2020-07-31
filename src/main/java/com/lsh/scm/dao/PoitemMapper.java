package com.lsh.scm.dao;

import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.Poitem;

public interface PoitemMapper {
    int insert(Map<String, Object> poitems);

    int deleteByPoid(long poid);

    List<Poitem> selectByPoId(long poId);
}
