package com.eterlar.seckillsystem.controller;

import com.eterlar.seckillsystem.exception.GlobalException;
import com.eterlar.seckillsystem.pojo.Order;
import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.service.OrderService;
import com.eterlar.seckillsystem.vo.OrderDetailVo;
import com.eterlar.seckillsystem.vo.RespBean;
import com.eterlar.seckillsystem.enums.RespBeanEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author eterlar
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetail = orderService.detail(orderId);
        return RespBean.success(orderDetail);
    }

    /**
     * 获取用户所有订单
     * @param user
     * @return
     */
    @GetMapping("/orderList")
    public String orderList(Model model, User user) {
        if (null == user) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        List<Order> orders = orderService.orderList(user.getId());
        model.addAttribute("orders",orders);
        return "orderList";
    }
}
