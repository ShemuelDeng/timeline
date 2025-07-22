package com.shemuel.timeline.controller;

import com.shemuel.timeline.annotation.AccessLimit;
import com.shemuel.timeline.common.RestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-11-20:26
 * @Description:
 */
@RestController
@AccessLimit
public class HealthyController {

    @GetMapping("/ok")
    public RestResult<String> healthy() {
        return RestResult.success("Yes, I'm ok!");
    }
}
