package com.leyou.item.controller;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Hwj
 * @Date 2019/4/2 19:39
 * @Version 1.0.0
 **/
/*方法返回的都是json数据*/
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     *
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key) {

        PageResult<SpuBo> result = this.goodsService.querySpuByPage(page, rows, saleable, key);

        if(result==null || CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
            goodsService.saveGoods(spuBo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品
     * @param s
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        goodsService.updateGoods(spuBo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除商品
     * @param ids
     * @return
     */
    @DeleteMapping("goods/spu/{id}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("id") String ids){
        String separator="-";
        if (ids.contains(separator)){
            String[] goodsId = ids.split(separator);
            for (String id:goodsId){
                this.goodsService.deleteGoods(Long.parseLong(id));
            }
        }
        else {
            this.goodsService.deleteGoods(Long.parseLong(ids));
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据spu的id查询详情spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long spuId){
        SpuDetail spuDetail = goodsService.queryDetailById(spuId);
        if(spuDetail == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(spuDetail);

    }

    /**
     * 根据spuId查询下边的所有sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        List<Sku> skus = goodsService.querySkuBySpuId(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(skus);

    }


    /**
     * 根据sku的集合查询所有sku
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }


    /**
     * 根据id查询sku
     * @param id
     * @return
     */
    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id){
        Sku sku = this.goodsService.querySkuById(id);
        if (sku == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(sku);
    }

    /**
     * 减库存
     * @param carts
     * @return
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> carts){
        goodsService.decreaseStock(carts);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 商品上下架
     * @param id
     * @return
     */
    @PutMapping("goods/spu/out/{id}")
    public ResponseEntity<Void> goodsSoldOut(@PathVariable("id") Long id){
        this.goodsService.goodsSoldOut(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

