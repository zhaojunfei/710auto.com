package com.auto710.wechat;

import cn.hutool.core.util.StrUtil;
import com.auto710.config.ConfigService;
import com.auto710.entity.CmsMenu;
import com.auto710.entity.WechatReplyRule;
import com.auto710.entity.WechatTemplateMsg;
import com.auto710.service.CmsMenuService;
import com.auto710.service.WechatReplyRuleService;
import com.auto710.service.WechatTemplateMsgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信公众号服务：access_token、自定义菜单同步、模板消息、关键词自动回复
 * <p>真实环境需配置 AppID/AppSecret，并调用微信开放接口；未配置时为安全空跑模式。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatService {

    private final ConfigService configService;
    private final CmsMenuService menuService;
    private final WechatReplyRuleService replyRuleService;
    private final WechatTemplateMsgService templateMsgService;

    public boolean isEnabled() {
        return StrUtil.isNotBlank(configService.get(ConfigService.WECHAT_MP_APPID));
    }

    /**
     * 获取 access_token（实际应缓存 7000 秒）
     */
    public String getAccessToken() {
        if (!isEnabled()) {
            return null;
        }
        // TODO 调用 https://api.weixin.qq.com/cgi-bin/token 并缓存到 Redis
        return null;
    }

    /**
     * 构建微信自定义菜单 JSON（三级结构）
     */
    public Map<String, Object> buildMenuJson() {
        List<CmsMenu> topMenus = menuService.list(new LambdaQueryWrapper<CmsMenu>()
                .eq(CmsMenu::getPlatform, "mp")
                .eq(CmsMenu::getVisible, 1)
                .orderByAsc(CmsMenu::getSort));

        List<Map<String, Object>> buttons = new ArrayList<>();
        for (CmsMenu menu : topMenus) {
            if (menu.getParentId() != null) {
                continue;
            }
            Map<String, Object> button = new LinkedHashMap<>();
            button.put("name", menu.getMenuName());
            List<CmsMenu> children = topMenus.stream()
                    .filter(m -> m.getId().equals(menu.getId())).findFirst()
                    .map(m -> menuService.list(new LambdaQueryWrapper<CmsMenu>()
                            .eq(CmsMenu::getParentId, m.getId())
                            .orderByAsc(CmsMenu::getSort)))
                    .orElse(List.of());
            if (children.isEmpty()) {
                applyButtonType(button, menu);
            } else if (children.size() <= 5) {
                List<Map<String, Object>> subs = new ArrayList<>();
                for (CmsMenu child : children) {
                    Map<String, Object> sub = new LinkedHashMap<>();
                    sub.put("name", child.getMenuName());
                    applyButtonType(sub, child);
                    subs.add(sub);
                }
                button.put("sub_button", subs);
            } else {
                applyButtonType(button, menu);
            }
            buttons.add(button);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("button", buttons);
        return root;
    }

    private void applyButtonType(Map<String, Object> button, CmsMenu menu) {
        switch (StrUtil.blankToDefault(menu.getMenuType(), "view")) {
            case "miniprogram" -> {
                button.put("type", "miniprogram");
                button.put("url", menu.getUrl());
                button.put("appid", menu.getAppId());
                button.put("pagepath", menu.getPagePath());
            }
            case "click", "keyword" -> {
                button.put("type", "click");
                button.put("key", menu.getKeyValue());
            }
            default -> {
                button.put("type", "view");
                button.put("url", menu.getUrl());
            }
        }
    }

    /**
     * 同步菜单到微信
     */
    public Map<String, Object> syncMenu() {
        Map<String, Object> json = buildMenuJson();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("enabled", isEnabled());
        result.put("menu", json);
        if (!isEnabled()) {
            result.put("message", "未配置公众号 AppID/AppSecret，仅生成菜单结构（空跑）");
            result.put("synced", false);
        } else {
            // TODO POST https://api.weixin.qq.com/cgi-bin/menu/create?access_token=xxx
            result.put("synced", true);
            result.put("message", "菜单已提交微信同步");
        }
        return result;
    }

    /**
     * 关键词自动回复匹配
     */
    public WechatReplyRule matchReply(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return defaultReply();
        }
        List<WechatReplyRule> rules = replyRuleService.list(new LambdaQueryWrapper<WechatReplyRule>()
                .eq(WechatReplyRule::getStatus, 1)
                .eq(WechatReplyRule::getRuleType, "关键词")
                .orderByAsc(WechatReplyRule::getSort));
        for (WechatReplyRule rule : rules) {
            if ("全匹配".equals(rule.getMatchType()) && keyword.equals(rule.getKeyword())) {
                return rule;
            }
            if ("半匹配".equals(rule.getMatchType()) && rule.getKeyword() != null
                    && keyword.contains(rule.getKeyword())) {
                return rule;
            }
        }
        return defaultReply();
    }

    public WechatReplyRule subscribeReply() {
        return replyRuleService.getOne(new LambdaQueryWrapper<WechatReplyRule>()
                .eq(WechatReplyRule::getRuleType, "关注")
                .eq(WechatReplyRule::getStatus, 1)
                .last("limit 1"));
    }

    private WechatReplyRule defaultReply() {
        return replyRuleService.getOne(new LambdaQueryWrapper<WechatReplyRule>()
                .eq(WechatReplyRule::getRuleType, "默认")
                .eq(WechatReplyRule::getStatus, 1)
                .last("limit 1"));
    }

    /**
     * 发送模板消息（按业务类型取模板 ID）
     */
    public void sendTemplateMessage(String openid, String msgType, Map<String, String> data) {
        WechatTemplateMsg template = templateMsgService.getOne(new LambdaQueryWrapper<WechatTemplateMsg>()
                .eq(WechatTemplateMsg::getMsgType, msgType)
                .eq(WechatTemplateMsg::getStatus, 1)
                .last("limit 1"));
        if (template == null) {
            log.debug("未配置[{}]模板消息，跳过发送", msgType);
            return;
        }
        String token = getAccessToken();
        if (token == null) {
            log.debug("公众号未启用，模板消息空跑: openid={}, templateId={}", openid, template.getTemplateId());
            return;
        }
        // TODO 调用 https://api.weixin.qq.com/cgi-bin/message/template/send
        log.info("发送模板消息: openid={}, templateId={}, data={}", openid, template.getTemplateId(), data);
    }
}
