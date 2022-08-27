package com.landingis.api.cfg.tenants.tenant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("multitenancy.master.datasource")
@Data
public class TenantDatabaseConfigProperties {
    private long connectionTimeout;
    private int maxPoolSize;
    private long idleTimeout;
    private int minIdle;
    private String dialect;
    private boolean showSql;
    private String ddlAuto;
}
