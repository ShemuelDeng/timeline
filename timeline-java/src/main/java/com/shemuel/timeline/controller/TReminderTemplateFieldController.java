package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TReminderTemplateField;
import com.shemuel.timeline.service.TReminderTemplateFieldService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 模板扩展字段定义表 控制器
 */
@RestController
@RequestMapping("/api/t-reminder-template-field")
@RequiredArgsConstructor
@Tag(name = "模板扩展字段定义表管理")
public class TReminderTemplateFieldController {

    private final TReminderTemplateFieldService tReminderTemplateFieldService;

    @GetMapping("/list")
    @Operation(summary = "获取模板扩展字段定义表列表")
    public RestResult<IPage<TReminderTemplateField>> list(TReminderTemplateField tReminderTemplateField) {
        return RestResult.success(tReminderTemplateFieldService.selectPage(tReminderTemplateField));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取模板扩展字段定义表详情")
    public RestResult<TReminderTemplateField> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tReminderTemplateFieldService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加模板扩展字段定义表")
    public RestResult<Object> add(@RequestBody TReminderTemplateField tReminderTemplateField) {
        return RestResult.success(tReminderTemplateFieldService.insert(tReminderTemplateField));
    }

    @PutMapping("/update")
    @Operation(summary = "修改模板扩展字段定义表")
    public RestResult<Object> edit(@RequestBody TReminderTemplateField tReminderTemplateField) {
        return RestResult.success(tReminderTemplateFieldService.update(tReminderTemplateField));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除模板扩展字段定义表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tReminderTemplateFieldService.deleteByIds(ids));
    }
}
