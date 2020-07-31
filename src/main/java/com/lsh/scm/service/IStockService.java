package com.lsh.scm.service;

import java.util.HashMap;

import com.github.pagehelper.PageInfo;
import com.lsh.scm.entity.CheckStock;

public interface IStockService {
    void instock(long poid, String account);

    void outstock(long soid, String account);

    void checkStock(CheckStock cs, int num);

    PageInfo<HashMap<String, Object>> selectReport(Integer page, String time);

    PageInfo<HashMap<String, Object>> selectInstock(Integer page, String time);

    PageInfo<HashMap<String, Object>> selectOutstock(Integer page, String time);
}
