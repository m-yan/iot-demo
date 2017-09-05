package com.hpe.ha.ipe.web;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.hpe.ha.ipe.domain.HomeStatus;
import com.hpe.ha.ipe.service.EventLogService;
import com.hpe.ha.ipe.service.HomeSecurityService;
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
	private HomeStatusService homeStatusService;
	
	@Autowired
	private IRemoconController iRemoconCtl;
	
	@Autowired
	private EventLogService eventLogger;
	
	@Autowired
	private HomeSecurityService homeSecurityService;
	
	@Autowired
	private ServerPushBroker serverPushBroker;
	
	
	@GetMapping("/api/home_status")
	HomeStatus getMonitoringMode() {
		HomeStatus homeStatus = homeStatusService.findOne("12345678");
		return homeStatus;
	}
	
	@PutMapping("/api/home_status")
	@ResponseStatus(HttpStatus.OK)
	HomeStatus setMonitoringMode(@RequestBody HomeStatus status) {
		status.setId("12345678");
		if (status.isMonitoringMode()) {
			eventLogger.writeLog("自宅監視を開始しました。");
		} else {
			eventLogger.writeLog("自宅監視を終了しました。");
		}		
		return homeStatusService.update(status);
	}

	@PostMapping("/api/alert")
	@ResponseStatus(HttpStatus.OK)
	void alert() {
		eventLogger.writeLog("自宅監視中に室内に誰かがいる事を検知しました。");
		homeSecurityService.pushAlert();
	}
	
	@PostMapping("/ha/ipe/iremocon_commands")
	ResponseEntity<String> sendIRemoconCommand(@RequestBody String body, @RequestHeader("X-M2M-RI") String requestId) {
		ContentInstance notifiedCin = this.parseNotificaitonAndExtractContentInstance(body);
		
		if (notifiedCin == null) {
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}	

		Integer infraredId = Integer.valueOf(notifiedCin.getContent());
		switch(infraredId) {
		case 1:
			eventLogger.writeLog("エアコンを操作しました。（電源ON）");
			break;
		case 2:
			eventLogger.writeLog("エアコンを操作しました。（電源OFF）");
			break;
		case 3:
			eventLogger.writeLog("照明を操作しました。（電源ON）");
			break;
		case 4:
			eventLogger.writeLog("照明を操作しました。（電源OFF）");
			break;
		}
		iRemoconCtl.sendInfrared(infraredId);
		
		return ResponseEntity.ok().header("X-M2M-RI", requestId).header("X-M2M-RSC", "2000").body(null);
	}
	
	@PostMapping("/ha/ipe/forwardNotification")
	ResponseEntity<String> forwardNotification(@RequestBody String body, @RequestHeader("X-M2M-RI") String requestId) {
		ContentInstance notifiedCin = this.parseNotificaitonAndExtractContentInstance(body);
		
		if (notifiedCin == null) {
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}
		
		Map<String, WebSocketSession> sessions = null;
		String containerID = notifiedCin.getParentID();
		if (containerID == null) {
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);	
		} else if (containerID.contains("environmentalData")) {
			sessions = serverPushBroker.getSessionsSubscribeingEnvironmentalInfo();
		} else if (containerID.contains("motionSensorData")) {
			sessions = serverPushBroker.getSessionsSubscribingMonitoringMode();
		} else if (containerID.contains("default")) {
			sessions = serverPushBroker.getSessionsSubscribingEvents();
		}
		
		for (Entry<String, WebSocketSession> entry : sessions.entrySet()) {
			try {
				entry.getValue().sendMessage(new TextMessage(notifiedCin.toJson()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().header("X-M2M-RI", requestId).header("X-M2M-RSC", "2000").body(null);
	}
	
	private ContentInstance parseNotificaitonAndExtractContentInstance(String payload) {
		ContentInstance cin = null;
		  try {
				Notification notification = mapper.readValue(payload, Notification.class);
				cin = mapper.readValue(notification.getNev().getRep(), ContentInstance.class);
			} catch (JsonParseException e) {
				logger.warn("Received JSON format is invalid.");
			} catch (JsonMappingException e) {
				logger.warn("Received Resource does not conform to the terms of oneM2M.");
				logger.warn(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		 return cin;
	}
}
