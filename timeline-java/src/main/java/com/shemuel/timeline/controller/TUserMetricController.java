package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TUserMetric;
import com.shemuel.timeline.service.TUserMetricService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL） 控制器
 */
@RestController
@RequestMapping("/api/t-user-metric")
@RequiredArgsConstructor
@Tag(name = "用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）管理")
public class TUserMetricController {

    private final TUserMetricService tUserMetricService;

    @GetMapping("/list")
    @Operation(summary = "获取用户指标统计（单表，按指标类型区分；今日类带日期，总量类日期为NULL）列表")
    public RestResult<List<TUserMetric>> list(TUserMetric tUserMetric) {
        return RestResult.success(tUserMetricService.selectList(tUserMetric));
    }
}
