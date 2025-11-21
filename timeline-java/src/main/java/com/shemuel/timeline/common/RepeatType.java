package com.shemuel.timeline.common;

import java.util.Set;

public final class RepeatType {

    private RepeatType() {}

    public static final String NONE = "NONE";
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String WORKDAY = "WORKDAY";
    public static final String WEEKEND = "WEEKEND";
    public static final String MONTHLY = "MONTHLY";
    public static final String YEARLY = "YEARLY";


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
        return RepeatType.NONE.equals(repeatType)
                || RepeatType.DAILY.equals(repeatType)
                || RepeatType.WEEKLY.equals(repeatType)
                || RepeatType.WORKDAY.equals(repeatType)
                || RepeatType.WEEKEND.equals(repeatType)
                || RepeatType.MONTHLY.equals(repeatType)
                || RepeatType.YEARLY.equals(repeatType)
                || RepeatType.CUSTOM.equals(repeatType);
    }
}
