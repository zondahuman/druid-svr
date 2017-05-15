package com.abin.lee.druid.pool.vo.response;

import com.abin.lee.druid.pool.enums.RootEnum;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by abin on 2017/4/25 2017/4/25.
 * circular-reference
 * com.abin.lee.circular.main.vo.response
 */
//@Getter
//@Setter
@Data
public class BaseVo {

    private String status;
    private String message;
    private Map<String, Object> data = Maps.newHashMap();


    public static BaseVo success(String message) {
        BaseVo vo = new BaseVo();
        vo.setStatus(RootEnum.SUCCESS.name());
        vo.setMessage(message);
        return vo;
    }


    public static BaseVo success() {
        return success("");
    }

    public static BaseVo success(Data data) {
        BaseVo vo = new BaseVo();
        vo.setStatus(RootEnum.SUCCESS.name());
        return vo;
    }


    public static BaseVo error() {
        BaseVo vo = new BaseVo();
        vo.setStatus("-1");
        vo.setMessage("服务器异常");
        return vo;
    }


    public static BaseVo error(String status, String message) {
        BaseVo vo = new BaseVo();
        vo.setStatus(status);
        vo.setMessage(message);
        return vo;
    }

    public static BaseVo addRows(List<?> list) {
        return BaseVo.success().addData("rows", list);
    }


    public BaseVo addData(String key, Object value) {
        data.put(key, value);
        return this;
    }





}
