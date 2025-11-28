package com.shemuel.timeline.common;

import java.util.Set;

public final class RepeatRuleConst {

    private RepeatRuleConst() {}

    public static final String NONE = "NONE";
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String WORKDAY = "WORKDAY";
    public static final String WEEKEND = "WEEKEND";
    public static final String MONTHLY = "MONTHLY";
    public static final String YEARLY = "YEARLY";

    public static final String CRON = "CRON";


    /**
     * 自定义
     */
    public static final String CUSTOM = "CUSTOM";

    /**
     * 一个主提醒创建多个子提醒
     */
    public static final Set<String> multiItemRepeatTypes = Set.of(WEEKLY, MONTHLY, YEARLY);

    public static boolean isMultiItemRepeatType(String repeatType) {
        return multiItemRepeatTypes.contains(repeatType);
    }


    public static boolean checkRepeatType(String repeatType) {
        return RepeatRuleConst.NONE.equals(repeatType)
                || RepeatRuleConst.DAILY.equals(repeatType)
                || RepeatRuleConst.WEEKLY.equals(repeatType)
                || RepeatRuleConst.WORKDAY.equals(repeatType)
                || RepeatRuleConst.WEEKEND.equals(repeatType)
                || RepeatRuleConst.CRON.equals(repeatType)
                || RepeatRuleConst.MONTHLY.equals(repeatType)
                || RepeatRuleConst.YEARLY.equals(repeatType)
                || RepeatRuleConst.CUSTOM.equals(repeatType);
    }
}
