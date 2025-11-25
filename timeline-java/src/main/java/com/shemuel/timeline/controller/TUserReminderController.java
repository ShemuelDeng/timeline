package com.shemuel.timeline.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.shemuel.timeline.common.*;
import com.shemuel.timeline.entity.TUserReminderItem;
import com.shemuel.timeline.exception.ServiceException;
import com.shemuel.timeline.tools.weather.WeatherTool;
import com.shemuel.timeline.tools.wx.WeComRobotTool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.shemuel.timeline.entity.TUserReminder;
import com.shemuel.timeline.service.TUserReminderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 用户提醒表主表， 只记录用户需要的提醒类型，方式 控制器
 */
@RestController
@RequestMapping("/api/t-user-reminder")
@Slf4j
@Tag(name = "用户提醒表主表， 只记录用户需要的提醒类型，方式管理")
public class TUserReminderController {


    private  final  TUserReminderService tUserReminderService;

    static String prompt = """
                        
            你是一个「智能提醒解析器」，专门把用户的一句话解析成创建提醒所需的字段。
            当前真实时间信息：
            - 今天的日期是：%s
            - 当前时间是：%s
            - 时区：东八区 (GMT+8)    
            【任务】
            - 用户会用中文自然语言输入一句话，例如：
              - “每周的周一周三的11:00提醒我用药”
              - “明天下午 3 点开会提醒我开会”
              - “6 月 1 号每年提醒我给女朋友过生日 提前 3 天提醒”
            - 请你理解这句话的含义，输出一个 JSON，用于创建提醒记录。
                        
            【输出要求（非常重要）】
            1. 只输出 **一段 JSON**，不允许有任何多余文字（不能有注释、中文解释、前后缀）。
            2. JSON 字段如下（如果确定这个字段没有值，就填 null 或合理的默认值）：
                        
            {
              "title": string,                // 你所识别的用户的提醒事项， 例如“用药提醒”、“会议提醒”
              "remindTime": string,           // 用户说的时间点，格式：yyyy-MM-dd'T'HH:mm:ss（例如 2025-11-25T11:00:00）
              "repeatRule": string,           // NONE, DAILY, WEEKLY, MONTHLY, YEARLY, WORKDAY, CUSTOM 之一
              "customMode": string | null,    // 当 repeatRule=CUSTOM 时使用，例如： ANNIVERSARY, BIRTHDAY ，否则为 null
              "repeatWeekdays": string | null,// 每周的星期几，1-7 表示周一到周日，例如 "1,3,5"
              "repeatMonthDays": string | null,// 每月的哪几天，例如 "1,10,20"
            }
                        
            3. 时间一律按「东八区」(GMT+8) 处理。
                        
            【字段规则说明】
                        
            1. repeatRule 选择规则：
               - 一次性提醒（只说了具体某天某个时间，没有“每… / 每隔…”） → "NONE"
               - “每天”、“每晚 9 点” → "DAILY"
               - “每周…周几…”，“每周一三五”，“每周末” → "WEEKLY"
               - “每月…号”，“每月 1 号、15 号” → "MONTHLY"
               - “每年…月…日”，“每年 6 月 1 号”、“每年生日” → "YEARLY"
               - “工作日”、“每个工作日” → "WORKDAY"
               - 其他：（仅识别生日和纪念日即可） → 先用合适的 repeatRule，如果包含生日和纪念日则为 "CUSTOM"
                        
            2. repeatWeekdays 映射：
               - 1 = 周一，2 = 周二，3 = 周三，4 = 周四，5 = 周五，6 = 周六，7 = 周日
               - 例如：
                 - “每周一三” → "1,3"
                 - “每周一到周五” → "1,2,3,4,5"
                 - “每周末” → "6,7"
                        
            3. repeatMonthDays：
               - “每月 1 号、10 号、20 号” → "1,10,20"
                        
            4. customMode 建议：
               - 如果是生日、纪念日相关（“生日”、“纪念日”、“结婚纪念日”等） → customMode = "ANNIVERSARY" 或 "BIRTHDAY"
               - 否则大部分场景 customMode = null
                       
                        
                        
            5. remindTime：
               - 解析一句话中的具体时间：
                 - 如果明确写了时间（“11:00”、“晚上 9 点”、“15:30”）→ 则对应yyyy-MM-dd'T'HH:mm:ss，要准确识别今天明天后天大后天，使用当前日期计算即可
                 - 只说“早上”、“下午”、“晚上”等，你可以合理选择一个默认时间：
                   - 早上 → 09:00
                   - 下午 → 15:00
                   - 晚上 → 20:00
               - 解析日期：
                 - “今天”、“明天”、“后天” → 相对当前日期
                 - “本周五”、“下周一”、“每周一”：
                   - 对于重复提醒：repeatRule=WEEKLY，repeatWeekdays 里填对应星期
                 - “每年 6 月 1 号”：“YEARLY”，specifyDates 可填 "06-01"
                 - 每周，每月，每年， 则remindTime 设为当前时间 
                        
            6. title：
               - 如果句子中有明显的“要做的事”，用作 title,尽量还原用户的事项，但表达要合理：
                 - “提醒我用药” → title = "用药提醒"
                 - “提醒我开会” → title = "会议提醒"
                 - “提醒我给女朋友过生日” → title = "给女朋友过生日"
                        
            【示例】
                        
            示例 1：
            用户输入：“每周的周一周三的11:00提醒我用药”
                        
            期望输出 JSON 结构类似：
                        
            {
              "title": "用药提醒",
              "remindTime": "2025-11-26T11:00:00",
              "repeatRule": "WEEKLY",
              "customMode": null,
              "repeatWeekdays": "1,3",
              "repeatMonthDays": null,
              "specifyDates": null
            }
                        
            示例 2：
            用户输入：“明天下午三点提醒我开会”
                        
            → 输出（只需保证结构和字段含义正确，日期按当前时间推算）：
                        
            {
              "title": "会议提醒",
              "remindTime": "2025-11-26T15:00:00",
              "repeatRule": "NONE",
              "customMode": null,
              "repeatWeekdays": null,
              "repeatMonthDays": null,
              "specifyDates": null,
            }
                        
            示例 3：
            用户输入：“每年 6 月 1 号提醒我给女朋友过生日”
                        
            → 输出示例：
                        
            {
              "title": "生日提醒",
              "remindTime": "2026-05-29T09:00:00",
              "repeatRule": "CUSTOM",
              "customMode": "ANNIVERSARY",
              "repeatWeekdays": null,
              "repeatMonthDays": null,
              "specifyDates": "06-01",
            }
                        
            【最后提醒】
            - 无论用户输入什么，你的回复必须是「只有一段 JSON」，不能包含任何解释文字。
            - 即使有些字段你无法确定，也要按上述字段名给出（用 null 或合理默认值）。
                        
                        
            """;

