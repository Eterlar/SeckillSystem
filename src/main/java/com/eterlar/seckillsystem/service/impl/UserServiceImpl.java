package com.eterlar.seckillsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eterlar.seckillsystem.exception.GlobalException;
import com.eterlar.seckillsystem.mapper.UserMapper;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.service.UserService;
import com.eterlar.seckillsystem.utils.CookieUtil;
import com.eterlar.seckillsystem.utils.MD5Util;
import com.eterlar.seckillsystem.utils.UUIDUtil;
import com.eterlar.seckillsystem.vo.LoginVo;
import com.eterlar.seckillsystem.vo.RespBean;
import com.eterlar.seckillsystem.enums.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

/**
* @author eterlar
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2022-06-10 18:49:59
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 登录
     * @param loginvo
     * @return 成功与否
     */
    @Override
    public RespBean doLogin(LoginVo loginvo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginvo.getMobile();
        String password = loginvo.getPassword();
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if(!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成 cookie
        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:"+ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);
    }

    /**
     * 根据 cookie 获取用户
     * @param userTicket 用户 ticket
     * @return Cookie对应用户
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasText(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        // 以防万一，我们再次设置一下
        if (null != user) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    /**
     * 更新密码
     * @param userTicket
     * @param password 前端输入的密码
     * @param request request 请求
     * @param response response 请求
     * @return 统一返回结果
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(null == user) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int res = userMapper.updateById(user);
        if (1 == res) {
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }

    /**
     * 用户注册
     * @param loginvo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doSignup(LoginVo loginvo, HttpServletRequest request, HttpServletResponse response) {
        // 插入数据库
        String mobile = loginvo.getMobile();
        String salt = "1a2b3c";
        String password = MD5Util.formPassToDBPass(loginvo.getPassword(), salt);
        loginvo.setPassword(password);
        User user = new User();
        user.setId(Long.valueOf(mobile));
        user.setNickname(mobile);
        user.setPassword(password);
        user.setSalt(salt);
        user.setRegisterDate(new Date());
        user.setLoginCount(1);

        int insert = userMapper.insert(user);
        if (insert > 0) {
            // 生成 cookie
            String ticket = UUIDUtil.uuid();
            redisTemplate.opsForValue().set("user:"+ticket, user);
            CookieUtil.setCookie(request, response, "userTicket", ticket);
            return RespBean.success(ticket);
        } else {
            throw new GlobalException(RespBeanEnum.SING_UP_ERROR);
        }
    }
}
