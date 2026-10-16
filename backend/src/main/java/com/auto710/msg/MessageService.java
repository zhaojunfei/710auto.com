package com.auto710.msg;

import com.auto710.config.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 消息通知服务：公众号模板消息 / 短信（异步走 RabbitMQ）
 * <p>工单全流程节点：预约确认、接车、施工进度、完工待取车、结算完成、存胎到期提醒、退款。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    public static final String MSG_TYPE_APPOINT_CONFIRM = "预约确认";
    public static final String MSG_TYPE_RECEIVE = "接车";
    public static final String MSG_TYPE_PROGRESS = "施工进度";
    public static final String MSG_TYPE_FINISH = "完工待取车";
    public static final String MSG_TYPE_SETTLE = "结算完成";
    public static final String MSG_TYPE_TIRE_REMIND = "存胎到期提醒";
    public static final String MSG_TYPE_REFUND = "退款";

    private final RabbitTemplate rabbitTemplate;
    private final ConfigService configService;

    /**
     * 发送公众号模板消息
     */
    public void sendTemplate(Long memberId, String msgType, Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("memberId", memberId);
        payload.put("msgType", msgType);
        payload.put("data", data);
        try {
            rabbitTemplate.convertAndSend("auto710.exchange", "msg.template", payload);
        } catch (Exception e) {
            log.warn("模板消息入队失败(忽略): {}", e.getMessage());
        }
    }

    /**
     * 发送短信（后台开关控制）
     */
    public void sendSms(String phone, String content) {
        if (!configService.getBool(ConfigService.SMS_ENABLED, false)) {
            log.debug("短信通道未开启，跳过发送: {}", phone);
            return;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("phone", phone);
        payload.put("content", content);
        try {
            rabbitTemplate.convertAndSend("auto710.exchange", "sms.send", payload);
        } catch (Exception e) {
            log.warn("短信入队失败(忽略): {}", e.getMessage());
        }
    }

    /**
     * 预约超时自动释放
     */
    public void pushOrderTimeout(Long appointmentId) {
        try {
            rabbitTemplate.convertAndSend("auto710.exchange", "order.timeout", appointmentId);
        } catch (Exception e) {
            log.warn("预约超时任务入队失败(忽略): {}", e.getMessage());
        }
    }

    /**
     * 存胎换季到期提醒
     */
    public void pushTireRemind(Long storageOrderId) {
        try {
            rabbitTemplate.convertAndSend("auto710.exchange", "tire.remind", storageOrderId);
        } catch (Exception e) {
            log.warn("存胎提醒入队失败(忽略): {}", e.getMessage());
        }
    }
}
