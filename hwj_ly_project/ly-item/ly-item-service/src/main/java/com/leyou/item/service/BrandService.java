package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author Hwj
 * @Date 2019/3/28 15:44
 * @Version 1.0.0
 **/
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(
            Integer page, Integer rows, String sortBy, Boolean desc, String search) {
        //1.设置分页条件
        PageHelper.startPage(page,rows);

        //2.通过Example的静态内部类Criteria拼接设置查询条件：模糊查询,首字母查询，排序
        //初始化example对象,Example做条件查询
        Example example = new Example(Brand.class);
        Example.Criteria criteria=example.createCriteria();

        //根据name模糊查询，或者根据首字母查询
        if(StringUtils.isNotBlank(search)){
                /* WHERE 'name' LIKE "%x%" OR letter == 'x'*/
            criteria.andLike("name", "%"+search+"%")
                    .orEqualTo("letter", search.toUpperCase());
        }
        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            /*ORDER BY id DESC*/
            example.setOrderByClause(sortBy +" "+(desc ? " DESC" : " ASC"));
        }
        //3.获取条件下的查询结果
        List<Brand> brands = this.brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //4.封装查询结果返回给PageInfo
        PageInfo<Brand> info=new PageInfo<>(brands);
        //5.将pageinfo的查询结果  总条数  结合构造函数返回给结果集。
        return new PageResult<>(info.getTotal(),info.getList());
    }
    //给保存添加事务,下面的保存要么整体失败，要么整体成功.
    //RequestBody用对象来接收参数,不能再用参数来接收如：（Brand brand, List<Long> cids）
    //传送过来的数据是简单的键值对形式时就可用来接收：（Brand brand, List<Long> cids）
    @Transactional
    public void saveBrand(Brand brand,List<Long> cids) {
        // 新增品牌信息
        //insert方法会给所有字段设置值，没有传递的就设置为null
        //insertSelective只给传递的参数设置值，没有传递的默认为null,不会去设置
        int count = this.brandMapper.insertSelective(brand);
       /* if(count!=1){    添加了事务
           throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }*/
        // 新增品牌和分类中间表


       /* for (Long cid : cids) {
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
            if(count!=1){
                throw new LyException(ExceptionEnum.CATEGROY_BRAND_SAVE_ERROR);
            }
        }*/
       cids.forEach(cid ->{
           this.brandMapper.insertCategoryBrand(cid, brand.getId());
       });

    }

    /**
     * 品牌更新
     * @param brand
     * @param categories
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(Brand brand,List<Long> categories) {
//        System.out.println("bid:hhhhhhhhhhhhhhhhhhhh哈哈哈哈"+brand);
;        //删除原来的数据
        deleteByBrandIdInCategoryBrand(brand.getId());
        // 修改品牌信息
        this.brandMapper.updateByPrimaryKeySelective(brand);

        //维护品牌和分类中间表
        for (Long cid : categories) {
            //System.out.println("cid:"+cid+",bid:"+brand.getId());
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
        }
    }


    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    public List<Brand> queryByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    /**
     * 品牌删除
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(Long id) {
        //删除品牌信息
        brandMapper.deleteByPrimaryKey(id);

        //维护中间表
        brandMapper.deleteByBrandIdInCategoryBrand(id);
    }

    /**
     * 删除中间表中的数据
     * @param bid
     */
    public void deleteByBrandIdInCategoryBrand(Long bid) {
        brandMapper.deleteByBrandIdInCategoryBrand(bid);
    }

}
