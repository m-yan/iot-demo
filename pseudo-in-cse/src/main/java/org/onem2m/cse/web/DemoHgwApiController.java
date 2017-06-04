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

	@PostMapping("/ems/devices/12345678/1101")
	@ResponseStatus(HttpStatus.OK)
	public void operateAir(@RequestBody HgwApi devices) {
		if (devices.getModules().getOperationStatus() == null) {
		} else if (devices.getModules().getOperationStatus()) {
			iRecomonCtl.sendInfrared(1);
		} else {
			iRecomonCtl.sendInfrared(2);
		}
	}
	
	@PostMapping("/ems/devices/12345678/1201")
	@ResponseStatus(HttpStatus.OK)
	public void operateLight(@RequestBody HgwApi devices) {
		if (devices.getModules().getOperationStatus()) {
			iRecomonCtl.sendInfrared(3);
		} else {
			iRecomonCtl.sendInfrared(4);
		}
	}
	
	@PostMapping("/ems/devices/12345678/1301")
	@ResponseStatus(HttpStatus.OK)
	public void operateTV() {
	}
	

	@GetMapping(value= "/ems/devices/12345678/2101", produces = MediaType.APPLICATION_XML_VALUE)
	public String getTemperature() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return String.format("<Device><Modules><temperatureSensorDataPoints><Data><measuredTemperatureValue>%d</measuredTemperatureValue></Data></temperatureSensorDataPoints></Modules></Device>", homeStatus.getTemperature().intValue() * 10);
	}
	
	@GetMapping(value= "/ems/devices/12345678/2201", produces = MediaType.APPLICATION_XML_VALUE)
	public String getHumidity() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return String.format("<Device><Modules><humiditySensorDataPoints><Data><measuredValueOfRelativeHumidity>%d</measuredValueOfRelativeHumidity></Data></humiditySensorDataPoints></Modules></Device>", homeStatus.getHumidity());
	}
	
	@GetMapping(value= "/ems/devices/12345678/2301", produces = MediaType.APPLICATION_XML_VALUE)
	public String getIlluminance() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return String.format("<Device><Modules><illuminanceSensorDataPoints><Data><measuredIlluminanceValue1>%d</measuredIlluminanceValue1></Data></illuminanceSensorDataPoints></Modules></Device>", homeStatus.getIlluminance());
	}
	
	@GetMapping(value= "/ems/devices/12345678/2401", produces = MediaType.APPLICATION_XML_VALUE)
	public String getDoorSensorStatus() {
		return "<Device><Modules><operationStatus>true</operationStatus><openCloseSensorDataPoints><Data><degreeOfOpeningDetectionStatus2>2</degreeOfOpeningDetectionStatus2></Data></openCloseSensorDataPoints></Modules></Device>";
	}
	
	@PostMapping("/ems/devices/12345678/2401")
	@ResponseStatus(HttpStatus.OK)
	public void operateOcSensor(@RequestBody HgwApi devices) {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		if (devices.getModules().getOperationStatus() == null) {
		} else if (devices.getModules().getOperationStatus()) {
			homeStatus.setOcSensorPower(true);
		} else {
			homeStatus.setOcSensorPower(false);
		}
		homeStatusService.update(homeStatus);
	}
		
	@GetMapping(value= "/ems/devices/12345678/2501", produces = MediaType.APPLICATION_XML_VALUE)
	public String getMotionDetectionStatus() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return String.format("<Device><Modules><operationStatus>true</operationStatus><humanDetectionSensorDataPoints><Data><humanDetectionStatus>%d</humanDetectionStatus></Data></humanDetectionSensorDataPoints></Modules></Device>", homeStatus.getMotionDetectionStatus());
	}
	
	@PostMapping("/ems/devices/12345678/2501")
	@ResponseStatus(HttpStatus.OK)
	public void operateMSensor(@RequestBody HgwApi devices) {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		if (devices.getModules().getOperationStatus() == null) {
		} else if (devices.getModules().getOperationStatus()) {
			homeStatus.setMSensorPower(true);
		} else {
			homeStatus.setMSensorPower(false);
		}
		homeStatusService.update(homeStatus);
	}
}
