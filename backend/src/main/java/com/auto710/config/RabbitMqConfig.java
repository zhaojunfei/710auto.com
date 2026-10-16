package com.auto710.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 队列配置
 * <p>队列：预约超时释放、模板消息、短信、存胎到期提醒、每日经营日报。</p>
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "auto710.exchange";
    public static final String DLX_EXCHANGE = "auto710.dlx.exchange";

    public static final String QUEUE_ORDER_TIMEOUT = "order.timeout";
    public static final String QUEUE_MSG_TEMPLATE = "msg.template";
    public static final String QUEUE_SMS_SEND = "sms.send";
    public static final String QUEUE_TIRE_REMIND = "tire.remind";
    public static final String QUEUE_DAILY_REPORT = "daily.report";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange auto710Exchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    private Queue durableQueue(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", name + ".dlq");
        return new Queue(name, true, false, false, args);
    }

    @Bean
    public Queue orderTimeoutQueue() {
        return durableQueue(QUEUE_ORDER_TIMEOUT);
    }

    @Bean
    public Queue msgTemplateQueue() {
        return durableQueue(QUEUE_MSG_TEMPLATE);
    }

    @Bean
    public Queue smsSendQueue() {
        return durableQueue(QUEUE_SMS_SEND);
    }

    @Bean
    public Queue tireRemindQueue() {
        return durableQueue(QUEUE_TIRE_REMIND);
    }

    @Bean
    public Queue dailyReportQueue() {
        return durableQueue(QUEUE_DAILY_REPORT);
    }

    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue()).to(auto710Exchange()).with(QUEUE_ORDER_TIMEOUT);
    }

    @Bean
    public Binding msgTemplateBinding() {
        return BindingBuilder.bind(msgTemplateQueue()).to(auto710Exchange()).with(QUEUE_MSG_TEMPLATE);
    }

    @Bean
    public Binding smsSendBinding() {
        return BindingBuilder.bind(smsSendQueue()).to(auto710Exchange()).with(QUEUE_SMS_SEND);
    }

    @Bean
    public Binding tireRemindBinding() {
        return BindingBuilder.bind(tireRemindQueue()).to(auto710Exchange()).with(QUEUE_TIRE_REMIND);
    }

    @Bean
    public Binding dailyReportBinding() {
        return BindingBuilder.bind(dailyReportQueue()).to(auto710Exchange()).with(QUEUE_DAILY_REPORT);
    }
}
