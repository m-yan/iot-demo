package com.hpe.ha.ipe.web;

import java.io.IOException;

import org.onem2m.resource.ContentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.hpe.ha.ipe.domain.HomeStatus;
import com.hpe.ha.ipe.service.HomeStatusService;
import com.hpe.ha.ipe.service.IRemoconController;
import com.hpe.ha.ipe.valueobject.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class ApiController {

	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

	private static final ObjectMapper mapper = new ObjectMapper();	
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
	}
	
	@Autowired
	HomeStatusService homeStatusService;
	
	@Autowired
	IRemoconController iRemoconCtl;
		
	@GetMapping("/api/home_status")
	HomeStatus getMonitoringMode() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return homeStatus;
	}
	
	@PutMapping("/api/home_status")
	@ResponseStatus(HttpStatus.OK)
	HomeStatus setMonitoringMode(@RequestBody HomeStatus status) {
		status.setId("12345678");
		return homeStatusService.update(status);
	}

	@PostMapping("/ha/ipe/forwardNotification")
	ResponseEntity<String> forwardNotification(@RequestBody String body, @RequestHeader("X-M2M-RI") String requestId) {
		ContentInstance notifiedCin = null;
		  try {
				Notification notification = mapper.readValue(body, Notification.class);
				notifiedCin = mapper.readValue(notification.getNev().getRep(), ContentInstance.class);
			} catch (JsonParseException e) {
				logger.warn("Received JSON format is invalid.");
			} catch (JsonMappingException e) {
				logger.warn("Received Resource does not conform to the terms of oneM2M.");
				logger.warn(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		if (notifiedCin != null) {
			iRemoconCtl.sendInfrared(Integer.valueOf(notifiedCin.getContent()));
		}
		return ResponseEntity.ok().header("X-M2M-RI", requestId).header("X-M2M-RSC", "2000").body(null);
	}
	
}
