package com.abin.lee.druid.pool.service;


import com.abin.lee.druid.pool.model.OrderInfo;
import com.abin.lee.druid.pool.vo.request.TeamVo;

/**
 * Created by abin on 2017/4/27 17:47.
 * distribute-svr
 * com.abin.lee.distribute.mycat.service
 */
public interface OrderService {

    void insert(OrderInfo orderInfo);

    OrderInfo findById(Integer id);

    void update(OrderInfo orderInfo);


}
