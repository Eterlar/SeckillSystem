package com.eterlar.seckillsystem.config;

import com.eterlar.seckillsystem.annotation.AccessLimit;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.service.UserService;
import com.eterlar.seckillsystem.utils.CookieUtil;
import com.eterlar.seckillsystem.vo.RespBean;
import com.eterlar.seckillsystem.enums.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author eterlar
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (null == accessLimit) {
                // 代表没有被这个注解标识,直接往下执行就可以
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxTimes = accessLimit.maxTimes();
            boolean needLogin = accessLimit.needLogin();

            String requestURI = request.getRequestURI();
            if (needLogin) {
                if (null == user) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                requestURI += ":" + user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer counter = (Integer) valueOperations.get(requestURI);
            if (null == counter) {
                valueOperations.set(requestURI, 1, seconds, TimeUnit.SECONDS);
            } else if (counter < maxTimes) {
                valueOperations.increment(requestURI);
            } else {
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 构建返回对象
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out;
        try {
            out = response.getWriter();
            RespBean respBean = RespBean.error(respBeanEnum);
            out.write(new ObjectMapper().writeValueAsString(respBean));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.flush();
        out.close();
    }

    /**
     * 获取当前登录用户
     * @return 登录用户
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(!StringUtils.hasText(userTicket)) {
            return null;
        }
        return userService.getUserByCookie(userTicket, request, response);
    }
}
