package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Hwj
 * @Date 2019/4/27 11:02
 * @Version 1.0.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long skuId; //商品skuId
    private Integer num; //购买数量
}
