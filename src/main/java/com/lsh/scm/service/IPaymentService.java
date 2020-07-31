package com.lsh.scm.service;

public interface IPaymentService {
    void pay(long poId, String account, String type);

    void rece(Long soId, String account, String type);
}
