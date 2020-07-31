package com.lsh.scm.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.dao.PoitemMapper;
import com.lsh.scm.dao.PomainMapper;
import com.lsh.scm.dao.ProductMapper;
import com.lsh.scm.entity.Poitem;
import com.lsh.scm.entity.Pomain;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.exception.TransactionException;
import com.lsh.scm.service.IPomainService;

/**
 * 采购单业务
 *
 * @author Administrator
 */
@Service
public class PomainServiceImpl implements IPomainService {
    @Autowired
    private PomainMapper pomainMapper;
    @Autowired
    private PoitemMapper poitemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private HttpSession session;

    /**
     * 新增采购单
     *
     * @param pomain
     * @throws SQLException
     */
    @Override
    @Transactional
    public void insert(Pomain pomain) {
        pomainMapper.insert(pomain);
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("poId", pomain.getPoId());
        itemMap.put("poitems", pomain.getPoitems());
        int n = poitemMapper.insert(itemMap);
        if (n <= 0) {
            throw new TransactionException("采购单明细添加失败！");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("poitems", pomain.getPoitems());
        map.put("type", 1);
        n = productMapper.updatePoNum(map);
        if (n <= 0) {
            throw new TransactionException("修改采购在途数失败！");
        }
    }

    /**
     * 修改采购单
     *
     * @param pomain
     * @throws SQLException
     */
    @Override
    @Transactional
    public void update(Pomain pomain) {

        // 修改主单
        int n = pomainMapper.update(pomain);
        if (n != 1) {
            throw new TransactionException("修改采购单主单失败！");
        }
        // 修改原来明细的在途数
        List<Poitem> items = poitemMapper.selectByPoId(pomain.getPoId());
        if (items == null) {
            throw new TransactionException("没有查到采购单" + pomain.getPoId() + "详情");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("poitems", pomain.getPoitems());
        map.put("type", 2);
        n = productMapper.updatePoNum(map);
        if (n <= 0) {
            throw new TransactionException("修改产品在途数时失败");
        }
        // 删除原来的明细单
        n = poitemMapper.deleteByPoid(pomain.getPoId());
        if (n == 0) {
            throw new TransactionException("删除采购明细单失败！");
        }

        // 新增新的明细单
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("poId", pomain.getPoId());
        itemMap.put("poitems", pomain.getPoitems());
        n = poitemMapper.insert(itemMap);
        if (n == 0) {
            throw new TransactionException("修改采购明细单失败！");
        }
        // 增加采购在途数
        map.put("type", 1);
        n = productMapper.updatePoNum(map);
        if (n <= 0) {
            throw new TransactionException("增加产品在途数时失败");
        }
    }

    /**
     * 删除采购单
     *
     * @param poid
     * @throws SQLException
     */
    @Override
    @Transactional
    public void delete(Long poid) {

        // 修改产品的在途数
        List<Poitem> items = poitemMapper.selectByPoId(poid);
        if (items == null || items.size() == 0) {
            throw new TransactionException("没有查到采购单" + poid + "详情，删除失败");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("poitems", items);
        map.put("type", 2);
        int n = productMapper.updatePoNum(map);
        if (n <= 0) {
            throw new TransactionException("修改产品在途数时失败");
        }

        // 删除详情
        n = poitemMapper.deleteByPoid(poid);
        if (n == 0) {
            throw new TransactionException("删除采购单明细失败");
        }

        n = pomainMapper.deleteByPoId(poid);
        if (n != 1) {
            throw new TransactionException("删除采购单主信息失败");
        }
    }

    @Override
    public PageInfo<Pomain> selectByStatus(Integer page, Integer type, Integer payType) {
        if (type == null) {
            throw new TransactionException("type参数不能为空，查询失败");
        }
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        m.put("type", type);
        m.put("payType", payType);
        if (type == 1 || type == 4) {
            Scmuser user = (Scmuser) session.getAttribute("login");
            m.put("account", user.getAccount());
        }
        List<Pomain> pomains = pomainMapper.selectPomain(m);
        System.out.println(pomains);
        PageInfo<Pomain> info = new PageInfo<>(pomains);
        return info;
    }

    @Override
    public PageInfo<Pomain> selectReport(Integer page, String time) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        m.put("startDate", time + "-01");
        m.put("endDate", time + "-31");
        List<Pomain> pomains = pomainMapper.selectCondition(m);
        PageInfo<Pomain> info = new PageInfo<>(pomains);
        return info;
    }

}
