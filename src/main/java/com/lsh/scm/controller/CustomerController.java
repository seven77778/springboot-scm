package com.lsh.scm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.CustomerMapper;
import com.lsh.scm.entity.Customer;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.util.DateUtil;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/sell")
public class CustomerController {
    @Autowired
    private CustomerMapper customerMapper;

    private ResponseMessage rm = new ResponseMessage();

    public boolean checkCustomer(Customer customer) {
        if (StringUtil.isEmpty(customer)) {
            rm.setMessage("用户信息不能为空");
            return false;
        }
        String customerCode = customer.getCustomerCode();
        if (!customerCode.matches("^[a-zA-Z0-9]{4,20}$")) {
            rm.setMessage("客户编号customerCode参数：要求4-20位的数字字母");
            return false;
        }
        String name = customer.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setMessage("名称name参数:不能为空");
            return false;
        } else if (name.length() > 100) {
            rm.setMessage("名称name参数：长度不能超过100个字符");
            return false;
        }
        String password = customer.getPassWord();
        if (StringUtil.isEmpty(password)) {
            rm.setMessage("密码password参数：不能为空");
            return false;
        } else if (password.length() > 20 || password.length() < 4) {
            rm.setMessage("密码password参数：长度为4~20位");
            return false;
        }
        String createDate = customer.getCreateDate();

        if (StringUtil.isEmpty(createDate)) {
            rm.setMessage("注册日期createDate参数：不能为空");
            return false;
        } else if (!DateUtil.isValid(createDate)) {
            rm.setMessage("注册日期createDate参数：日期格式不正确");
            return false;
        }

        String tel = customer.getTel();
        if (StringUtil.isEmpty(tel)) {
            rm.setMessage("电话tel参数：不能为空");
            return false;
        }

        return true;
    }

    @RequestMapping("/customer/add")
    public ResponseMessage customerAdd(Customer customer) {
        boolean flag = checkCustomer(customer);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        customerMapper.addCustomer(customer);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("添加供应商成功");
        return rm;
    }


    @RequestMapping("/customer/delete")
    public ResponseMessage delete(String customerCode) {
        if (customerCode == null) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("缺少必要的参数：customerCode");
        } else {
            customerMapper.deleteCustomer(customerCode);
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("删除成功");
            rm.setData(customerMapper.select(null));
        }
        return rm;
    }

    @RequestMapping("/customer/update")
    public ResponseMessage update(Customer customer) {
        boolean flag = checkCustomer(customer);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        int n = customerMapper.updateCustomer(customer);
        if (n == 0) {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("修改失败");
        } else {
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("修改成功");
        }
        return rm;
    }

    @RequestMapping("/customer/show")
    public PageInfo<Customer> select(Customer customer, Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<Customer> customers = customerMapper.select(customer);
        PageInfo<Customer> info = new PageInfo<>(customers);
        return info;
    }

    @RequestMapping("/customer/all")
    public List<Customer> selectAll() {
        List<Customer> customers = customerMapper.select(null);
        return customers;
    }
}
