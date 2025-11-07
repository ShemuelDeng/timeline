package com.shemuel.timeline.common;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 企微发消息类型
 * @author zhangdong
 * @date 2022/5/23 16:38:01
 */
@Getter
public enum WxMsgTypeEnum {
    /**
     *
     */
    TEXT("text", "文本"),
    IMAGE("image", "图片"),
    VIDEO("video", "视频"),
    FILE("file", "文件"),
    MARK_DOWN("markdown", "markdown"),
    ;

    WxMsgTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private final String type;
    private final String desc;

    public static String getDesc(String type){
        return Arrays.stream(values()).filter(pullGroupStatusEnum -> Objects.equals(pullGroupStatusEnum.getType(), type))
                .map(WxMsgTypeEnum::getDesc).findFirst().orElse(null);
    }
}
