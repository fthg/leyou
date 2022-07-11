package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category")
@Data
public class Category {
	//@GerneratedValue(strategy=GenerationType.IDENTITY)   用于指定主键生成策略
	//IDENTITY  数据库ID自增长,oracle 不支持
	//AUTO：    JPA自动选择合适的策略,默认
	//SEQUENCE: 通过序列产生主键，  配合@sequenceGenerator注解指定序列名，Mysql不支持这种方式
	//TABLE:通过表产生主键,易于数据库移植
	@Id
	@KeySql(useGeneratedKeys=true)
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent;
	private Integer sort;
	// getter和setter略
    // 注意isParent的get和set方法

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean parent) {
		isParent = parent;
	}
}