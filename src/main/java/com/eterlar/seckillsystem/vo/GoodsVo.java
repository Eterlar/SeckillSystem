package com.eterlar.seckillsystem.vo;

import com.eterlar.seckillsystem.pojo.Goods;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 * @author eterlar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods {

    /**
     * 秒杀价
     */
    private BigDecimal seckillPrice;

    /**
     * 库存数量
     */
    private Integer stockCount;

    /**
     * 秒杀开始时间
     */
    private Date startDate;

    /**
     * 秒杀结束时间
     */
    private Date endDate;
}
