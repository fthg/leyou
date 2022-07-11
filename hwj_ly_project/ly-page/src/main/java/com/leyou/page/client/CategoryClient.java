package com.leyou.page.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author Hwj
 * @Date 2019/4/11 18:40
 * @Version 1.0.0
 **/
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi{

}
