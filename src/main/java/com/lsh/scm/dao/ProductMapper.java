package com.lsh.scm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lsh.scm.entity.Poitem;
import com.lsh.scm.entity.Product;
import com.lsh.scm.entity.Soitem;

public interface ProductMapper {
    public void addProduct(Product product);

    int deleteProduct(String productCode);

    int updateProduct(Product product);

    List<Product> select(Product product);

    Product selectByProductCode(String productCode);

    int updatePoNum(Map map);

    int updateSoNum(Map map);

    int instock(List<Poitem> poitems);

    int outstock(List<Soitem> soitems);

    int check(Map map);

    List<Product> selectStock(Map map);

    HashMap<String, Object> selectTotalNum(String time);
}
