package com.lsh.scm.entity;

/**
 * 用户模块表
 */
public class UserModel {
    //	private String account;//用户账号
    private Integer modelCode;//模块编号

    private String modelName;//模块名称
//	private String modelUri;//模块路径


    public Integer getModelCode() {
        return modelCode;
    }

    /*public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }*/
    public void setModelCode(Integer modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

}
