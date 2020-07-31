package com.lsh.scm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.StockRecord;

public interface StockRecordMapper {
    void insert(Map map);

    void insertCheck(StockRecord record);

    List<StockRecord> selectStockRecordByProductCode(Map map);

    HashMap<String, Object> selectInstockReport(String time);

    HashMap<String, Object> selectOutstockReport(String time);

    List<HashMap<String, Object>> selectProductStockRecord(String time);

    List<HashMap<String, Object>> selectInstockPomain(String time);

    List<HashMap<String, Object>> selectOutstockPomain(String time);
}
