package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface SkuMapper extends BaseMapper<Sku> {


    /**
     * 根据id查询sku信息
     * @param id
     * @return
     */
    @Select("SELECT a.*,b.stock FROM tb_sku a,tb_stock b WHERE a.id=b.sku_id AND a.spu_id=#{id}")
    List<Sku> queryById(@Param("id") Long id);
}
