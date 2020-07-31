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
import com.lsh.scm.dao.SoitemMapper;
import com.lsh.scm.dao.SomainMapper;
import com.lsh.scm.dao.ProductMapper;
import com.lsh.scm.entity.Scmuser;
import com.lsh.scm.entity.Soitem;
import com.lsh.scm.entity.Somain;
import com.lsh.scm.exception.TransactionException;
import com.lsh.scm.service.ISomainService;

/**
 * 销售单业务
 *
 * @author Administrator
 */
@Service
public class SomainServiceImpl implements ISomainService {
    @Autowired
    private SomainMapper somainMapper;
    @Autowired
    private SoitemMapper soitemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private HttpSession session;

    /**
     * 新增销售单
     *
     * @param somain
     * @throws SQLException
     */
    @Override
    @Transactional
    public void insert(Somain somain) {
        somainMapper.insert(somain);
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("soId", somain.getSoId());
        itemMap.put("soitems", somain.getSoitems());
        int n = soitemMapper.insert(itemMap);
        if (n <= 0) {
            throw new TransactionException("销售单明细添加失败！");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("soitems", somain.getSoitems());
        map.put("type", 1);
        n = productMapper.updateSoNum(map);
        if (n <= 0) {
            throw new TransactionException("当前产品数量不足添加失败！");
        }
    }

    /**
     * 修改销售单
     *
     * @param somain
     * @throws SQLException
     */
    @Override
    @Transactional
    public void update(Somain somain) {

        // 修改主单
        int n = somainMapper.update(somain);
        if (n != 1) {
            throw new TransactionException("修改销售单主单失败！");
        }
        // 修改原来明细的销售待发数
        List<Soitem> items = soitemMapper.selectBySoId(somain.getSoId());
        if (items == null) {
            throw new TransactionException("没有查到销售单" + somain.getSoId() + "详情");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("soitems", somain.getSoitems());
        map.put("type", 2);
        n = productMapper.updateSoNum(map);
        if (n <= 0) {
            throw new TransactionException("当前产品数量不足添加失败！");
        }
        // 删除原来的明细单
        n = soitemMapper.deleteBySoId(somain.getSoId());
        if (n == 0) {
            throw new TransactionException("删除销售明细单失败！");
        }

        // 新增新的明细单
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("soId", somain.getSoId());
        itemMap.put("soitems", somain.getSoitems());
        n = soitemMapper.insert(itemMap);
        if (n <= 0) {
            throw new TransactionException("销售单明细修改失败！");
        }
        // 增加销售销售待发数
        map.put("type", 1);
        n = productMapper.updateSoNum(map);
        if (n <= 0) {
            throw new TransactionException("当前产品数量不足添加失败！");
        }
    }

    /**
     * 删除销售单
     *
     * @param soid
     * @throws SQLException
     */
    @Override
    @Transactional
    public void delete(long soid) {

        // 修改产品的销售待发数
        List<Soitem> items = soitemMapper.selectBySoId(soid);
        if (items == null) {
            throw new TransactionException("没有查到销售单" + soid + "详情");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("soitems", items);
        map.put("type", 2);
        int n = productMapper.updateSoNum(map);
        if (n <= 0) {
            throw new TransactionException("修改产品销售待发数时失败");
        }

        // 删除详情
        n = soitemMapper.deleteBySoId(soid);
        if (n == 0) {
            throw new TransactionException("删除销售单明细失败");
        }

        n = somainMapper.deleteBySoId(soid);
        if (n <= 0) {
            throw new TransactionException("删除销售单主信息失败");
        }
    }

    @Override
    public PageInfo<Somain> selectByStatus(Integer page, int type, Integer payType) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        m.put("type", type);
        m.put("payType", payType);
        if (type == 1 || type == 4) {
            Scmuser user = (Scmuser) session.getAttribute("login");
            m.put("account", user.getAccount());
        }
        List<Somain> somains = somainMapper.selectSomain(m);
        System.out.println(somains);
        PageInfo<Somain> info = new PageInfo<>(somains);
        return info;
    }

    @Override
    public PageInfo<Somain> selectReport(Integer page, String time) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        HashMap<String, Object> m = new HashMap<>();
        m.put("startDate", time + "-01");
        m.put("endDate", time + "-31");
        List<Somain> somains = somainMapper.selectCondition(m);
        PageInfo<Somain> info = new PageInfo<>(somains);
        return info;
    }

}
