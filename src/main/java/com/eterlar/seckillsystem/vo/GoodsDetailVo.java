package com.eterlar.seckillsystem.vo;

import com.eterlar.seckillsystem.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品详情返回对象
 * @author eterlar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetailVo {

    private User user;

    private GoodsVo goodsVo;

    private int seckillStatus;

    private int remainSeconds;

}
