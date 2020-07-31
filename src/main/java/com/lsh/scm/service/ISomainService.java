package com.lsh.scm.service;

import com.github.pagehelper.PageInfo;
import com.lsh.scm.entity.Somain;

public interface ISomainService {
    void insert(Somain somain);

    void update(Somain somain);

    void delete(long poid);

    PageInfo<Somain> selectByStatus(Integer page, int type, Integer payType);

    public PageInfo<Somain> selectReport(Integer page, String time);
}
