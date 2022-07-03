package com.eterlar.seckillsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eterlar.seckillsystem.exception.GlobalException;
import com.eterlar.seckillsystem.pojo.Order;
import com.eterlar.seckillsystem.pojo.SeckillGoods;
import com.eterlar.seckillsystem.pojo.SeckillOrder;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.service.GoodsService;
import com.eterlar.seckillsystem.service.OrderService;
import com.eterlar.seckillsystem.mapper.OrderMapper;
import com.eterlar.seckillsystem.service.SeckillGoodsService;
import com.eterlar.seckillsystem.service.SeckillOrderService;
import com.eterlar.seckillsystem.vo.GoodsVo;
import com.eterlar.seckillsystem.vo.OrderDetailVo;
import com.eterlar.seckillsystem.enums.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
* @author eterlar
* @description 针对表【t_order】的数据库操作Service实现
* @createDate 2022-06-13 14:38:51
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

    @Resource
    private SeckillGoodsService seckillGoodsService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     * @param user
     * @param goodsVo
     * @return 秒杀订单
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goodsVo.getId())
        );
        // 设置库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        // update加了行级排他锁，是原子操作，可以保存线程安全
        seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count-1")
                .eq("goods_id", goodsVo.getId())
                .gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) {
            // 判断是否有库存
            valueOperations.set("isStockEmpty:" + goodsVo.getId(), "0");
            return null;
        }
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);
        valueOperations.set("order:" + user.getId() + ":" + goodsVo.getId(), seckillOrder);

        // 返回订单
        return order;
    }

    /**
     * 订单详情
     * @param orderId 订单ID
     * @return 订单详情封装对象
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (null == orderId) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    /**
     * 获取用户所有订单
     * @param userId 用户ID
     * @return 用户所有订单列表
     */
    @Override
    public List<Order> orderList(Long userId) {
        return orderMapper.selectList(
                new QueryWrapper<Order>().eq(true, "user_id",userId)
        );
    }
}




