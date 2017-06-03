package org.onem2m.cse.web;

import org.onem2m.cse.domain.HomeStatus;
import org.onem2m.cse.service.HomeStatusService;
import org.onem2m.cse.service.IRemoconController;
import org.onem2m.cse.valueobject.HgwApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
public class DemoHgwApiController {

	@Autowired
	HomeStatusService homeStatusService;
	
	@Autowired
	IRemoconController iRecomonCtl;
		
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
	
	@PostMapping("/ems/devices/12345678/1201")
	@ResponseStatus(HttpStatus.OK)
	public void operateLight(@RequestBody HgwApi devices) {
		if (devices.getModules().isOperationStatus()) {
			iRecomonCtl.sendInfrared(5);
		} else {
			iRecomonCtl.sendInfrared(6);
		}		
	}
	
	
	@GetMapping(value= "/ems/devices/12345678/2301", produces = MediaType.APPLICATION_XML_VALUE)
	public String getIlluminanceSensorStatus() {
		return "<Device><Modules><illuminanceSensorDataPoints><Data><measuredIlluminanceValue1>1000</measuredIlluminanceValue1></Data></illuminanceSensorDataPoints></Modules></Device>";
	}
	
	@GetMapping(value= "/ems/devices/12345678/2401", produces = MediaType.APPLICATION_XML_VALUE)
	public String getDoorSensorStatus() {
		return "<Device><Modules><operationStatus>true</operationStatus><openCloseSensorDataPoints><Data><degreeOfOpeningDetectionStatus2>2</degreeOfOpeningDetectionStatus2></Data></openCloseSensorDataPoints></Modules></Device>";
	}
		
	@GetMapping(value= "/ems/devices/12345678/2501", produces = MediaType.APPLICATION_XML_VALUE)
	public String getMotionDetectionStatus() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return String.format("<Device><Modules><operationStatus>true</operationStatus><humanDetectionSensorDataPoints><Data><humanDetectionStatus>%d</humanDetectionStatus></Data></humanDetectionSensorDataPoints></Modules></Device>", homeStatus.getMotionDetectionStatus());
	}
}
