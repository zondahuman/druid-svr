package com.abin.lee.druid.pool.service.impl;

import com.abin.lee.druid.pool.dao.OrderInfoMapper;
import com.abin.lee.druid.pool.model.OrderInfo;
import com.abin.lee.druid.pool.model.OrderInfoExample;
import com.abin.lee.druid.pool.service.OrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by abin on 2017/4/27 17:48.
 * distribute-svr
 * com.abin.lee.distribute.mycat.service.impl
 */
@Service
public class OrderServiceImpl implements OrderService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    OrderInfoMapper orderInfoMapper;



    @Override
    public void insert(OrderInfo orderInfo) {
        this.orderInfoMapper.insert(orderInfo);
    }


    @Override
    public OrderInfo findById(Integer id) {
        OrderInfoExample example = new OrderInfoExample();
        example.createCriteria().andIdEqualTo(id);
        List<OrderInfo> limits = orderInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(limits)) {
            return null;
        } else {
            return limits.get(0);
        }
    }
}
