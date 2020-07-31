package com.lsh.scm.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.ScmConfig;
import com.lsh.scm.dao.CheckStockMapper;
import com.lsh.scm.dao.PoitemMapper;
import com.lsh.scm.dao.PomainMapper;
import com.lsh.scm.dao.ProductMapper;
import com.lsh.scm.dao.SoitemMapper;
import com.lsh.scm.dao.SomainMapper;
import com.lsh.scm.dao.StockRecordMapper;
import com.lsh.scm.entity.CheckStock;
import com.lsh.scm.entity.Poitem;
import com.lsh.scm.entity.Soitem;
import com.lsh.scm.entity.StockRecord;
import com.lsh.scm.exception.TransactionException;
import com.lsh.scm.service.IStockService;
import com.lsh.scm.util.DateUtil;

@Service
public class StockServiceImpl implements IStockService {
    @Autowired
    private PomainMapper pomainMapper;
    @Autowired
    private SomainMapper somainMapper;
    @Autowired
    private PoitemMapper poitemMapper;
    @Autowired
    private SoitemMapper soitemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private StockRecordMapper stockRecordMapper;
    @Autowired
    private CheckStockMapper checkStockMapper;

    /**
     * 入库操作
     *
     * @param poid
     * @param account 操作人
     */
    @Override
    @Transactional
    public void instock(long poid, String account) {

        String now = DateUtil.currentTime();
        HashMap<String, Object> map = new HashMap<>();
        map.put("now", now);
        map.put("poId", poid);
        map.put("account", account);
        // 1.修改采购单记录
        int n = pomainMapper.instock(map);
        if (n != 1) {
            throw new TransactionException("修改采购单主单入库信息失败");
        }
        // 2.根据poid查询采购单详情
        List<Poitem> poitems = poitemMapper.selectByPoId(poid);

        // 3.修改采购在途数及增加库存数
        n = productMapper.instock(poitems);
        if (n <= 0) {
            throw new TransactionException("修改采购在途数及增加库存数失败");
        }

        // 4.增加库存变化记录
        HashMap<String, Object> m = new HashMap<>();
        m.put("items", poitems);
        m.put("stockTime", now);
        m.put("stockType", ScmConfig.PO_INSTOCK);
        m.put("account", account);
        m.put("id", poid);
        stockRecordMapper.insert(m);
    }

    /**
     * 出库操作
     *
     * @param soid
     * @param account 操作人
     */
    @Override
    @Transactional
    public void outstock(long soid, String account) {
        String now = DateUtil.currentTime();
        HashMap<String, Object> map = new HashMap<>();
        map.put("now", now);
        map.put("soId", soid);
        map.put("account", account);
        // 1.修改销售单记录
        int n = somainMapper.outstock(map);
        if (n != 1) {
            throw new TransactionException("修改销售单主单出库信息失败");
        }
        // 2.根据soid查询销售单详情
        List<Soitem> soitems = soitemMapper.selectBySoId(soid);
        if (soitems == null || soitems.size() == 0) {
            throw new TransactionException("没有销售单的产品明细，不能出库");
        }

        // 3.修改销售待发数及减少库存数
        n = productMapper.outstock(soitems);
        if (n <= 0) {
            throw new TransactionException("库存不足，不能出库");
        }

        // 4.增加库存变化记录
        HashMap<String, Object> m = new HashMap<>();
        m.put("items", soitems);
        m.put("stockTime", now);
        m.put("stockType", ScmConfig.SO_OUTSTOCK);
        m.put("account", account);
        m.put("id", soid);
        stockRecordMapper.insert(m);

    }

    /**
     * 库存盘点更新
     */
    @Override
    @Transactional
    public void checkStock(CheckStock cs, int num) {
//		增加库存盘点记录
        checkStockMapper.insert(cs);
//		修改产品表库存数量
        HashMap<String, Object> m = new HashMap<>();
        m.put("productCode", cs.getProductCode());
        m.put("num", num);
        productMapper.check(m);
//		增加出库或入库记录
        StockRecord record = new StockRecord();
        record.setCreateUser(cs.getCreateUser());
        record.setProductCode(cs.getProductCode());
        record.setStockNum(Math.abs(num));
        record.setStockTime(cs.getStockTime());
        record.setStockType(num > 0 ? ScmConfig.CHECK_INSTOCK : ScmConfig.CHECK_OUTSTOCK);
        stockRecordMapper.insertCheck(record);
    }

    @Override
    public PageInfo<HashMap<String, Object>> selectReport(Integer page, String time) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<HashMap<String, Object>> list = stockRecordMapper.selectProductStockRecord(time);
        PageInfo<HashMap<String, Object>> info = new PageInfo<>(list);
        return info;
    }

    @Override
    public PageInfo<HashMap<String, Object>> selectInstock(Integer page, String time) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<HashMap<String, Object>> pomains = stockRecordMapper.selectInstockPomain(time);
        PageInfo<HashMap<String, Object>> info = new PageInfo<>(pomains);
        return info;
    }

    @Override
    public PageInfo<HashMap<String, Object>> selectOutstock(Integer page, String time) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<HashMap<String, Object>> somains = stockRecordMapper.selectOutstockPomain(time);
        PageInfo<HashMap<String, Object>> info = new PageInfo<>(somains);
        return info;
    }

}
