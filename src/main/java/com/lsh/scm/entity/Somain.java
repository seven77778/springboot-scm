package com.lsh.scm.entity;

import java.util.List;

/**
 * 销售单主信息
 */
public class Somain {
    private Long soId;// 销售单编号
    private String customerCode;// 客户编号
    private String account;// 用户账号
    private String createTime;// 创建时间
    private Double tipFee;// 附加费用
    private Double productTotal;// 产品总价
    private Double soTotal;// 产品总价
    private Integer payType;// 付款方式
    private Double prePayFee;// 最低预付金额
    private Integer status;// 处理状态
    private String remark;// 备注
    private String stockTime;// 出库登记时间
    private String stockUser;// 出库登记用户
    private String payTime;// 付款登记时间
    private String payUser;// 付款登记用户
    private String prePayTime;// 预付登记时间
    private String prePayUser;// 预付登记用户
    private String endTime;// 了结时间
    private String endUser;// 了结用户

    private String customerName;// 客户名字 关联customer表
    // 销售单明细
    private List<Soitem> soitems;

    public Long getSoId() {
        return soId;
    }

    public void setSoId(Long soId) {
        this.soId = soId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Double getTipFee() {
        return tipFee;
    }

    public void setTipFee(Double tipFee) {
        this.tipFee = tipFee;
    }

    public Double getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(Double productTotal) {
        this.productTotal = productTotal;
    }

    public Double getSoTotal() {
        return soTotal;
    }

    public void setSoTotal(Double soTotal) {
        this.soTotal = soTotal;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Double getPrePayFee() {
        return prePayFee;
    }

    public void setPrePayFee(Double prePayFee) {
        this.prePayFee = prePayFee;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStockTime() {
        return stockTime;
    }

    public void setStockTime(String stockTime) {
        this.stockTime = stockTime;
    }

    public String getStockUser() {
        return stockUser;
    }

    public void setStockUser(String stockUser) {
        this.stockUser = stockUser;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayUser() {
        return payUser;
    }

    public void setPayUser(String payUser) {
        this.payUser = payUser;
    }

    public String getPrePayTime() {
        return prePayTime;
    }

    public void setPrePayTime(String prePayTime) {
        this.prePayTime = prePayTime;
    }

    public String getPrePayUser() {
        return prePayUser;
    }

    public void setPrePayUser(String prePayUser) {
        this.prePayUser = prePayUser;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndUser() {
        return endUser;
    }

    public void setEndUser(String endUser) {
        this.endUser = endUser;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<Soitem> getSoitems() {
        return soitems;
    }

    public void setSoitems(List<Soitem> soitems) {
        this.soitems = soitems;
    }

}
