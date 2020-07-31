package com.lsh.scm.dao;

import java.util.List;

import com.lsh.scm.entity.Vender;

public interface VenderMapper {
    void addVender(Vender vender);

    int deleteVender(String venderCode);

    int updateVender(Vender vender);

    List<Vender> select(Vender vender);

    Vender selectByVenderCode(String venderCode);
}
