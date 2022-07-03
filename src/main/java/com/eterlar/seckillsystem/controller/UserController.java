package com.eterlar.seckillsystem.controller;

import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author eterlar
 */
@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * 用户信息（该接口主要用于测试）
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {

        return RespBean.success(user);
    }
}
