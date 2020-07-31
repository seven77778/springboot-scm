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
import com.lsh.scm.dao.ProductMapper;
import com.lsh.scm.dao.StockRecordMapper;
import com.lsh.scm.entity.CheckStock;
import com.lsh.scm.entity.Pomain;
import com.lsh.scm.entity.Product;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.Somain;
import com.lsh.scm.entity.StockRecord;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.service.IPomainService;
import com.lsh.scm.service.ISomainService;
import com.lsh.scm.service.IStockService;
import com.lsh.scm.util.DateUtil;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/stock")
public class StockController {
    private ResponseMessage rm = new ResponseMessage();

    @Autowired
    private IStockService stockService;

    @Autowired
    private IPomainService pomainService;

    @Autowired
    private ISomainService somainService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @RequestMapping("/instock")
    public ResponseMessage instock(Long poId, Integer page, Integer payType, HttpSession session) {
        Scmuser user = (Scmuser) session.getAttribute("login");
        if (StringUtil.isEmpty(poId)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("入库的采购单编号poId参数：不能为空");
            return rm;
        }
        if (StringUtil.isEmpty(payType)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("入库的采购单付款方式payType参数：不能为空");
            return rm;
        }
        stockService.instock(poId, user.getAccount());
        PageInfo<Pomain> info = pomainService.selectByStatus(page, ScmConfig.CAN_STOCK, payType);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("入库成功");
        rm.setData(info);
        return rm;
    }

    @RequestMapping("/outstock")
    public ResponseMessage outstock(Long soId, Integer page, Integer payType, HttpSession session) {
        if (StringUtil.isEmpty(soId)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("出库的销售单编号soId参数：不能为空");
            return rm;
        }
        if (StringUtil.isEmpty(payType)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("出库的销售单付款方式payType参数：不能为空");
            return rm;
        }
        Scmuser user = (Scmuser) session.getAttribute("login");
        stockService.outstock(soId, user.getAccount());
        PageInfo<Somain> info = somainService.selectByStatus(page, ScmConfig.CAN_STOCK, payType);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("出库成功");
        rm.setData(info);
        return rm;
    }

    @RequestMapping("/checkstock")
    public ResponseMessage checkStock(CheckStock cs, Integer num, HttpSession session) {
        if (StringUtil.isEmpty(cs.getProductCode())) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品编号productCode参数：不能为空");
            return rm;
        }
        Integer originNum = cs.getOriginNum();
        if (StringUtil.isEmpty(originNum)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品原始数量originNum参数：不能为空");
            return rm;
        }
        String type = cs.getType();
        if (StringUtil.isEmpty(type)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品损益类型type参数：不能为空");
            return rm;
        }
        if (StringUtil.isEmpty(cs.getDescription())) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品损益原因description参数：不能为空");
            return rm;
        }
        if (StringUtil.isEmpty(num)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品变化数量num参数：不能为空或0");
            return rm;
        }
        Scmuser user = (Scmuser) session.getAttribute("login");
        num = cs.getType().equals("损耗") ? -num : num;
        cs.setRealNum(originNum + num);
        cs.setCreateUser(user.getAccount());
        cs.setStockTime(DateUtil.currentTime());
        stockService.checkStock(cs, num);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("盘点成功");
        return rm;
    }

    @RequestMapping("/query")
    public PageInfo<Product> queryStock(String productCode, String name, Integer min, Integer max, Integer page) {
        HashMap<String, Object> m = new HashMap<>();
        m.put("productCode", productCode);
        m.put("name", name);
        m.put("min", min);
        m.put("max", max);
        PageHelper.startPage(page == null ? 1 : page, 10);
        PageInfo<Product> info = new PageInfo<>(productMapper.selectStock(m));
//		System.out.println(m);
        return info;
    }

    @RequestMapping("/alterRecord")
    public ResponseMessage queryAlterRecord(String productCode, Integer stockType, Integer page) {
        if (StringUtil.isEmpty(productCode)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品编号productCode参数：不能为空");
            return rm;
        }
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        m.put("stockType", stockType);
        m.put("productCode", productCode);
        PageInfo<StockRecord> info = new PageInfo<>(stockRecordMapper.selectStockRecordByProductCode(m));
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("查询产品库存变更记录成功");

        rm.setData(info);
        return rm;
    }
}
