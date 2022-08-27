package com.landingis.api.storage.tenant.repository;

import com.landingis.api.storage.tenant.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SettingsRepository extends JpaRepository<Settings, Long>, JpaSpecificationExecutor<Settings> {
    public Settings findSettingsByKey(String key);
    public Settings findSettingsByKeyAndKind(String key, Integer kind);
}
