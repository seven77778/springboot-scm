package com.lsh.scm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.CategoryMapper;
import com.lsh.scm.entity.Category;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.util.StringUtil;

@RestController
@RequestMapping("/main/sell")
public class CategoryController {
    @Autowired
    private CategoryMapper categoryMapper;

    private ResponseMessage rm = new ResponseMessage();

    private boolean checkCategory(Category category) {
        Integer categoryId = category.getCategoryId();
        if (StringUtil.isEmpty(categoryId)) {
            rm.setMessage("产品类别序列号categoryId参数：不能为空");
            return false;
        }
        String name = category.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setMessage("产品类别名称name参数：不能为空");
            return false;
        }
        return true;
    }

    @RequestMapping("/category/add")
    public ResponseMessage categoryAdd(Category category) {
        String name = category.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("产品类别名称name参数：不能为空");
            return rm;
        }

        categoryMapper.addCategory(category);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("添加产品分类成功");

        return rm;
    }

    @RequestMapping("/category/delete")
    public ResponseMessage delete(Integer categoryId) {
        if (categoryId == null) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("缺少必要的参数：categoryId");
        } else {
            int n = categoryMapper.deleteCategory(categoryId);
            if (n == 0) {
                rm.setCode(BusinessStatus.FAIL);
                rm.setMessage("删除失败");
            } else {
                rm.setCode(BusinessStatus.SUCCESS);
                rm.setMessage("删除成功");
                rm.setData(categoryMapper.select(null));
            }
        }
        return rm;
    }

    @RequestMapping("/category/update")
    public ResponseMessage update(Category category) {
        if (!checkCategory(category)) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        int n = categoryMapper.updateCategory(category);
        if (n == 0) {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("修改失败");
        } else {
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("修改成功");
        }
        return rm;
    }

    @RequestMapping("/category/show")
    public PageInfo<Category> select(Category category, Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<Category> categorys = categoryMapper.select(category);
        PageInfo<Category> info = new PageInfo<>(categorys);
        return info;
    }

    @RequestMapping("/category/all")
    public List<Category> selectAll() {
        List<Category> categorys = categoryMapper.select(null);
        return categorys;
    }
}
