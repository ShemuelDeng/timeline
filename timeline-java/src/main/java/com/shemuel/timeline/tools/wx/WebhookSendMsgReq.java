package com.shemuel.timeline.tools.wx;

import com.alibaba.fastjson.annotation.JSONField;
import com.shemuel.timeline.common.WxMsgTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangdong
 * @date 2022/4/26 16:50
 */
@NoArgsConstructor
@Data
public class WebhookSendMsgReq {

	@JSONField(name = "msgtype")
	private String msgtype;
	@JSONField(name = "text")
	private TextDto text;

	private MarkDownDto markdown;

	public static WebhookSendMsgReq text(String content){
		WebhookSendMsgReq webhookSendMsgReq = new WebhookSendMsgReq();
		webhookSendMsgReq.setMsgtype(WxMsgTypeEnum.TEXT.getType());
		TextDto textDto = new TextDto();
		textDto.setContent(content);
		webhookSendMsgReq.setText(textDto);
		return webhookSendMsgReq;
	}

	public static WebhookSendMsgReq markdown(String content){
		WebhookSendMsgReq webhookSendMsgReq = new WebhookSendMsgReq();
		webhookSendMsgReq.setMsgtype(WxMsgTypeEnum.MARK_DOWN.getType());
		MarkDownDto textDto = new MarkDownDto();
		textDto.setContent(content);
		webhookSendMsgReq.setMarkdown(textDto);
		return webhookSendMsgReq;
	}
}
