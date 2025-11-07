package com.shemuel.timeline.tools.wx;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangdong
 * @date 2022/4/15 20:27
 */
@NoArgsConstructor
@Data
public class TextDto {
	@JSONField(name = "content")
	private String content;
}
