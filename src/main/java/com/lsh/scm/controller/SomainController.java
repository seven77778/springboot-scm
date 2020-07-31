package com.lsh.scm.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.transaction.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.constants.ScmConfig;
import com.lsh.scm.dao.SoitemMapper;
import com.lsh.scm.dao.SomainMapper;
import com.lsh.scm.entity.Soitem;
import com.lsh.scm.entity.Somain;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.service.ISomainService;
import com.lsh.scm.util.DateUtil;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/sell")
public class SomainController {
    private ResponseMessage rm = new ResponseMessage();

    @Autowired
    private ISomainService somainService;
    @Autowired
    private SomainMapper somainMapper;
    @Autowired
    private SoitemMapper soitemMapper;

    public boolean checkSomain(Somain somain) {

        Long soId = somain.getSoId();
        if (soId == null) {
            rm.setMessage("销售单编号soId参数：不能为空");
            return false;
        }

        String account = somain.getAccount();
        if (StringUtil.isEmpty(account)) {
            rm.setMessage("创建用户account参数：不能为空");
            return false;
        }
        String customerCode = somain.getCustomerCode();
        if (StringUtil.isEmpty(customerCode)) {
            rm.setMessage("客户编号customerCode参数：不能为空");
            return false;
        }
        String createTime = somain.getCreateTime();
        if (StringUtil.isEmpty(createTime)) {
            rm.setMessage("创建时间createTime参数：不能为空");
            return false;
        } else if (!DateUtil.isValid(createTime)) {
            rm.setMessage("创建时间createTime参数格式不正确：yyyy-MM-dd HH:mm:ss");
            return false;
        }
        if (StringUtil.isEmpty(somain.getPayType())) {
            rm.setMessage("付款方式 payType参数：不能为空");
            return false;
        }
        Double productTotal = somain.getProductTotal();
        if (StringUtil.isEmpty(productTotal)) {
            rm.setMessage("销售产品价productTotal参数：不能为空");
            return false;
        } else if (productTotal <= 0) {
            rm.setMessage("销售产品价productTotal参数：必须为正数");
            return false;
        }

        Double tipFee = somain.getTipFee();
        if (tipFee < 0) {
            rm.setMessage("销售单附加费用不能为负数");
            return false;
        }
        Double soTotal = somain.getSoTotal();
        if (soTotal != productTotal + somain.getTipFee()) {
            rm.setMessage("销售单总价不正确");
            return false;
        }

        List<Soitem> soitems = somain.getSoitems();
        if (StringUtil.isEmpty(soitems) || soitems.size() == 0) {
            rm.setMessage("销售产品详细soitems参数：不能为空");
            return false;
        }

        Double total = 0.0;
        for (Soitem soitem : soitems) {
            Integer num = soitem.getNum();
            if (num <= 0) {
                rm.setMessage("销售的产品数量必须为正整数");
                return false;
            }

            Double unitPrice = soitem.getUnitPrice();
            if (unitPrice <= 0) {
                rm.setMessage("销售的产品单价必须为正整数");
                return false;
            }
            Double itemPrice = soitem.getItemPrice();
            if (itemPrice != unitPrice * num) {
                rm.setMessage("销售的产品明细价格不正确");
                return false;
            }
            total += itemPrice;
        }
        if (productTotal.doubleValue() != total.doubleValue()) {
            rm.setMessage("销售的产品明细总价格不正确");
            return false;
        }

        if (soTotal != productTotal + tipFee) {
            rm.setMessage("销售单总价格不正确");
            return false;
        }

        Integer payType = somain.getPayType();
        if (payType != ScmConfig.PayOnDelivery && payType != ScmConfig.AdvancePayToDelivery && payType != ScmConfig.PayBeforeDelivery) {
            rm.setMessage("付款方式不正确");
            return false;
        }

        Double prePayFee = somain.getPrePayFee();
        if (prePayFee < 0) {
            rm.setMessage("预付款金额不能为负数");
            return false;
        } else if (prePayFee >= soTotal) {
            rm.setMessage("预付款金额不能超过销售单总价格");
            return false;
        }

        Integer status = somain.getStatus();
        if (status == null || status != ScmConfig.ADD) {
            rm.setMessage("新添加的销售单状态status参数：应该为1表示新增");
            return false;
        }
        return true;
    }

    /**
     * 添加销售单
     */
    @RequestMapping("/somain/add")
    public ResponseMessage add(@RequestBody Somain somain) {
        if (!checkSomain(somain)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        somainService.insert(somain);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("销售单添加成功");
        return rm;
    }

    @RequestMapping("/somain/update")
    public ResponseMessage update(@RequestBody Somain somain,
                                  HttpSession session) {
        if (!checkSomain(somain)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        somainService.update(somain);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("销售单修改成功");
        return rm;
    }

    @RequestMapping("/somain/delete")
    public ResponseMessage delete(Integer page, long soId) {
        page = page == null ? 1 : page;
        somainService.delete(soId);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("销售单删除成功");
        rm.setData(somainService.selectByStatus(page, ScmConfig.ADD, null));
        return rm;
    }

    @RequestMapping("/somain/show")
    public PageInfo<Somain> select(Integer page, int type, Integer payType) {
        PageInfo<Somain> info = somainService.selectByStatus(page, type, payType);
        return info;
    }

    @RequestMapping("/somain/query")
    public PageInfo<Somain> selectQuery(Integer page, Long soId,
                                        String customerCode, Integer payType, String startDate,
                                        String endDate, Integer status) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        if (!StringUtil.isEmpty(soId))
            m.put("soId", soId);
        if (!StringUtil.isEmpty(customerCode))
            m.put("customerCode", customerCode);
        if (!StringUtil.isEmpty(payType))
            m.put("payType", payType);
        if (!StringUtil.isEmpty(startDate))
            m.put("startDate", startDate);
        if (!StringUtil.isEmpty(endDate))
            m.put("endDate", endDate);
        if (!StringUtil.isEmpty(status))
            m.put("status", status);
        List<Somain> somains = somainMapper.selectCondition(m);
        PageInfo<Somain> info = new PageInfo<>(somains);

        return info;
    }

    @RequestMapping("/somain/queryItem")
    public List<Soitem> queryItem(Long soId) {
        if (soId == null)
            throw new TransactionException("销售单编号不能为空");

        List<Soitem> soitems = soitemMapper.selectBySoId(soId);
        return soitems;
    }

    @RequestMapping("/somain/end")
    public ResponseMessage end(Long soId, Integer payType, Integer page,
                               HttpSession session) {
        Scmuser user = (Scmuser) session.getAttribute("login");
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", user.getAccount());
        map.put("soId", soId);
        map.put("now", DateUtil.currentTime());
        map.put("endStatus", ScmConfig.END);
        int n = somainMapper.end(map);
        if (n == 1) {
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("了结成功");
            rm.setData(somainService.selectByStatus(page, ScmConfig.CAN_END, payType));
        } else {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("了结失败");
        }
        return rm;
    }
}
