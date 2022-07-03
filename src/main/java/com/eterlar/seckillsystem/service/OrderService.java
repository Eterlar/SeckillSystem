package com.eterlar.seckillsystem.service;

import com.eterlar.seckillsystem.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.vo.GoodsVo;
import com.eterlar.seckillsystem.vo.OrderDetailVo;

import java.util.List;

/**
* @author eterlar
* @description 针对表【t_order】的数据库操作Service
* @createDate 2022-06-13 14:38:51
*/
public interface OrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param goodsVo
     * @return
     */
    Order seckill(User user, GoodsVo goodsVo);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /**
     * 获取用户所有订单
     * @param userId
     * @return
     */
    List<Order> orderList(Long userId);
}
