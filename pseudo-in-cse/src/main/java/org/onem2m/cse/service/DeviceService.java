package org.onem2m.cse.service;

import org.onem2m.cse.domain.Device;
import org.onem2m.cse.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

	@Autowired
	DeviceRepository deviceRepo;
	
	public Device findOne(String id) {
		return deviceRepo.findOne(id);
	}
	
	public Device update(Device device) {
		return deviceRepo.save(device);
	}
}
