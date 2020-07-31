package com.lsh.scm.service;

import com.github.pagehelper.PageInfo;
import com.lsh.scm.entity.Pomain;

public interface IPomainService {
    void insert(Pomain pomain);

    void update(Pomain pomain);

    void delete(Long poid);

    PageInfo<Pomain> selectByStatus(Integer page, Integer type, Integer payType);

    public PageInfo<Pomain> selectReport(Integer page, String time);
}
