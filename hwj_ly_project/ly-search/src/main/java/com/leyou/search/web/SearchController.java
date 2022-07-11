package com.leyou.search.web;


import com.leyou.common.vo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class SearchController{

    @Autowired
    private SearchService searchService;


    /**
     * 搜索功能
     * @param searchRequest
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest){
        SearchResult result =this.searchService.search(searchRequest);
        if(result == null || CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }

       return ResponseEntity.ok(result);
    }




}
