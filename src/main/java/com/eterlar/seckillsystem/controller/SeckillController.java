package com.eterlar.seckillsystem.controller;

import com.eterlar.seckillsystem.annotation.AccessLimit;
import com.eterlar.seckillsystem.vo.SeckillMessage;
import com.eterlar.seckillsystem.pojo.SeckillOrder;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.rabbitmq.MQSender;
import com.eterlar.seckillsystem.service.GoodsService;
import com.eterlar.seckillsystem.service.SeckillOrderService;
import com.eterlar.seckillsystem.utils.JsonUtil;
import com.eterlar.seckillsystem.vo.GoodsVo;
import com.eterlar.seckillsystem.vo.RespBean;
import com.eterlar.seckillsystem.enums.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eterlar
 */
@RestController
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Resource
    private GoodsService goodsService;

    @Resource
    private SeckillOrderService seckillOrderService;


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MQSender mqSender;

    /**
     * 内存标记
     */
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @PostMapping("/{path}/doSeckill")
    public RespBean doSeckill(User user, Long goodsId, @PathVariable String path) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 通过内存标记减少 redis的访问。判断是否有库存
        if (Boolean.TRUE.equals(emptyStockMap.get(goodsId))) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 检查秒杀地址是否正确
        boolean check = seckillOrderService.checkPath(user, goodsId, path);
        if (Boolean.FALSE.equals(check)) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(null != seckillOrder) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        // 预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        assert stock != null;
        if (stock.intValue() < 0) {
            // 0 还会再减一次，所以在这里加一次
            valueOperations.increment("seckillGoods:" + goodsId);
            emptyStockMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));

        return RespBean.success(0);
    }

    /**
     * 初始化方法，将商品库存数量加载到 redis 中
     */
    @Override
    public void afterPropertiesSet(){
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue()
                    .set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });

    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId: -1 秒杀失败，0 排队中
     */
    @GetMapping(value = "/getResult")
    public RespBean getResult(User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/path")
    @AccessLimit(seconds=5, maxTimes=5)
    public RespBean getPath(User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        String str = seckillOrderService.createPath(user, goodsId);
        return RespBean.success(str);
    }
}
