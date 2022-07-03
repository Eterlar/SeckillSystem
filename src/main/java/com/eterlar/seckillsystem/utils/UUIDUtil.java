package com.eterlar.seckillsystem.utils;

import java.util.UUID;

/**
 * UUID工具类
 * @author eterlar
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
