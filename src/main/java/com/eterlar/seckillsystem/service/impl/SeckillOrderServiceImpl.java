package com.eterlar.seckillsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eterlar.seckillsystem.pojo.SeckillOrder;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.service.SeckillOrderService;
import com.eterlar.seckillsystem.mapper.SeckillOrderMapper;
import com.eterlar.seckillsystem.utils.MD5Util;
import com.eterlar.seckillsystem.utils.UUIDUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
* @author eterlar
* @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Service实现
* @createDate 2022-06-13 14:39:05
*/
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
    implements SeckillOrderService{

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取秒杀结果
     * @param user 用户
     * @param goodsId 商品ID
     * @return 订单ID，如果商品库存为空返回-1L
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (null != seckillOrder) {
            return seckillOrder.getOrderId();
        } else if (Boolean.TRUE.equals(redisTemplate.hasKey("isStockEmpty:" + goodsId))){
            return -1L;
        } else{
            return 0L;
        }
    }

    /**
     * 生成秒杀地址
     * @param user 用户
     * @param goodsId 商品ID
     * @return 秒杀的地址
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        // 将每个用户秒杀相应商品的唯一的地址存进 redis 中，并设置超时时间
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str ,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 校验秒杀地址
     * @param user 用户
     * @param goodsId 商品ID
     * @param path 秒杀地址
     * @return 秒杀地址是否正确
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (null == user || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }
        String realPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return StringUtils.hasText(realPath) && realPath.equals(path);
    }
}




