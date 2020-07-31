package com.lsh.scm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.ProductMapper;
import com.lsh.scm.entity.Product;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/sell")
public class ProductController {
    @Autowired
    private ProductMapper productMapper;

    private ResponseMessage rm = new ResponseMessage();

    public boolean checkProduct(Product product) {
        if (product == null) {
            rm.setMessage("产品信息不能为空");
            return false;
        }
        String productCode = product.getProductCode();
        if (StringUtil.isEmpty(productCode)) {
            rm.setMessage("产品编号productCode参数：不能为空");
            return false;
        } else if (!productCode.matches("^[a-zA-Z0-9]{4,20}$")) {
            rm.setMessage("产品编号productCode参数：要求4-20位的数字字母");
            return false;
        }
        String name = product.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setMessage("名称name参数：不能为空");
            return false;
        } else if (name.length() > 100) {
            rm.setMessage("名称name参数：长度不能超过100个字符");
            return false;
        }
        String unitName = product.getUnitName();
        if (StringUtil.isEmpty(unitName)) {
            rm.setMessage("数量单位unitNume参数：不能为空");
            return false;
        }
        Double price = product.getPrice();
        if (price == null) {
            rm.setMessage("销售价price参数：不能为空");
            return false;
        } else if (price <= 0) {
            rm.setMessage("销售价price参数要求为正整数");
            return false;
        }

        Integer categoryId = product.getCategoryId();

        if (StringUtil.isEmpty(categoryId)) {
            rm.setMessage("分类编号categoryId参数：不能为空");
            return false;
        }
        return true;
    }

    @RequestMapping("/product/add")
    public ResponseMessage productAdd(Product product) {
        if (!checkProduct(product)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        ;
        productMapper.addProduct(product);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("添加产品成功");
        return rm;
    }

    @RequestMapping("/product/delete")
    public ResponseMessage delete(String productCode) {
        if (productCode == null) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("缺少必要的参数：productCode");
        } else {
            int n = productMapper.deleteProduct(productCode);
            if (n == 0) {
                rm.setCode(BusinessStatus.FAIL);
                rm.setMessage("修改失败");
            } else {
                rm.setCode(BusinessStatus.SUCCESS);
                rm.setMessage("删除成功");
                rm.setData(productMapper.select(null));
            }
        }
        return rm;
    }

    @RequestMapping("/product/update")
    public ResponseMessage update(Product product) {
        if (!checkProduct(product)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        ;
        int n = productMapper.updateProduct(product);
        if (n == 0) {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("修改失败");
        } else {
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("修改成功");
        }
        return rm;
    }

    @RequestMapping("/product/show")
    public PageInfo<Product> select(Product product, Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<Product> products = productMapper.select(product);
        PageInfo<Product> info = new PageInfo<>(products);
        return info;
    }

    @RequestMapping("/product/all")
    public List<Product> selectAll() {
        List<Product> products = productMapper.select(null);
        return products;
    }

}
