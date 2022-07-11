package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.Transient;
//规格组
@Table(name = "tb_spec_group")
@Data
public class SpecGroup {

    @Id
    @KeySql(useGeneratedKeys=true)
    private Long id;

    private Long cid;

    private String name;

    @Transient  //忽略这个字段   因为数据库表中没有这个
    private List<SpecParam> params; // 该组下的所有规格参数集合

   // getter和setter省略
}