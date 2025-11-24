package com.shemuel.timeline.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.dev33.satoken.stp.StpUtil;
import com.shemuel.timeline.common.Constants;
import com.shemuel.timeline.common.CustomMode;
import com.shemuel.timeline.common.RepeatType;
import com.shemuel.timeline.common.WindowPosition;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
@Tag(name = "用户提醒表主表， 只记录用户需要的提醒类型，方式管理")
public class TUserReminderController {

    private final TUserReminderService tUserReminderService;


    @PostMapping("/addBySentence")
    @Operation(summary = "一句话创建提醒")
    public RestResult<Object> addBySentence(@RequestBody TUserReminder tUserReminder ) {
        log.info("一句话创建提醒 {}", tUserReminder.getContent());
        return RestResult.success("ok");
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户提醒表主表， 只记录用户需要的提醒类型，方式列表")
    public RestResult<IPage<TUserReminder>> list(TUserReminder tUserReminder) {
        tUserReminder.setUserId(StpUtil.getLoginIdAsLong());
        tUserReminder.setVisible(Constants.active);
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

        tUserReminder.setUserId(StpUtil.getLoginIdAsLong());
        return RestResult.success(tUserReminderService.insert(tUserReminder));
    }

    @PostMapping("/remindAgain")
    @Operation(summary = "添加用户提醒表主表， 只记录用户需要的提醒类型，方式")
    public RestResult<Object> remindAgain(@RequestBody TUserReminder tUserReminder) {
        TUserReminder oldReminder = tUserReminderService.getById(tUserReminder.getId());

        oldReminder.setId(null);
        oldReminder.setRepeatRule(RepeatType.NONE);
        oldReminder.setDoCircle(Constants.NO_active);
        oldReminder.setRemindTime(LocalDateTime.now().plusMinutes(10));
        oldReminder.setVisible(Constants.NO_active);
        oldReminder.setUserId(StpUtil.getLoginIdAsLong());
        oldReminder.setSpecifyTimes(null);
        return RestResult.success(tUserReminderService.insert(oldReminder));
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

        if (Objects.nonNull(tUserReminder.getNotifyDesktopPosition())){
            if (WindowPosition.positionMap.get(tUserReminder.getNotifyDesktopPosition()) == null){
                tUserReminder.setNotifyDesktopPosition(0);
            }
        }


        if (Objects.equals(tUserReminder.getRepeatRule(), RepeatType.NONE)
                && Objects.equals(tUserReminder.getDoCircle(), Constants.NO_active)){
            if (StringUtils.isEmpty(tUserReminder.getSpecifyTimes()) && tUserReminder.getRemindTime().isBefore(LocalDateTime.now())){
                throw new ServiceException("当前首次提醒时间无效");
            }
        }

    }

}
