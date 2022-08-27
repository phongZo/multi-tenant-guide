package com.landingis.api.storage.master.repository;


import com.landingis.api.storage.master.model.DbConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DbConfigRepository extends JpaRepository<DbConfig, Long>, JpaSpecificationExecutor<DbConfig> {
    List<DbConfig> findAllByInitialize(boolean initialize);
    DbConfig findByDeviceId(Long deviceId);
    DbConfig findByName(String name);
}
