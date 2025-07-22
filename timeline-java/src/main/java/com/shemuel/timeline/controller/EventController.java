package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.Event;
import com.shemuel.timeline.service.EventService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 时间轴事件表 控制器
 */
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
@Tag(name = "时间轴事件表管理")
public class EventController {

    private final EventService eventService;

    @GetMapping("/list")
    @Operation(summary = "获取时间轴事件表列表")
    public RestResult<IPage<Event>> list(Event event) {
        return RestResult.success(eventService.selectPage(event));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取时间轴事件表详情")
    public RestResult<Event> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(eventService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加时间轴事件表")
    public RestResult<Object> add(@RequestBody Event event) {
        return RestResult.success(eventService.insert(event));
    }

    @PutMapping("/update")
    @Operation(summary = "修改时间轴事件表")
    public RestResult<Object> edit(@RequestBody Event event) {
        return RestResult.success(eventService.update(event));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除时间轴事件表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(eventService.deleteByIds(ids));
    }
}
