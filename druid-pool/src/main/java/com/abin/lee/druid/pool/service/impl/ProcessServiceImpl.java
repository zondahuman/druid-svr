package com.abin.lee.druid.pool.service.impl;

import com.abin.lee.druid.pool.common.util.JsonUtil;
import com.abin.lee.druid.pool.dao.TeamMapper;
import com.abin.lee.druid.pool.model.OrderInfo;
import com.abin.lee.druid.pool.model.Team;
import com.abin.lee.druid.pool.service.OrderService;
import com.abin.lee.druid.pool.service.ProcessService;
import com.abin.lee.druid.pool.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

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
    @Resource
    OrderService orderService;

    @Override
    @Async
    public void execute(int id, int orderId) {
        Team team = teamService.findById(id);
        LOGGER.info("team............= " + JsonUtil.toJson(team));

        OrderInfo orderInfo = orderService.findById(orderId);
        LOGGER.info("orderInfo............= " + JsonUtil.toJson(orderInfo));
//        Integer version = orderInfo.getVersion() + 1;
//        orderInfo.setVersion(version);
        orderInfo.setUpdateTime(new Date());
        this.orderService.update(orderInfo);
    }


    public void updateTeam(int id) {
        this.asyncTaskExecutor.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                Team team = teamService.findById(id);
                LOGGER.info("updateTeam----team............= " + JsonUtil.toJson(team));
                team.setUpdateTime(new Date());
                teamService.update(team);
            }
        }));
    }

    public void updateOrder(int id) {
        this.asyncTaskExecutor.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                OrderInfo orderInfo = orderService.findById(id);
                LOGGER.info("updateTeam----orderInfo............= " + JsonUtil.toJson(orderInfo));
                orderService.update(orderInfo);
            }
        }));
    }

    public void updateOrder11(int id) throws InterruptedException {
        int totalThread = 3;
        long start = System.currentTimeMillis();
        CountDownLatch countDown = new CountDownLatch(totalThread);
        for(int i = 0; i < totalThread; i++) {
            final String threadName = "Thread " + i;
            new Thread(() -> {
                System.out.println(String.format("%s\t%s %s", new Date(), threadName, "started"));
                try {
                    Thread.sleep(10000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                countDown.countDown();
                System.out.println(String.format("%s\t%s %s", new Date(), threadName, "ended"));
            }).start();;
        }
        countDown.await();
        long stop = System.currentTimeMillis();
        System.out.println(String.format("Total time : %sms", (stop - start)));
    }


    public void batchProcess(int id, int orderId) {
        LOGGER.info("batchProcess----id={}  orderId={}", id, orderId, "-------start");
        int totalThread = 3;
        long start = System.currentTimeMillis();
        CountDownLatch countDown = new CountDownLatch(totalThread);
        try {
            this.asyncTaskExecutor.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateTeam(id);
                    }catch(Exception e){
                        LOGGER.error("e={}", e);
                    }finally {
                        countDown.countDown();
                    }
                }
            }));
            this.asyncTaskExecutor.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateOrder(orderId);
                    }catch(Exception e){
                        LOGGER.error("e={}", e);
                    }finally {
                        countDown.countDown();
                    }
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                countDown.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        long stop = System.currentTimeMillis();
        LOGGER.info("batchProcess----id={}  orderId={}", id, orderId, "-------end");

    }

}
