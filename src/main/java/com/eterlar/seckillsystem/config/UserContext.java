package com.eterlar.seckillsystem.config;

import com.eterlar.seckillsystem.pojo.User;
import org.springframework.stereotype.Component;

/**
 * @author eterlar
 */
@Component
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }
}
