package com.lsh.scm.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.constants.ScmConfig;
import com.lsh.scm.dao.PayRecordMapper;
import com.lsh.scm.entity.PayRecord;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.service.IPaymentService;
import com.lsh.scm.service.IPomainService;

@RestController
@RequestMapping("/main/finance")
public class PaymentController {
    @Autowired
    private IPomainService pomainService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private PayRecordMapper payRecordMapper;


    private ResponseMessage rm = new ResponseMessage();

    @RequestMapping("/pay")
    public ResponseMessage payment(Long poId, String type, Integer payType, Integer page, HttpSession session) {
        Scmuser user = (Scmuser) session.getAttribute("login");
        paymentService.pay(poId, user.getAccount(), type);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("付款成功");
        rm.setData(pomainService.selectByStatus(page, ScmConfig.CAN_PAY, payType));
        return rm;
    }

    @RequestMapping("/receipt")
    public ResponseMessage receipt(Long soId, String type, HttpSession session) {
        Scmuser user = (Scmuser) session.getAttribute("login");
        paymentService.rece(soId, user.getAccount(), type);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("收款成功");
        return rm;
    }

    @RequestMapping("/query")
    public Object queryRecord(String startDate, String endDate, String type, Integer payType, Long no, Integer page) {
        String mainTable = null;
        String field = null;
        if ("收入".equals(type)) {
            mainTable = "somain";
            field = "soId";
        } else if ("支出".equals(type)) {
            mainTable = "pomain";
            field = "poId";
        } else {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("收支类型为收入或支出");
            return rm;
        }
        HashMap<String, Object> m = new HashMap<>();
        m.put("startDate", startDate);
        m.put("endDate", endDate);
        m.put("type", type);
        m.put("no", no);
        m.put("payType", payType);
        m.put("mainTable", mainTable);
        m.put("field", field);
        PageHelper.startPage(page == null ? 1 : page, 10);
        PageInfo<PayRecord> info = new PageInfo<PayRecord>(payRecordMapper.selectRecord(m));
        return info;
    }

}
