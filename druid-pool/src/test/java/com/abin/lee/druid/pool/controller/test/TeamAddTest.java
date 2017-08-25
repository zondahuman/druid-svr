package com.abin.lee.druid.pool.controller.test;

import com.abin.lee.druid.pool.common.util.JsonUtil;
import com.abin.lee.druid.pool.common.util.RestTemplateUtil;
import com.abin.lee.druid.pool.vo.request.TeamVo;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

/**
 * Created by abin on 2017/4/25 2017/4/25.
 * circular-reference
 * com.abin.lee.circular.main.test
 */
public class TeamAddTest {
    private static String httpUrl = "http://localhost:8300/team/call";;
//    private static String httpUrl = "http://localhost:7100/team/call";


    @Test
    public void testTeamAdd1(){
        TeamVo teamVo = new TeamVo();
        teamVo.setTeamName("abin4");
        String json = JsonUtil.toJson(teamVo);
        Map<String, String> headers = Maps.newHashMap();
        headers.put("STATUS_INPUT", "what");
        String result = RestTemplateUtil.getInstance().httpPost(httpUrl,json, headers);
        System.out.println("result="+result);
    }

    @Test
    public void testTeamAdd2(){
        TeamVo teamVo = new TeamVo();
        teamVo.setTeamName("abin");
        String json = JsonUtil.toJson(teamVo);
        String result = RestTemplateUtil.getInstance().httpPost(httpUrl,json);
        System.out.println("result="+result);
    }

    public static void main(String[] args) {
        String param = "jinlan investment company limited";
        int length = param.length();
        System.out.println(length);
    }

}
