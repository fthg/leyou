package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequestMapping("category")
public interface CategoryApi {
    @GetMapping("list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);
    @GetMapping("all/level")
    List<Category> queryAllByCid3(@RequestParam("id") Long id);

    @GetMapping //根据id查询分类的名字
    List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}