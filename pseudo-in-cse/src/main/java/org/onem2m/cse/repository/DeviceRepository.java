package org.onem2m.cse.repository;

import org.onem2m.cse.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String>{

}