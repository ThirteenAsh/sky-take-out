package com.sky.task;

import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    private static final String SHOP_STATUS_KEY = "SHOP STATUS";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时处理订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void TimeOutOrder(){
        log.info("开始定时处理超时订单,{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        //查询超时订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if (!CollectionUtils.isEmpty(ordersList)){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 定时处理派送订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("开始处理派送订单,{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (!CollectionUtils.isEmpty(ordersList)){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 每天8点自动营业
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void autoOpenShop() {
        redisTemplate.opsForValue().set(SHOP_STATUS_KEY, StatusConstant.ENABLE);
        log.info("自动营业，执行时间:{}", LocalDateTime.now());
    }

    /**
     * 每天18点自动打烊
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void autoCloseShop() {
        redisTemplate.opsForValue().set(SHOP_STATUS_KEY, StatusConstant.DISABLE);
        log.info("自动打烊，执行时间:{}", LocalDateTime.now());
    }
}
