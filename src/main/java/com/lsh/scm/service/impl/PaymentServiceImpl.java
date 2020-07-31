package com.lsh.scm.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.ScmConfig;
import com.lsh.scm.dao.PayRecordMapper;
import com.lsh.scm.dao.PomainMapper;
import com.lsh.scm.dao.SomainMapper;
import com.lsh.scm.entity.PayRecord;
import com.lsh.scm.entity.Pomain;
import com.lsh.scm.entity.Somain;
import com.lsh.scm.service.IPaymentService;
import com.lsh.scm.util.DateUtil;

@Service
public class PaymentServiceImpl implements IPaymentService {
    @Autowired
    private PomainMapper pomainMapper;
    @Autowired
    private SomainMapper somainMapper;
    @Autowired
    private PayRecordMapper payRecordMapper;

    @Override
    @Transactional
    public void pay(long poId, String account, String type) {
        // 根据id查询采购单
        Pomain pomain = pomainMapper.selectByPoId(poId);

        PayRecord payRecord = new PayRecord();
        payRecord.setAccount(account);
        payRecord.setOrdercode(String.valueOf(poId));
        payRecord.setPayTime(DateUtil.currentTime());

        HashMap<String, Object> map = new HashMap<>();
        map.put("poId", poId);
        map.put("account", account);
        map.put("now", DateUtil.currentTime());
        int r = 0;
        if ("1".equals(type)) {// 付款
            r = pomainMapper.pay(map);
            payRecord.setPayType(ScmConfig.PAY_MONEY);
            if (ScmConfig.PAYTYPE_PRE_PAY == pomain.getPayType()) {// 预付款到发货
                // 收款金额为尾款
                payRecord.setPayPrice(pomain.getPoTotal() - pomain.getPrePayFee());
            } else {
                payRecord.setPayPrice(pomain.getPoTotal());
            }
        } else if ("2".equals(type)) {// 付预付款
            r = pomainMapper.prepay(map);
            payRecord.setPayType(ScmConfig.PAY_PRE_MONEY);
            payRecord.setPayPrice(pomain.getPrePayFee());

        } else {
            throw new RuntimeException("付款登记类型不匹配：type=" + type);
        }

        if (r != 1) {
            throw new RuntimeException("付款登记操作失败：type=" + type);
        }

        payRecordMapper.insert(payRecord);
    }

    @Override
    @Transactional
    public void rece(Long soId, String account, String type) {
        // 根据id查询销售单
        Somain somain = somainMapper.selectBySoId(soId);

        PayRecord payRecord = new PayRecord();
        payRecord.setAccount(account);
        payRecord.setOrdercode(String.valueOf(soId));
        payRecord.setPayTime(DateUtil.currentTime());

        HashMap<String, Object> map = new HashMap<>();
        map.put("soId", soId);
        map.put("account", account);
        map.put("now", DateUtil.currentTime());
        System.out.println(map + ".........");
        int r = 0;
        if ("1".equals(type)) {// 收款
            r = somainMapper.pay(map);
            payRecord.setPayType(ScmConfig.RECEIVE_MONEY);
            if (ScmConfig.PAYTYPE_PRE_PAY == somain.getPayType()) {// 预付款到发货
                // 收款金额为尾款
                payRecord.setPayPrice(somain.getSoTotal() - somain.getPrePayFee());
            } else {
                payRecord.setPayPrice(somain.getSoTotal());
            }
        } else if ("2".equals(type)) {// 收预付款
            r = somainMapper.prepay(map);
            payRecord.setPayType(ScmConfig.RECEIVE_PRE_MONEY);
            payRecord.setPayPrice(somain.getPrePayFee());

        } else {
            throw new RuntimeException("收款登记类型不匹配：type=" + type);
        }

        if (r != 1) {
            throw new RuntimeException("该销售单无法完成收款，操作失败");
        }

        payRecordMapper.insert(payRecord);
    }

}
