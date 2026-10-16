package com.auto710.mq;

import com.auto710.common.util.EncryptUtil;
import com.auto710.msg.MessageService;
import com.auto710.wechat.WechatService;
import com.auto710.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RabbitMQ 消费者：模板消息、短信、存胎提醒、预约超时、每日日报
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqListeners {

    private final WechatService wechatService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_MSG_TEMPLATE)
    public void onTemplateMessage(Map<String, Object> payload) {
        log.info("[MQ] 消费模板消息: {}", payload);
        try {
            Object msgType = payload.get("msgType");
            Map<String, String> data = (Map<String, String>) payload.get("data");
            String openid = payload.get("openid") == null ? null : String.valueOf(payload.get("openid"));
            if (openid != null) {
                wechatService.sendTemplateMessage(openid, String.valueOf(msgType), data);
            }
        } catch (Exception e) {
            log.warn("[MQ] 模板消息处理失败: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_SMS_SEND)
    public void onSms(Map<String, Object> payload) {
        // 短信通道为可选能力，未接入时仅记录，预留短信服务商对接
        log.info("[MQ] 发送短信: phone={}", payload.get("phone"));
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_TIRE_REMIND)
    public void onTireRemind(Long storageOrderId) {
        log.info("[MQ] 存胎到期提醒任务: storageOrderId={}", storageOrderId);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ORDER_TIMEOUT)
    public void onOrderTimeout(Long appointmentId) {
        log.info("[MQ] 预约超时延迟任务已登记: appointmentId={}", appointmentId);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_DAILY_REPORT)
    public void onDailyReport(Object payload) {
        log.info("[MQ] 生成每日经营日报: {}", payload);
    }
}
