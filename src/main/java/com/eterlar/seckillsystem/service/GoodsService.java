package com.eterlar.seckillsystem.service;

import com.eterlar.seckillsystem.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eterlar.seckillsystem.vo.GoodsVo;

import java.util.List;

/**
* @author eterlar
* @description 针对表【t_goods(商品表)】的数据库操作Service
* @createDate 2022-06-13 14:38:16
*/
public interface GoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
