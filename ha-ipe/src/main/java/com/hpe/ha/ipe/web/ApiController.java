package com.hpe.ha.ipe.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hpe.ha.ipe.domain.HomeStatus;
import com.hpe.ha.ipe.service.HomeStatusService;
import com.hpe.ha.ipe.service.IRemoconController;
import com.hpe.ha.ipe.valueobject.HgwApi;
import com.hpe.ha.ipe.valueobject.Notification;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
public class ApiController {

	@Autowired
	HomeStatusService homeStatusService;
	
	@Autowired
	IRemoconController iRemoconCtl;
		
	@GetMapping(value= "/ems/devices/12345678/0101", produces = MediaType.APPLICATION_XML_VALUE)
	public String getMonitoringMode() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return String.format("<Device><Modules><hgwDataPoints><Data><monitoringMode>%d</monitoringMode></Data></hgwDataPoints></Modules></Device>", homeStatus.getMonitoringMode());
	}
	
	@PostMapping("/ems/devices/12345678/0101")
	@ResponseStatus(HttpStatus.OK)
	public void setMonitoringMode(@RequestBody HgwApi devices) {
		int monitoringMode = devices.getModules().getHgwDataPoints().getData().getMonitoringMode();
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		homeStatus.setMonitoringMode(monitoringMode);
		homeStatusService.update(homeStatus);
	}

	@PostMapping("/ha/ipe/forwardNotification")
	public ResponseEntity<String> forwardNotification(@RequestBody Notification body, @RequestHeader("X-M2M-RI") String requestId) {
		iRemoconCtl.sendInfrared(Integer.valueOf(body.getRep().getCon()));
		return ResponseEntity.ok().header("X-M2M-RI", requestId).header("X-M2M-RSC", "2000").body(null);
	}
	
}
