package com.shemuel.timeline.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TUserReminderField;
import com.shemuel.timeline.service.TUserReminderFieldService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒的扩展字段值表 控制器
 */
@RestController
@RequestMapping("/api/t-user-reminder-field")
@RequiredArgsConstructor
@Tag(name = "用户提醒的扩展字段值表管理")
public class TUserReminderFieldController {

    private final TUserReminderFieldService tUserReminderFieldService;

    @GetMapping("/list")
    @Operation(summary = "获取用户提醒的扩展字段值表列表")
    public RestResult<IPage<TUserReminderField>> list(TUserReminderField tUserReminderField) {
        return RestResult.success(tUserReminderFieldService.selectPage(tUserReminderField));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户提醒的扩展字段值表详情")
    public RestResult<TUserReminderField> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tUserReminderFieldService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户提醒的扩展字段值表")
    public RestResult<Object> add(@RequestBody TUserReminderField tUserReminderField) {
        return RestResult.success(tUserReminderFieldService.insert(tUserReminderField));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户提醒的扩展字段值表")
    public RestResult<Object> edit(@RequestBody TUserReminderField tUserReminderField) {
        return RestResult.success(tUserReminderFieldService.update(tUserReminderField));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除用户提醒的扩展字段值表")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tUserReminderFieldService.deleteByIds(ids));
    }
}