    ZoneId zone = ZoneId.of("Asia/Shanghai");
    LocalDate today = LocalDate.now(zone);
    LocalTime now = LocalTime.now(zone);

    String todayStr = today.format(DateTimeFormatter.ISO_DATE);              // 2025-11-25
    String nowStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));     // 13:05:00

    String finalPrompt = String.format(prompt, todayStr, nowStr);

    private final ChatClient chatClient;


    public TUserReminderController(DeepSeekChatModel deepSeekChatModel, TUserReminderService tUserReminderService) {
        this.chatClient = ChatClient.builder(deepSeekChatModel).build();
        this.tUserReminderService = tUserReminderService;
    }

    @PostMapping("/addBySentence")
    @Operation(summary = "一句话创建提醒")
    public RestResult<Object> addBySentence(@RequestBody TUserReminder tUserReminder ) {
        log.info("一句话创建提醒 {}", tUserReminder.getContent());
        String content = chatClient
                .prompt()
                .system(finalPrompt)
                .user(tUserReminder.getContent())
                .call()
                .content();

        System.out.println( content);

        TUserReminder parsed = JSON.parseObject(content.replace("```", "").replace("json", ""), TUserReminder.class);
        parsed.setUserId(StpUtil.getLoginIdAsLong());
        if (parsed.getRepeatRule() == RepeatType.WEEKLY
                || parsed.getRepeatRule() == RepeatType.MONTHLY
                || parsed.getRepeatRule() == RepeatType.YEARLY){
            parsed.setRemindTime(LocalDateTime.now());
        }
        tUserReminderService.insert(parsed);
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
        oldReminder.setStatus(RemindStatus.ON);
        oldReminder.setRemindTime(LocalDateTime.now().plusMinutes(10));
        oldReminder.setVisible(Constants.NO_active);
        oldReminder.setUserId(StpUtil.getLoginIdAsLong());
        oldReminder.setSpecifyTimes(null);
        return RestResult.success(tUserReminderService.insert(oldReminder));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户提醒表主表， 只记录用户需要的提醒类型，方式")
    public RestResult<Object> edit(@RequestBody TUserReminder tUserReminder) {
        tUserReminder.setUserId(StpUtil.getLoginIdAsLong());
        checkParams(tUserReminder);
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

            String specifyTimes = tUserReminder.getSpecifyTimes();
            boolean before = tUserReminder.getRemindTime().isBefore(LocalDateTime.now());
            if (StringUtils.isEmpty(specifyTimes) && before){
                throw new ServiceException("当前首次提醒时间无效");
            }

            String[] split = specifyTimes.split(",");

            if (split.length == 1 && before){
                throw new ServiceException("当前首次提醒时间无效");
            }
        }

    }

}
