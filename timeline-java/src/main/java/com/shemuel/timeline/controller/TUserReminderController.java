package com.shemuel.timeline.controller;

import java.util.List;
import java.util.Objects;

import cn.dev33.satoken.stp.StpUtil;
import com.shemuel.timeline.common.CustomMode;
import com.shemuel.timeline.common.RepeatType;
import com.shemuel.timeline.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.service.TUserReminderService;
import com.shemuel.timeline.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒表主表， 只记录用户需要的提醒类型，方式 控制器
 */
@RestController
@RequestMapping("/api/t-user-reminder")
@RequiredArgsConstructor
@Tag(name = "用户提醒表主表， 只记录用户需要的提醒类型，方式管理")
public class TUserReminderController {

    private final TUserReminderService tUserReminderService;

    @GetMapping("/list")
    @Operation(summary = "获取用户提醒表主表， 只记录用户需要的提醒类型，方式列表")
    public RestResult<IPage<TUserReminder>> list(TUserReminder tUserReminder) {
        tUserReminder.setUserId(StpUtil.getLoginIdAsLong());
        return RestResult.success(tUserReminderService.selectPage(tUserReminder));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户提醒表主表， 只记录用户需要的提醒类型，方式详情")
    public RestResult<TUserReminder> getInfo(@PathVariable("id") Long id) {
        return RestResult.success(tUserReminderService.getById(id));
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户提醒表主表， 只记录用户需要的提醒类型，方式")
    public RestResult<Object> add(@RequestBody TUserReminder tUserReminder) {
        checkParams(tUserReminder);

        tUserReminder.setUserId(StpUtil.getLoginIdAsLong());
        return RestResult.success(tUserReminderService.insert(tUserReminder));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户提醒表主表， 只记录用户需要的提醒类型，方式")
    public RestResult<Object> edit(@RequestBody TUserReminder tUserReminder) {
        return RestResult.success(tUserReminderService.update(tUserReminder));
    }

    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "删除用户提醒表主表， 只记录用户需要的提醒类型，方式")
    public RestResult<Object> remove(@PathVariable List<Long> ids) {
        return RestResult.success(tUserReminderService.deleteByIds(ids));
    }

    private void checkParams(TUserReminder tUserReminder) {
        if (!RepeatType.checkRepeatType(tUserReminder.getRepeatRule())){
            throw new ServiceException("请选择重复规则");
        }
        if (Objects.equals(tUserReminder.getRepeatRule(), RepeatType.CUSTOM)){
            if (Objects.isNull(tUserReminder.getCustomMode())){
                throw new ServiceException("请选择自定义模式");
            }
        }

        if (Objects.equals(tUserReminder.getRepeatRule(), RepeatType.WEEKLY)){
            if (Objects.isNull(tUserReminder.getRepeatWeekdays())){
                throw new ServiceException("请选择周几");
            }
        }

        if (Objects.equals(tUserReminder.getRepeatRule(), RepeatType.MONTHLY)){
            if (Objects.isNull(tUserReminder.getRepeatMonthDays())){
                throw new ServiceException("请选择每月的哪几天");
            }
        }

        if (Objects.equals(tUserReminder.getRepeatRule(), RepeatType.YEARLY)){
            if (Objects.isNull(tUserReminder.getSpecifyDates())){
                throw new ServiceException("请选择每年哪些天");
            }
        }
    }
}
