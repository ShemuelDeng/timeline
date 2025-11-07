package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TReminderTemplate;
import com.shemuel.timeline.service.TReminderTemplateService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 提醒模板表 控制器
 */
@RestController
@RequestMapping("/api/t-reminder-template")
@RequiredArgsConstructor
@Tag(name = "提醒模板表管理")
public class TReminderTemplateController {

    private final TReminderTemplateService tReminderTemplateService;

    @GetMapping("/list")
    @Operation(summary = "获取提醒模板表列表")
    public RestResult<IPage<TReminderTemplate>> list(TReminderTemplate tReminderTemplate) {
        return RestResult.success(tReminderTemplateService.selectPage(tReminderTemplate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取提醒模板表详情")
    public RestResult<TReminderTemplate> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tReminderTemplateService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加提醒模板表")
    public RestResult<Object> add(@RequestBody TReminderTemplate tReminderTemplate) {
        return RestResult.success(tReminderTemplateService.insert(tReminderTemplate));
    }

    @PutMapping("/update")
    @Operation(summary = "修改提醒模板表")
    public RestResult<Object> edit(@RequestBody TReminderTemplate tReminderTemplate) {
        return RestResult.success(tReminderTemplateService.update(tReminderTemplate));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除提醒模板表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tReminderTemplateService.deleteByIds(ids));
    }
}
