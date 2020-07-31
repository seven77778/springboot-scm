package com.lsh.scm.dao;

import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.Soitem;

public interface SoitemMapper {
    int insert(Map<String, Object> soitems);

    int deleteBySoId(long soId);

    List<Soitem> selectBySoId(long soId);
}
