package com.eterlar.seckillsystem.rabbitmq;

import com.eterlar.seckillsystem.vo.SeckillMessage;
import com.eterlar.seckillsystem.pojo.SeckillOrder;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.service.GoodsService;
import com.eterlar.seckillsystem.service.OrderService;
import com.eterlar.seckillsystem.utils.JsonUtil;
import com.eterlar.seckillsystem.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息消费者
 * @author eterlar
 */
@Service
@Slf4j
public class MQReceiver {
    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderService orderService;

    /**
     * 监听队列，接收信息并消费
     * @param msg
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("接收到的消息: " + msg);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        assert seckillMessage != null;
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1) {
            return;
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(null != seckillOrder) {
            return;
        }
        // 下单
        orderService.seckill(user, goodsVo);
    }
}
