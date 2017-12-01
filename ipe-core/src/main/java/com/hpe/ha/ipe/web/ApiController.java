package com.hpe.ha.ipe.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.onem2m.mca.complexdatatype.Notification;
import org.onem2m.mca.mqtt.TopicReference;
import org.onem2m.mca.primitive.RequestPrimitive;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hpe.ha.ipe.service.MqttPublisher;
import com.hpe.ha.ipe.util.ApplicationProperties;
import org.springframework.http.HttpStatus;
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
	private MqttPublisher mqttPublisher;

	@PostMapping("notification/container-changes")
	ResponseEntity<String> fowardNotification(@RequestBody String body, @RequestHeader("X-M2M-RI") String requestId) {
		logger.info("HTTP Request received. body: [{}]", body);

		Notification notification = Notification.parse(body);
		String notifiedContent = notification.getNev().getRep();
		if (notification == null || notifiedContent == null) {
			logger.warn("Received request is invalid.");
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}

		ContentInstance notifiedCin = ContentInstance.parse(notifiedContent);
		if (notifiedCin == null || notifiedCin.getParentID() == null || notifiedCin.getContent() == null) {
			logger.warn("Received request is invalid.");
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}

		String receiver = this.extractReceiver(notifiedCin.getParentID());
		if (receiver == null) {
			logger.warn("Received request is invalid.");
			return ResponseEntity.badRequest().header("X-M2M-RI", requestId).header("X-M2M-RSC", "4000").body(null);
		}

		String topic = TopicReference.generateTopicForRequest(prop.getInCseId(), receiver);
		RequestPrimitive request = RequestPrimitive.newCreateRequest(prop.getInCseId() + "/" + receiver, prop.getInCseId(), notifiedCin);
		
		if (mqttPublisher.sendMessage(topic, request.toJson())) {
			return ResponseEntity.ok().header("X-M2M-RI", requestId).header("X-M2M-RSC", "2000").body(null);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("X-M2M-RI", requestId)
					.header("X-M2M-RSC", "5000").body(null);
		}
	}

	private String extractReceiver(String parentId) {
		String regex = new StringBuilder().append(prop.getInCseId()).append("/(.*)/.*").toString();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(parentId);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

}
