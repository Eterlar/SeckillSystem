package com.eterlar.seckillsystem;

import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.utils.UserUtil;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * @author eterlar
 */

@SpringBootTest
public class UserUtilTest {

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 创建用户
     */
    @Test
    void createUsers() {
        try {
            userUtil.createUsers(5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 模拟生成秒杀路径用于压力测试
     */
    @Test
    void createUsersSeckillPath() {
        int count = 5000;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String goodsId = "1";
        List<User> users = userUtil.initUsers(count);
        String seckillPath = "666666";
        System.out.println("create secKillPath, waiting...");
        for (User user : users) {
            valueOperations.set("seckillPath:" + user.getId() + ":" + goodsId, seckillPath);
        }
        System.out.println("finished...");
    }
}
