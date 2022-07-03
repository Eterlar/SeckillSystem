package com.eterlar.seckillsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillSystemApplicationTests {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisScript<Boolean> redisScript;

    @Test
    void contextLoads() {
    }

    @Test
    void createUsers() {
//        UserUtil util = new UserUtil();
//        try {
//            util.createUsers(5);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @Test
    public void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean isLocked = valueOperations.setIfAbsent("k1", "v1");
        if (Boolean.TRUE.equals(isLocked)) {
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在使用，请稍后再试");
        }
    }

    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 设置超时时间, 防止应用在运行过程中一直不释放锁
        Boolean isLocked = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(isLocked)) {
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在使用，请稍后再试");
        }
    }

    @Test
    public void testLock03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLocked = valueOperations.setIfAbsent("k1", value, 50, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(isLocked)) {
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            // 获取锁的随机值
            System.out.println(valueOperations.get("k1"));
            // 执行 lua 脚本
            Boolean res = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(res);
        } else {
            System.out.println("有线程在使用，请稍后再试");
        }
    }
}
