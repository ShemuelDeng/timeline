package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TReminderCategory;
import com.shemuel.timeline.service.TReminderCategoryService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 提醒分类表 控制器
 */
@RestController
@RequestMapping("/api/t-reminder-category")
@RequiredArgsConstructor
@Tag(name = "提醒分类表管理")
public class TReminderCategoryController {

    private final TReminderCategoryService tReminderCategoryService;

    @GetMapping("/list")
    @Operation(summary = "获取提醒分类表列表")
    public RestResult<IPage<TReminderCategory>> list(TReminderCategory tReminderCategory) {
        return RestResult.success(tReminderCategoryService.selectPage(tReminderCategory));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取提醒分类表详情")
    public RestResult<TReminderCategory> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tReminderCategoryService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加提醒分类表")
    public RestResult<Object> add(@RequestBody TReminderCategory tReminderCategory) {
        return RestResult.success(tReminderCategoryService.insert(tReminderCategory));
    }

    @PutMapping("/update")
    @Operation(summary = "修改提醒分类表")
    public RestResult<Object> edit(@RequestBody TReminderCategory tReminderCategory) {
        return RestResult.success(tReminderCategoryService.update(tReminderCategory));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除提醒分类表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tReminderCategoryService.deleteByIds(ids));
    }
}
