package com.eterlar.seckillsystem.service;

import com.eterlar.seckillsystem.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eterlar.seckillsystem.pojo.User;

/**
* @author eterlar
* @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Service
* @createDate 2022-06-13 14:39:05
*/
public interface SeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(User user, Long goodsId);

    /**
     * 生成秒杀路径
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);
}
