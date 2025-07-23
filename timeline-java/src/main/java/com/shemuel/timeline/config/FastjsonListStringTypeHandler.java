package com.shemuel.timeline.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;

import java.util.List;

public class FastjsonListStringTypeHandler extends AbstractJsonTypeHandler<List<String>> {

    @Override
    protected List<String> parse(String json) {
        return JSON.parseArray(json, String.class);
    }

    @Override
    protected String toJson(List<String> obj) {
        return JSON.toJSONString(obj);
    }
}
