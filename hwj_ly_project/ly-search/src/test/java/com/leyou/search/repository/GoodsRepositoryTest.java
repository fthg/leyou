package com.leyou.search.repository;

import com.leyou.common.vo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.testGoods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    /**
     * 创建索引库
     */
    @Test
    public void testCreateIndex(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
//        //创建索引
//          template.createIndex(testGoods.class);
//          //配置映射
//          template.putMapping(testGoods.class);
    }

    /**
     * 导入索引库数据
     */
    @Test
    public void loadData1() {
        Integer page = 1;
        Integer size = 0;
        Integer rows = 100;
        do {
            // 查询spu信息
            PageResult<SpuBo> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<SpuBo> spuList = result.getItems();
            // 把spuList中的每个spuBo调用buildGoods构成一个Goods然后构成一个新的集合.
            //下面的代码等价   Collectors.toList()把流中所有元素收集到List中
     /*       List<Goods> goodList = spuList.stream()
                    .map(searchService::buildGoods).collect(Collectors.toList());*/
            List<Goods> goodsList=spuList.stream().map(spuBo ->{
                try{
                    return this.searchService.buildGoods(spuBo);
                }catch (IOException e){
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goodsList);
            page++;
            size = spuList.size();  //设置size为获取到的spu的大小,因为一页要拿100条，当不足100条就说明是最后一页，就可以退出循环了
        } while (size == 100);
    }
//    @Test
//    public void loadData(){
//        int page = 1;
//        int rows =100;
//        int size = 0;
//        do {
//            //查询spu信息
//            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
//            //取出当前页结果
//            List<Spu> spuList = result.getItems();
//            if(CollectionUtils.isEmpty(spuList)){
//                break;
//            }
//            //构建成goods
//            List<Goods> goodsList = spuList.stream().map(searchService::buildGoods).collect(Collectors.toList());
//            //存入索引库
//            goodsRepository.saveAll(goodsList);
//            //翻页
//            page++;
//            size = spuList.size();
//        }while(size == 100);
//    }

}