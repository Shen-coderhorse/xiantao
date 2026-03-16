package com.xiantao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiantao.entity.Category;
import com.xiantao.vo.CategoryVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryVO> getCategoryList();
}
