package com.lsh.scm.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.PayRecordMapper;
import com.lsh.scm.dao.PomainMapper;
import com.lsh.scm.dao.ProductMapper;
import com.lsh.scm.dao.SomainMapper;
import com.lsh.scm.dao.StockRecordMapper;
import com.lsh.scm.entity.Pomain;
import com.lsh.scm.entity.Somain;
import com.lsh.scm.service.IPomainService;
import com.lsh.scm.service.ISomainService;
import com.lsh.scm.service.IStockService;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/report")
public class ReportController {
    private HashMap<String, Object> result = new HashMap<>();

    @Autowired
    private PomainMapper pomainMapper;
    @Autowired
    private SomainMapper somainMapper;
    @Autowired
    private IPomainService pomainService;
    @Autowired
    private ISomainService somainService;

    @Autowired
    private PayRecordMapper payRecordMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private IStockService stockService;

    public boolean checkTime(String time) {
        if (StringUtil.isEmpty(time)) {
            result.put("status", BusinessStatus.PRAMA_ERROR);
            result.put("statusInfo", "查询的time参数:不能为空");
            return false;
        } else if (!time.matches("^[1-9]\\d{3}-\\d{2}$")) {
            result.put("status", BusinessStatus.PRAMA_ERROR);
            result.put("statusInfo", "查询的time参数:日期格式不正确");
            return false;
        }
        return true;
    }

    @RequestMapping("/pomain/main")
    public HashMap<String, Object> report_pomain(String time, Integer page) {
        if (!checkTime(time))
            return result;

        result = pomainMapper.report(time);
        PageInfo<Pomain> list = pomainService.selectReport(page == null ? 1 : page, time);
        result.put("details", list);

        return result;
    }

    @RequestMapping("/somain/main")
    public HashMap<String, Object> report_somain(String time, Integer page) {

        if (!checkTime(time))
            return result;

        result = somainMapper.report(time);
        PageInfo<Somain> list = somainService.selectReport(page == null ? 1 : page, time);
        result.put("details", list);

        return result;
    }

    @RequestMapping("/payment/main")
    public HashMap<String, Object> report_payment(String time, Integer page) {
        if (!checkTime(time))
            return result;

        result = payRecordMapper.report(time);


        return result;
    }

    @RequestMapping("/payment/detail/pay")
    public PageInfo<HashMap<String, Object>> report_payment_pay(String time, Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);

        List<HashMap<String, Object>> payDetail = payRecordMapper.selectPayDetail(time);
        PageInfo<HashMap<String, Object>> info = new PageInfo<>(payDetail);
        return info;
    }

    @RequestMapping("/payment/detail/receipt")
    public PageInfo<HashMap<String, Object>> report_payment_rece(String time, Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);

        List<HashMap<String, Object>> payDetail = payRecordMapper.selectReceDetail(time);
        PageInfo<HashMap<String, Object>> info = new PageInfo<>(payDetail);
        return info;
    }

    @RequestMapping("/instock/main")
    public HashMap<String, Object> report_instock(String time, Integer page) {

        if (!checkTime(time))
            return result;
        result = stockRecordMapper.selectInstockReport(time);
        PageInfo<HashMap<String, Object>> info = stockService.selectInstock(page, time);
        result.put("details", info);
        return result;
    }

    @RequestMapping("/outstock/main")
    public HashMap<String, Object> report_outstock(String time, Integer page) {

        if (!checkTime(time))
            return result;
        result = stockRecordMapper.selectOutstockReport(time);
        PageInfo<HashMap<String, Object>> info = stockService.selectOutstock(page, time);
        result.put("details", info);

        return result;
    }

    @RequestMapping("/stock/main")
    public HashMap<String, Object> report_stock(String time, Integer page) {
        if (!checkTime(time))
            return result;
        result = productMapper.selectTotalNum(time);
        PageInfo<HashMap<String, Object>> list = stockService.selectReport(page == null ? 1 : page, time);
        result.put("details", list);

        return result;
    }

}
