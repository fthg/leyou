package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author Hwj
 * @Date 2019/4/4 11:33
 * @Version 1.0.0
 **/
@RegisterMapper
public interface BaseMapper<T> extends Mapper<T>,IdListMapper<T,Long>,InsertListMapper<T> {
}
