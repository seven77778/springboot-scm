package com.lsh.scm.dao;

import java.util.List;

import com.lsh.scm.entity.Category;

public interface CategoryMapper {
    public void addCategory(Category category);

    int deleteCategory(Integer categoryId);

    int updateCategory(Category category);

    List<Category> select(Category category);
}
