package com.landingis.api.cfg.tenants.tenant;

import com.landingis.api.cfg.tenants.TenantConst;
import com.landingis.api.cfg.tenants.TenantDbContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver{

    private String defaultTenant = TenantConst.DEFAULT_TENANT_ID;

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t =  TenantDbContext.getCurrentTenant();
        if(t!=null){
            return t;
        } else {
            return defaultTenant;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}