package com.landingis.api.cfg.tenants.tenant;

import com.landingis.api.cfg.tenants.TenantDbContext;
import com.landingis.api.storage.master.model.DbConfig;
import com.landingis.api.storage.master.repository.DbConfigRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
@Slf4j
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 1L;

    private Map<String, DataSource> dataSourcesMtApp = new TreeMap<>();

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    com.landingis.api.cfg.tenants.tenant.TenantDatabaseConfigProperties configProperties;

    @Override
    protected DataSource selectAnyDataSource() {
        // This method is called more than once. So check if the data source map
        // is empty. If it is then rescan master_tenant table for all tenant
        if (dataSourcesMtApp.isEmpty()) {
            List<DbConfig> dbConfigs = dbConfigRepository.findAllByInitialize(true);
            log.info("selectAnyDataSource() method call...Total tenants:" + dbConfigs.size());
            for (DbConfig dbConfig : dbConfigs) {
                dataSourcesMtApp.put(dbConfig.getName(), createAndConfigureDataSource(dbConfig));
            }
        }
        return this.dataSourcesMtApp.values().iterator().next();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        System.out.println("get datasource by tenant: "+tenantIdentifier);
        // If the requested tenant id is not present check for it in the master
        // database 'master_tenant' table
        tenantIdentifier = initializeTenantIfLost(tenantIdentifier);
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            List<DbConfig> dbConfigs = dbConfigRepository.findAllByInitialize(true);
            log.info("selectDataSource() method call...Tenant:" + tenantIdentifier + " Total tenants:" + dbConfigs.size());
            for (DbConfig dbConfig : dbConfigs) {
                dataSourcesMtApp.put(dbConfig.getName(), createAndConfigureDataSource(dbConfig));
            }
        }
        //check again if tenant exist in map after rescan master_db, if not, throw UsernameNotFoundException
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            log.warn("Trying to get tenant:" + tenantIdentifier + " which was not found in master db after rescan");
            //override lai cho nay de throw exception when not found tenants
        }
        return this.dataSourcesMtApp.get(tenantIdentifier);
    }

    private String initializeTenantIfLost(String tenantIdentifier) {
        if (tenantIdentifier != TenantDbContext.getCurrentTenant()) {
            tenantIdentifier = TenantDbContext.getCurrentTenant();
        }
        return tenantIdentifier;
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    private DataSource createAndConfigureDataSource(DbConfig dbConfig) {
        HikariDataSource ds = new HikariDataSource();
        ds.setUsername(dbConfig.getUsername());
        ds.setPassword(dbConfig.getPassword());
        ds.setJdbcUrl(dbConfig.getUrl());
        ds.setDriverClassName(dbConfig.getDriverClassName());
        // HikariCP settings - could come from the master_tenant table but
        // hardcoded here for brevity
        // Maximum waiting time for a connection from the pool
        ds.setConnectionTimeout(configProperties.getConnectionTimeout());
        // Minimum number of idle connections in the pool
        ds.setMinimumIdle(configProperties.getMinIdle());
        // Maximum number of actual connection in the pool
        ds.setMaximumPoolSize(configProperties.getMaxPoolSize());
        // Maximum time that a connection is allowed to sit idle in the pool
        ds.setIdleTimeout(configProperties.getIdleTimeout());
        ds.setConnectionTimeout(configProperties.getConnectionTimeout());
        // Setting up a pool name for each tenant datasource
        String tenantConnectionPoolName = dbConfig.getName() + "-connection-pool";
        ds.setPoolName(tenantConnectionPoolName);
        log.info("Configured datasource:" + dbConfig.getName() + ". Connection pool name:" + tenantConnectionPoolName);
        return ds;
    }
}
