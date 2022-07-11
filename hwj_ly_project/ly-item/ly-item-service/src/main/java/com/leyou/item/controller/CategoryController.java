package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * @Author Hwj
 * @Date 2019/3/27 19:33
 * @RequestParam一般在get请求时,参数是一个个的参数时,请求url一般为http://localhost:8089/test/getDataById?id=1
 *@RequestBody一般在post请求时,参数是一个对象或者集合时,请求一般为json类型的请求体
 *@PathVariable一般在get请求时,参数是一个个的参数时,更能体现RestFul风格,请求url一般为:http://localhost:8089/test/getDataById/1
 * @Version 1.0.0
 **/
@RestController  //注解是@Controller和@ResponseBody的结合,下面的类返回都是json字符串
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /**
     * 根据父节点查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryByParentId(@RequestParam(value = "pid",defaultValue = "0") Long pid) {
//        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
        //如果pid的值为-1那么需要获取数据库中最后一条数据
        //程序如果出错，会响应500所以不用try catch
          /*  //视频中的实现
                 if(pid==null || pid<0){
                //响应400参数不合法,写法优化如下
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
               // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories =this.categoryService.queryCategoryListByPid(pid);
            if(CollectionUtils.isEmpty(categories)){
                //404，资源服务器未找到
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            // 200：查询成功
            return ResponseEntity.ok(categories);*/



        if (pid == -1){
            List<Category> last = this.categoryService.queryLast();
            return ResponseEntity.ok(last);
        }
        else {
            List<Category> list = this.categoryService.queryCategoryListByPid(pid);
            if (list == null) {
                //没有找到返回404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            //找到返回200
            return ResponseEntity.ok(list);
        }

    }

    @GetMapping //根据id查询分类的名字
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids){
        List<String> names = this.categoryService.queryNamesByIds(ids);
        if(CollectionUtils.isEmpty(names)){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(names);
    }


    /**
     * 根据id查询商品分类
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id){
        return ResponseEntity.ok(categoryService.queryAllByCid3(id));
    }

    /**
     * 用于修改品牌信息时，商品分类信息的回显
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category> list = categoryService.queryByBrandId(bid);
        if(list == null || list.size() < 1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 保存（新增分类）
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveCategory(Category category){
        System.out.println(category);
        this.categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除（删除分类）
     * @return
     */
    @DeleteMapping("cid/{cid}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("cid") Long id){
        this.categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 更新
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCategory(Category category){
        this.categoryService.updateCategory(category);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
