package com.leyou.common.vo;

import lombok.Data;
import org.mockito.cglib.core.CollectionUtils;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;// 总条数
    private int totalPage;// 总页数
    private List<T> items;// 当前页数据

    public PageResult() {
    }
    //代有总条数的构造器
    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }
    //代有总条数和总页数的构造器
    public PageResult(Long total, int totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}