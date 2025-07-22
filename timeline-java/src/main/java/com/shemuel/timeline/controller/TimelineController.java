package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.Timeline;
import com.shemuel.timeline.service.TimelineService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 时间轴表 控制器
 */
@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@Tag(name = "时间轴表管理")
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/list")
    @Operation(summary = "获取时间轴表列表")
    public RestResult<IPage<Timeline>> list(Timeline timeline) {
        return RestResult.success(timelineService.selectPage(timeline));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取时间轴表详情")
    public RestResult<Timeline> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(timelineService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加时间轴表")
    public RestResult<Object> add(@RequestBody Timeline timeline) {
        return RestResult.success(timelineService.insert(timeline));
    }

    @PutMapping("/update")
    @Operation(summary = "修改时间轴表")
    public RestResult<Object> edit(@RequestBody Timeline timeline) {
        return RestResult.success(timelineService.update(timeline));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除时间轴表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(timelineService.deleteByIds(ids));
    }
}
