package com.itactic.core.controller;

import com.itactic.core.model.AjaxResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 1Zx.
 * @date 2021/1/21 15:17
 */
@RestController
@ConditionalOnProperty(prefix = "common.health.controller", name = "enable", havingValue = "true")
public class HealthController {

    @GetMapping("health")
    public AjaxResult<String> health() {
        return AjaxResult.ok("操作成功");
    }
}
