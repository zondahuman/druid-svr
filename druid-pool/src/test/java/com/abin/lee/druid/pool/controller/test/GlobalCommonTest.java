package com.abin.lee.druid.pool.controller.test;


import com.abin.lee.druid.pool.common.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import java.text.ParseException;

/**
 * Created by abin on 2017/8/2 18:34.
 * gpc-svr
 * com.rpc.mail.client
 */


public class GlobalCommonTest {
    public static void main(String[] args) throws Exception {

        plus();

    }

    public static void plus() throws ParseException {
//        String json = "{\"certNumber\":\"1234567890\", \"balance\":\"-25000\"}";
        String json = "{\"certNumber\":\"1234567890\", \"balance\":654.79}";
        UserInfo userInfo = JsonUtil.decodeJson(json, new TypeReference<UserInfo>() {
        });
        System.out.println(JsonUtil.toJson(userInfo));
    }


    public static class UserInfo{
        private String certNumber;
        private Object balance;

    }


}