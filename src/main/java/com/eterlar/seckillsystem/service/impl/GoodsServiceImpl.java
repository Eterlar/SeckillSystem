package com.eterlar.seckillsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eterlar.seckillsystem.pojo.Goods;
import com.eterlar.seckillsystem.service.GoodsService;
import com.eterlar.seckillsystem.mapper.GoodsMapper;
import com.eterlar.seckillsystem.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author eterlar
* @description 针对表【t_goods(商品表)】的数据库操作Service实现
* @createDate 2022-06-13 14:38:16
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService{

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 获取商品详情
     * @param goodsId 商品ID
     * @return 商品VO封装对象
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}




