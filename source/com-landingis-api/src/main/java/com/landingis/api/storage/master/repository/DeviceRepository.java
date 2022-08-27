package com.landingis.api.storage.master.repository;


import com.landingis.api.storage.master.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {
    Device findByPosId(String posId);
    Device findByPosIdAndParentId(String posId, Long parentId);
}
