package com.itactic.jdbc.jdbc.autocreate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 1Zx.
 * @date 2020/4/27 19:37
 */
@Component
public class AutoCreateTableConfig {

    /** 自动生成策越：DAC:删除并创建，ND：存在不删除 */
    private static String tableBuildStrategy;

    public static String getTableBuildStrategy() {
        return tableBuildStrategy;
    }

    @Value("${auto.strategy:ND}")
    public void setTableBuildStrategy(String tableBuildStrategy) {
        AutoCreateTableConfig.tableBuildStrategy = tableBuildStrategy;
    }
}