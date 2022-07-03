package com.eterlar.seckillsystem.controller;

import com.eterlar.seckillsystem.service.UserService;
import com.eterlar.seckillsystem.vo.LoginVo;
import com.eterlar.seckillsystem.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author eterlar
 */
@Controller // 不能用 RestController（会返回对象而不是页面跳转），
@RequestMapping("/signup")
@Slf4j
public class SignupController {

    @Resource
    private UserService userService;

    /**
     * 跳转到注册页面
     * @return
     */
    @RequestMapping("/toSignup")
    public String toSignup() {
        return "signup";
    }

    @PostMapping("/doSignup")
    @ResponseBody
    public RespBean doSignup(@Valid LoginVo loginvo, HttpServletRequest request, HttpServletResponse response) {
        return userService.doSignup(loginvo, request, response);
    }
}
