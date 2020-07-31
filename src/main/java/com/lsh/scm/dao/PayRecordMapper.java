package com.lsh.scm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.PayRecord;

public interface PayRecordMapper {
    void insert(PayRecord record);

    HashMap<String, Object> report(String time);

    List<PayRecord> selectRecord(Map map);

    List<HashMap<String, Object>> selectPayDetail(String time);

    List<HashMap<String, Object>> selectReceDetail(String time);
}
