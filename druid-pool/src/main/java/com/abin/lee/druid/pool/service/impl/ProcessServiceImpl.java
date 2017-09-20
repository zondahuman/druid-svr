package com.abin.lee.druid.pool.service.impl;

import com.abin.lee.druid.pool.common.util.JsonUtil;
import com.abin.lee.druid.pool.dao.TeamMapper;
import com.abin.lee.druid.pool.model.Team;
import com.abin.lee.druid.pool.service.ProcessService;
import com.abin.lee.druid.pool.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by abin on 2017/9/18 22:51.
 * druid-svr
 * com.abin.lee.druid.pool.service.impl
 */
@Service
public class ProcessServiceImpl implements ProcessService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    ThreadPoolTaskExecutor asyncTaskExecutor;
    @Resource
    TeamMapper teamMapper;
    @Resource
    TeamService teamService;


    @Override
    public void execute(int id) {
        this.asyncTaskExecutor.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                Team team = teamService.findById(id);
                LOGGER.info("team............= "+ JsonUtil.toJson(team));

            }
        }));
    }






}
