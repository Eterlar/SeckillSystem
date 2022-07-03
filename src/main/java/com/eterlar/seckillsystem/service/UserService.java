package com.eterlar.seckillsystem.service;

import com.eterlar.seckillsystem.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eterlar.seckillsystem.vo.LoginVo;
import com.eterlar.seckillsystem.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author eterlar
* @description 针对表【t_user】的数据库操作Service
* @createDate 2022-06-10 18:49:59
*/
public interface UserService extends IService<User> {

    /**
     * 登录
     * @param loginvo
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVo loginvo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据 cookie 获取用户
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);

    /**
     * 用户注册
     * @param loginvo
     * @param request
     * @param response
     * @return
     */
    RespBean doSignup(LoginVo loginvo, HttpServletRequest request, HttpServletResponse response);
}
