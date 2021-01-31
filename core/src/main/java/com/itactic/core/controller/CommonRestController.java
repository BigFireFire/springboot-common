package com.itactic.core.controller;

import com.itactic.core.constants.HttpMethod;
import com.itactic.core.model.AjaxResult;
import com.itactic.core.service.ICommonService;
import com.itactic.jdbc.utils.ScanSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 1Zx.
 * @date 2021/1/21 15:17
 */
@RestController
@RequestMapping("common")
@ConditionalOnProperty(prefix = "common.controller", name = "enable", havingValue = "true")
public class CommonRestController {

    private static String entityPath;

    private final Map<String, Class<?>> entityMap = new ConcurrentHashMap<>();

    @Resource
    private ICommonService commonService;

    @RequestMapping("/crud/{entityName}")
    public <T> AjaxResult<T> crud(HttpServletRequest request,
                                  @PathVariable("entityName") String entityName,
                                  @RequestBody(required = false) LinkedHashMap entity,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                  @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        if (StringUtils.isBlank(entityPath)) {
            return AjaxResult.error(null, "请先配置实体类目录");
        }

        if (entityMap.size() == 0) {
            return AjaxResult.error(null, "目录：【%s】扫描不到实体类");
        }

        entityName = entityName.replace("_", "").toLowerCase();
        String method = request.getMethod();
        try {
            if (HttpMethod.GET.name().equals(method)) {
                return commonService.get(entityMap.get(entityName), entity, page, limit);
            } else if (HttpMethod.POST.name().equals(method)) {
                return commonService.save(entity, entityMap.get(entityName));
            } else if (HttpMethod.PUT.name().equals(method)) {
                return commonService.update(entity, entityMap.get(entityName));
            } else if (HttpMethod.DELETE.name().equals(method)) {
                return commonService.delete(entity, entityMap.get(entityName));
            }
        } catch (Exception e) {
            return AjaxResult.error(null, e.getMessage());
        }
        return AjaxResult.error(null, "不支持的请求方法");
    }

    @Value("${common.entity.path}")
    public void setEntityPath(String entityPath) {
        CommonRestController.entityPath = entityPath;
        Set<Class<?>> entityList = ScanSupport.getClass(CommonRestController.entityPath);
        if (null != entityList) {
            entityList.forEach(t-> entityMap.put(t.getSimpleName().toLowerCase(), t));
        }
    }
}

