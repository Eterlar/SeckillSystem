package com.eterlar.seckillsystem.mapper;

import com.eterlar.seckillsystem.pojo.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eterlar.seckillsystem.vo.GoodsVo;

import java.util.List;

/**
* @author eterlar
* @description 针对表【t_goods(商品表)】的数据库操作Mapper
* @createDate 2022-06-13 14:38:16
* @Entity com.eterlar.seckillsystem.pojo.Goods
*/
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     *
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}




