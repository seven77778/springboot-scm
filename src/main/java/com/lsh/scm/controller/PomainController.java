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
import com.lsh.scm.dao.PoitemMapper;
import com.lsh.scm.dao.PomainMapper;
import com.lsh.scm.entity.Poitem;
import com.lsh.scm.entity.Pomain;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.service.IPomainService;
import com.lsh.scm.util.DateUtil;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/purchase")
public class PomainController {
    private ResponseMessage rm;

    @Autowired
    private IPomainService pomainService;
    @Autowired
    private PomainMapper pomainMapper;
    @Autowired
    private PoitemMapper poitemMapper;

    public boolean checkPomain(Pomain pomain) {

        Long poId = pomain.getPoId();
        if (poId == null) {
            rm.setMessage("采购单编号poId参数：不能为空");
            return false;
        }

        String account = pomain.getAccount();
        if (StringUtil.isEmpty(account)) {
            rm.setMessage("创建用户account参数：不能为空");
            return false;
        }
        String venderCode = pomain.getVenderCode();
        if (StringUtil.isEmpty(venderCode)) {
            rm.setMessage("供应商编号venderCode参数：不能为空");
            return false;
        }
        String createTime = pomain.getCreateTime();
        if (StringUtil.isEmpty(createTime)) {
            rm.setMessage("创建时间createTime参数：不能为空");
            return false;
        } else if (!DateUtil.isValid(createTime)) {
            rm.setMessage("创建时间createTime参数格式不正确：yyyy-MM-dd HH:mm:ss");
            return false;
        }
        if (StringUtil.isEmpty(pomain.getPayType())) {
            rm.setMessage("付款方式 payType参数：不能为空");
            return false;
        }
        Double productTotal = pomain.getProductTotal();
        if (StringUtil.isEmpty(productTotal)) {
            rm.setMessage("采购产品价productTotal参数：不能为空");
            return false;
        } else if (productTotal <= 0) {
            rm.setMessage("采购产品价productTotal参数：必须为正数");
            return false;
        }
        Double tipFee = pomain.getTipFee();
        if (tipFee < 0) {
            rm.setMessage("采购单附加费用不能为负数");
            return false;
        }
        Double poTotal = pomain.getPoTotal();
        if (poTotal != productTotal + pomain.getTipFee()) {
            rm.setMessage("采购单总价不正确");
            return false;
        }

        List<Poitem> poitems = pomain.getPoitems();
        if (poitems == null || poitems.size() == 0) {
            rm.setMessage("采购产品详细poitems参数：不能为空");
            return false;
        }

        Double total = 0.0;
        for (Poitem poitem : poitems) {
            Integer num = poitem.getNum();
            if (num <= 0) {
                rm.setMessage("采购的产品数量必须为正整数");
                return false;
            }

            Double unitPrice = poitem.getUnitPrice();
            if (unitPrice <= 0) {
                rm.setMessage("采购的产品单价必须为正整数");
                return false;
            }
            Double itemPrice = poitem.getItemPrice();
            System.out.println("itemPrice:" + itemPrice);
            if (itemPrice != unitPrice * num) {
                rm.setMessage("采购的产品明细价格不正确");
                return false;
            }
            total += itemPrice;
        }
        System.out.println("productTotal:" + productTotal + ", total:" + total);
        if (productTotal.doubleValue() != total.doubleValue()) {
            rm.setMessage("采购的产品明细总价格不正确");
            return false;
        }

        if (poTotal != productTotal + tipFee) {
            rm.setMessage("采购单总价格不正确");
            return false;
        }
        Integer payType = pomain.getPayType();
        System.out.println("付款方式：" + payType);
        if (payType != ScmConfig.PayOnDelivery && payType != ScmConfig.AdvancePayToDelivery && payType != ScmConfig.PayBeforeDelivery) {
            rm.setMessage("付款方式不正确");
            return false;
        }

        Double prePayFee = pomain.getPrePayFee();
        if (prePayFee == null) {
            rm.setMessage("预付款金额不能为空");
            return false;
        } else if (prePayFee < 0) {
            rm.setMessage("预付款金额不能为负数");
            return false;
        } else if (prePayFee >= poTotal) {
            rm.setMessage("预付款金额不能超过采购单总价格");
            return false;
        } else if (payType != ScmConfig.AdvancePayToDelivery && prePayFee > 0) {
            rm.setMessage("预付款金额不正确");
            return false;
        }

        Integer status = pomain.getStatus();
        if (status == null || status != ScmConfig.ADD) {
            rm.setMessage("新添加的采购单状态status参数：应该为1表示新增");
            return false;
        }
        return true;
    }

    /**
     * 添加采购单
     */
    @RequestMapping("/pomain/add")
    public ResponseMessage add(@RequestBody Pomain pomain) {
        rm = new ResponseMessage();
        boolean flag = checkPomain(pomain);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        pomainService.insert(pomain);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("采购单添加成功");
        return rm;
    }

    @RequestMapping("/pomain/update")
    public ResponseMessage update(@RequestBody Pomain pomain, HttpSession session) {
        rm = new ResponseMessage();
        boolean flag = checkPomain(pomain);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        pomainService.update(pomain);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("采购单修改成功");
        return rm;
    }

    @RequestMapping("/pomain/delete")
    public ResponseMessage delete(Integer page, Long poId) {
        rm = new ResponseMessage();
        page = page == null ? 1 : page;
        pomainService.delete(poId);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("采购单删除成功");
        rm.setData(pomainService.selectByStatus(page, ScmConfig.ADD, null));
        return rm;
    }

    @RequestMapping("/pomain/show")
    public PageInfo<Pomain> select(Integer page, Integer type, Integer payType) {
        PageInfo<Pomain> info = pomainService.selectByStatus(page, type, payType);
        return info;
    }

    @RequestMapping("/pomain/query")
    public PageInfo<Pomain> selectQuery(Integer page, Long poId, String venderCode, Integer payType, String startDate, String endDate, Integer status) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        if (!StringUtil.isEmpty(poId))
            m.put("poId", poId + "");
        if (!StringUtil.isEmpty(venderCode))
            m.put("venderCode", venderCode);
        if (!StringUtil.isEmpty(payType))
            m.put("payType", payType);
        if (!StringUtil.isEmpty(startDate))
            m.put("startDate", startDate);
        if (!StringUtil.isEmpty(endDate))
            m.put("endDate", endDate);
        if (!StringUtil.isEmpty(status))
            m.put("status", status);
        List<Pomain> pomains = pomainMapper.selectCondition(m);
        PageInfo<Pomain> info = new PageInfo<>(pomains);

        return info;
    }

    @RequestMapping("/pomain/queryItem")
    public List<Poitem> queryItem(Long poId) {
        if (poId == null)
            throw new TransactionException("采购单编号不能为空");

        List<Poitem> poitems = poitemMapper.selectByPoId(poId);
        return poitems;
    }

    @RequestMapping("/pomain/end")
    public ResponseMessage end(Long poId, Integer page, Integer payType, HttpSession session) {
        rm = new ResponseMessage();
        Scmuser user = (Scmuser) session.getAttribute("login");
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", user.getAccount());
        map.put("poId", poId);
        map.put("now", DateUtil.currentTime());
        map.put("endStatus", ScmConfig.END);
        int n = pomainMapper.end(map);
        if (n == 1) {
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("了结成功");
            rm.setData(pomainService.selectByStatus(page, ScmConfig.CAN_END, payType));
        } else {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("了结失败");
        }
        return rm;
    }
}
