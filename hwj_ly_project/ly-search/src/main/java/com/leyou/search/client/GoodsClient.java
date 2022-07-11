package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * @Author Hwj
 * @Date 2019/4/11 19:05
 * @Version 1.0.0
 **/
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi{


}
