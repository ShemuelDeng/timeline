package com.shemuel.timeline.tools.wx;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 天气推送
 */
@Data
@Accessors(chain = true)
public class WxWeatherNoticeDataVO {
    /**
     * 城市
     */
    private PropertyVO phrase2;

    /**
     * 天气
     */
    private PropertyVO phrase3;
    /**
     * 温度
     */
    private PropertyVO character_string4;
    /**
     * 降水概率
     */
    private PropertyVO character_string21;
    /**
     * 温馨提示
     */
    private PropertyVO thing5;

}
