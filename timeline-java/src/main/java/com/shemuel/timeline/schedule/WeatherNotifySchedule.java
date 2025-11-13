package com.shemuel.timeline.schedule;

import com.shemuel.timeline.tools.weather.WeatherTool;
import com.shemuel.timeline.tools.wx.WeComRobotTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 天气通知
 * @author dengsx
 * @create 2025/04/19
 **/
@Slf4j
@Configuration
@EnableScheduling
public class WeatherNotifySchedule {

    private final ChatClient chatClient;
    private final WeatherTool weatherTool;
    private final WeComRobotTool weComRobotTool;

    public WeatherNotifySchedule(DeepSeekChatModel deepSeekChatModel, WeatherTool weatherTool, WeComRobotTool weComRobotTool) {
        this.chatClient = ChatClient.builder(deepSeekChatModel).build();
        this.weatherTool = weatherTool;
        this.weComRobotTool = weComRobotTool;
    }


    String prompt = """
                请你帮我自动执行以下流程，生成并发送早安问候：

                1. 使用 `weatherForecast` 工具查询城市的天气

                2. 该工具的查询结果会返回多个城市的天气， 每个城市仅会返回当天的天气，请将结果以 Markdown 格式总结后输出：
                   - 天气格式如下：
                      - 白天天气: 阴
                      - 夜间天气: 阴
                      - 气温: 28°C ~ 21°C
                      - 风向: 东风
                   
                   - 若天气为除“晴”、“阴”、“多云”以外的情况（例如“多云转雨”、“小雨”、“雷阵雨”等），视为异常天气；
                   - 对异常天气的部分使用以下格式高亮：
                     <font color="warning">这里写内容</font>
                   - 根据天气生成出行建议的温馨提示，例如：温馨提示：北京地区夜间有小雨，出行请注意防雨保暖。龙岗区天气稳定，适合出行。

                3. 同时生成一句“每日一言”，表达积极或温暖的情绪。

                4. 最终输出内容结构如下（Markdown）仅作为给你的参考：
                🌞 **早安问候**

                📍 **今日天气简报：**
                **大兴区** 
                   - 白天天气: 阴
                   - 夜间天气: 阴
                   - 气温: 28°C ~ 21°C
                   - 风向: 东风        
                **朝阳区**
                   - 白天天气: 阴
                   - 夜间天气: 阴
                   - 气温: 28°C ~ 21°C
                   - 风向: 东风
                **龙岗区**
                   - 白天天气: 阴
                   - 夜间天气: 阴
                   - 气温: 28°C ~ 21°C
                   - 风向: 东风          
                **温馨提示**：这里根据天气生成温馨提示
                💬 **今日寄语：**
                “这里随机生成”

                ☕ 祝你新的一天元气满满！

                5. 将生成的Markdown文本通过 `sendGroupTool` 工具发送到企业微信群。
                """;
    /**
     * 每天7点执行
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void weatherNotify() {
        int retryTimes  = 0 ;

        int delayInterval = 60000;

        while (retryTimes < 3){
            try {
                String content = chatClient.prompt()
                        .user(prompt)
                        .tools(weatherTool, weComRobotTool)
                        .call()
                        .content();
                log.info("weatherNotify called retry times {}, content: {}",retryTimes,  content);
                break;
            } catch (Exception e) {
                retryTimes++;
                log.error("weatherNotify error,", e);
                try {
                    Thread.sleep(delayInterval * retryTimes );
                } catch (InterruptedException ex) {
                }
            }
        }


    }

}
