package com.lsh.scm.dao;

import java.util.List;

import com.lsh.scm.entity.Customer;

public interface CustomerMapper {
    void addCustomer(Customer customer);

    int deleteCustomer(String customerCode);

    int updateCustomer(Customer customer);

    List<Customer> select(Customer customer);

    Customer selectByCustomerCode(String customerCode);
}
