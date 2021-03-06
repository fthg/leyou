package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author Hwj
 * @Date 2019/4/11 18:40
 * @Version 1.0.0
 **/
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi{

}
