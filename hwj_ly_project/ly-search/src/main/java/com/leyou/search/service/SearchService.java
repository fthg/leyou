package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author Hwj
 * @Date 2019/4/12 16:22
 * @Version 1.0.0
 **/
@Slf4j
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsRepository repository;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     *
     * @param spu
     * @return 通过Spu对象获取Id,进而查询到所有展示所需要的信息,将这些信息组成Goods对象返回
     */
    public Goods buildGoods(Spu spu) throws IOException {
        Long spuId = spu.getId();
                                           /*1.获取搜索字段*/
        //(1)查询商品所有级分类的名字
         /*List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
         }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());*/
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //(2)根据品牌id查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //(3)组装 搜索字段,加空格避免索引词异常
        String all=spu.getTitle()+" "+ StringUtils.join(names," ")+" "+brand.getName();
                               /*2.获取sku的信息：价格 和其它参数包括id,title,price,images*/
        //(1)根据spuid查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        List<Map<String,Object>> skus=new ArrayList<>();
        Set<Long> priceList = new HashSet<>();
        skuList.forEach( sku->{
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //数据库的图片有多张以，隔开的
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            //(2)保存价格
            priceList.add(sku.getPrice());
        });
                              /*  3.获取可搜索··的规格参数*/
        //(1)根据spu中的cid3查询所有的用于搜索的规格参数的   名字
        List<SpecParam> params = specificationClient.querySpecParamList(null, spu.getCid3(), true);
        if(CollectionUtils.isEmpty(params)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        /*获取通用规格参数和特有规格参数的   值：
                1.获得spuDetail
                2.从spuDetail中获取通用参数String,并且反序列化(new TypeReference),
                3.从spuDetail中获取特殊参数String,并且反序列化(new TypeReference)*/
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        /*Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);*/
        /*Map<Long, List<String>> specialSpec = JsonUtils
                .nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});*/
        Map<String,Object> genericSpecMap=MAPPER.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<String,Object>>(){});
        Map<String,List<Object>> specialSpecMap=MAPPER.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<String,List<Object>>>(){});
        //规格参数,key是规格参数的名字，值是规格参数的值
        HashMap<String, Object> specs = new HashMap<>();
            params.forEach(param->{
                //判断是否是通用规格
                if(param.getGeneric()){
                  String  value = genericSpecMap.get(param.getId().toString()).toString();
                    //判断是否是数值类型
                    if(param.getNumeric()){
                        //处理成段
                        value = chooseSegment(value,param);
                    }
                    specs.put(param.getName(),value);
                }else{       //特殊规格的情况
                    /*value = specialSpecMap.get(param.getId());*/
                    List<Object> value = specialSpecMap.get(param.getId().toString());
                    specs.put(param.getName(),value);
                }
                /*//存入map
                specs.put(key,value);*/
            });
        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spuId);
        goods.setSubTitle(spu.getSubTitle());

        goods.setAll(all);// 搜索字段，包含标题，分类，品牌，规格等

        goods.setPrice(priceList);// 所有sku的价格集合
        goods.setSkus(JsonUtils.toString(skus));// 所有sku的集合的json格式

        goods.setSpecs(specs);// 所有的可搜索的规格参数
        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段   逗号作为分割，得到所有区间
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");//数组的每一个数据又由-作为分割,得到区间的首尾两个数
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;           //区间有xxx以上的,故区间尾部的数字要分情况
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }



    public SearchResult search(SearchRequest request) {
        int page = request.getPage() - 1; // elasicsearch默认page是从0开始
        int size = request.getSize();
        if(StringUtils.isBlank(request.getKey())){
            return null;
        }
        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 0.结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        // 1.分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 2.过滤
        QueryBuilder basicQuery = bulidBasicQuery(request);
        if(basicQuery!=null){
            System.out.println();
        }
        queryBuilder.withQuery(basicQuery );

        //3.聚合分类和品牌
        String categoryAggName = "category_agg";
        String brandAggName = "brand_agg";
        //3.1 对商品分类进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //3.2 对品牌进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        // 4.查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        // 5.解析结果
        //5.1解析分页结果
        long total= result.getTotalElements();
        int totalPage = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //5.2解析聚合结果
        Aggregations aggs = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggs.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));

        // 6.完成规格参数聚合
        // 判断商品分类数量，看是否需要对规格参数进行聚合
        List<Map<String, Object>> specs = null;
        if (categories != null && categories.size() == 1) {
            // 商品分类存在且数量为1，可以聚合规格参数
            specs = buildSpecficationAgg(categories.get(0).getId(), basicQuery);
        }
        // 返回结果

  /*      if(StringUtils.isBlank(request.getKey())){
            return null;
        }
        //自定义查询构建起
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //添加查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //添加分页 ,分页页码从0开始
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //添加结果集过滤,  设置结果要包含的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //执行查询
        Page<Goods> goodsPage = this.repository.search(queryBuilder.build());*/
       /* return new PageResult<>(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent());*/
        return new SearchResult(total,totalPage,goodsList,categories,brands,specs);
    }

    /**
     * 基本查询
     * @param request
     * @return
     */
    private QueryBuilder bulidBasicQuery(SearchRequest request) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        // 过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        // 整理过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 商品分类和品牌要特殊处理
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
        }
        // 添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }

    private List<Map<String,Object>> buildSpecficationAgg(Long cid, QueryBuilder basicQuery) {

       List<Map<String,Object>> specs = new ArrayList<>();
        // 1.查询需要聚合的规格参数
            List<SpecParam> params = specificationClient.querySpecParamList(null,cid,true);
            //2.聚合
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            //2.1带上查询条件
            queryBuilder.withQuery(basicQuery);
            //2.2聚合
            for (SpecParam param : params) {
                String name = param.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
            }
            //3.获取结果
            AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
            
            //4.解析结果
            Aggregations aggs = result.getAggregations();
            for (SpecParam param : params) {
                //规格参数名称
               String name= param.getName();
               StringTerms terms= aggs.get(name);
                //准备map
                HashMap<String, Object> map = new HashMap<>();
                map.put("k",name);
                map.put("options",terms.getBuckets()
                        .stream().map(b -> b.getKeyAsString()).collect(Collectors.toList()));
                specs.add(map);
            }

        return specs;

    }

    // 解析品牌聚合结果
    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets()
                    .stream().map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        } catch (Exception e) {
            log.error("[搜索服务]查询品牌异常", e);
            return null;
        }
    }
    // 解析商品分类聚合结果
    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {

            List<Long> ids = terms.getBuckets().stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        } catch (Exception e) {
            log.error("[搜索服务]查询分类异常", e);
            return null;
        }
    }

    /**
     * 创建或更新索引
     * @param spuId
     */
    public void createOrUpdateIndex(Long spuId)  {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //如果商品存在则存入索引库
        if(spu!=null){
            //构建goods
            Goods goods=null;
            try {
                goods= buildGoods(spu);
            }catch ( IOException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //存入索引库
            repository.save(goods);
        }
    }

    /**
     * 删除索引
     * @param spuId
     */
    public void deleteIndex(Long spuId) {
        //从索引库中删除
        repository.deleteById(spuId);
    }
}
