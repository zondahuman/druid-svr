package com.abin.lee.druid.pool.service.impl;

import com.abin.lee.druid.pool.dao.TeamMapper;
import com.abin.lee.druid.pool.model.OrderInfo;
import com.abin.lee.druid.pool.model.OrderInfoExample;
import com.abin.lee.druid.pool.model.Team;
import com.abin.lee.druid.pool.model.TeamExample;
import com.abin.lee.druid.pool.service.OrderService;
import com.abin.lee.druid.pool.service.ProcessService;
import com.abin.lee.druid.pool.service.TeamService;
import com.abin.lee.druid.pool.vo.request.TeamVo;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by abin on 2017/4/27 17:48.
 * distribute-svr
 * com.abin.lee.distribute.mycat.service.impl
 */
@Service
public class TeamServiceImpl implements TeamService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    TeamMapper teamMapper;
    @Resource
    ProcessService processService;
    @Resource
    OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(TeamVo teamVo) {
        LOGGER.info("teamVo = " + teamVo);
        Team team = new Team();
        try {
            PropertyUtils.copyProperties(team, teamVo);
            team.setCreateTime(new Date());
            team.setUpdateTime(new Date());
            team.setVersion(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.teamMapper.insert(team);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setName("lee");
        orderInfo.setAge(23);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfo.setVersion(0);
        this.orderService.insert(orderInfo);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                super.afterCompletion(status);
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    LOGGER.info("进件{}事务提交，开始后续流程", status);
                    // 进件完毕，开始后续流程
                    processService.execute(team.getId(), orderInfo.getId());
                }
            }
        });

    }


    @Override
    public Team findById(Integer id) {
        TeamExample example = new TeamExample();
        example.createCriteria().andIdEqualTo(id);
        List<Team> limits = teamMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(limits)) {
            return null;
        } else {
            return limits.get(0);
        }
    }
}
