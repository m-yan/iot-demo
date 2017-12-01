package com.hpe.ha.ipe.web;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onem2m.resource.ContentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.hpe.ha.ipe.service.IRemoconController;
import com.hpe.ha.ipe.util.ApplicationProperties;
import com.hpe.ha.ipe.valueobject.Notification;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/ipe/api/")
public class ApiController {

	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

	private static final ObjectMapper mapper = new ObjectMapper();	
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
	}

	@Autowired
	private ApplicationProperties prop;
	
	@Autowired
	private IRemoconController iRemoconCtl;
	
	@PostMapping("send_iremocon_command")
	ResponseEntity<String> sendIRemoconCommand(@RequestBody String body, @RequestHeader("X-M2M-RI") String requestId) {
		logger.info("HTTP Request received. body: [{}]", body);

		ContentInstance notifiedCin = this.parseNotificaitonAndExtractContentInstance(body);
		
		if (notifiedCin == null || notifiedCin.getParentID() == null || notifiedCin.getContent() == null) {
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}
		
		String target = this.extractTarget(notifiedCin.getParentID());
		if (target == null) {
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}
		
		iRemoconCtl.sendInfrared(target, notifiedCin.getContent());
		
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
	
	private String extractTarget(String parentId) {
		String regex = new StringBuilder().append(prop.getInCseId()).append("/()/iRemoconCommands").toString();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(parentId);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
}
