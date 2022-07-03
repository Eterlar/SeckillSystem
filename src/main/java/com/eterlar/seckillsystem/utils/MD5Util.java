package com.eterlar.seckillsystem.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * MD5 工具类
 * @author eterlar
 */
@Component
public class MD5Util {

    /**
     * 加密盐
     */
    private static final String SALT = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 用户前端输入密码后加密
     * @param inputPass 前端输入的密码
     * @return 第一次加密密文
     */
    public static String inputPassToFromPass(String inputPass) {
        // 混淆一下，不直接使用盐 12c3
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    /**
     * 存入数据库之前加密
     * @param formPass 前端输入后加密后的密码
     * @param salt 加密盐
     * @return 返回第二次加密后密文
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 模拟前端输入到存入数据库的过程
     * @param inputPass 前端输入的密码
     * @param salt 加密盐
     * @return 数据库中存放的真实密码
     */
    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = formPassToDBPass(fromPass, salt);
        return dbPass;
    }
}
