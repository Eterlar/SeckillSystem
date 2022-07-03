package com.eterlar.seckillsystem.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码校验类
 * @author eterlar
 */
public class ValidatorUtil {

    private static final Pattern MOBILE_PATTEN = Pattern.compile("1([3-9])[0-9]{9}$");

    /**
     * 手机号码校验
     * @param mobile 手机号
     * @return 手机号是否合法
     */
    public static boolean isMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        Matcher matcher = MOBILE_PATTEN.matcher(mobile);
        return matcher.matches();
    }
}
