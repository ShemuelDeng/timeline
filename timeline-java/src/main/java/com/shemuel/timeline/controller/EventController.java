package com.shemuel.timeline.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shemuel.timeline.dto.EventDTO;
import com.shemuel.timeline.entity.Timeline;
import com.shemuel.timeline.service.TimelineService;
import com.shemuel.timeline.vo.EventPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.Event;
import com.shemuel.timeline.service.EventService;
import com.shemuel.timeline.common.RestResult;
import com.shemuel.timeline.config.S3Service;
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
    private final TimelineService timelineService;

    private final S3Service s3Service;

    @GetMapping("/list")
    @Operation(summary = "获取时间轴事件表列表，包含时间轴信息")
    public RestResult<EventPageVO<Event>> list(@Validated Event event) {
        // 1. 获取事件分页数据
        IPage<Event> eventPage = eventService.selectPage(event);
        eventPage.getRecords().stream().peek(e -> {
            List<String> images = e.getImages();
            List<String> imageUrls = new ArrayList<>();
            images.stream().forEach(key -> {
                imageUrls.add(s3Service.getFileSignedUrl(key, 3600000));
            });
        });
        EventPageVO<Event> result = new EventPageVO<>(eventPage);
        
        // 4. 由于@Validated注解确保只查询归属于一个时间轴的事件，我们只需要查询一次时间轴信息
        Long timelineId = event.getTimelineId();
        if (timelineId != null) {

            Timeline timeline = timelineService.getById(timelineId);
            result.setTimeline(timeline);
        }
        
        return RestResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取时间轴事件表详情")
    public RestResult<Event> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(eventService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加时间轴事件表")
    public RestResult<Event> add(@RequestBody @Validated Event event) {
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
