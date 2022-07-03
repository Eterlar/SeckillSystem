package com.eterlar.seckillsystem.controller;

import com.eterlar.seckillsystem.service.UserService;
import com.eterlar.seckillsystem.vo.LoginVo;
import com.eterlar.seckillsystem.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;

    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    /**
     * 登录
     * @param loginvo
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginvo, HttpServletRequest request, HttpServletResponse response) {
        return userService.doLogin(loginvo, request, response);
    }
}
