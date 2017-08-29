package com.hpe.ha.ipe.service;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.onem2m.mca.mqtt.RequestPrimitive;
import org.onem2m.mca.mqtt.TopicReference;
import org.onem2m.mca.mqtt.client.MqttConnection;
import org.onem2m.resource.ContentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpe.ha.ipe.util.ApplicationProperties;


@Service
public class IRemoconController {
	
	private static final Logger logger = LoggerFactory.getLogger(IRemoconController.class);
	
	@Autowired
	MqttConnectionManager mqttConnectionManager;
	
	@Autowired
	private ApplicationProperties prop;
	
	public void sendInfrared(int infraredId) {
		String command = String.format("*is;%d", infraredId);
		this.sendRequest(this.createRequest(command));
	}
	
	public void learnInfrared(int infraredId) {
		String command = String.format("*ic;%d", infraredId);
		this.sendRequest(this.createRequest(command));
	}
	
	private String createRequest(String command) {
		ContentInstance cin = new ContentInstance(command);
		RequestPrimitive request = RequestPrimitive.newCreateRequest("HPE_IoT/ADN-AE", "HPE_IoT", cin);
		return request.toJson();
	}
	
	private void sendRequest(String request) {
		String topic = TopicReference.getTopicForRequestFromCseTo(prop.getAeId());
		byte[] payload = request.getBytes();
		MqttConnection mqttConnection = mqttConnectionManager.getConnection();
		try {
			mqttConnection.publish(topic, payload);
			logger.info("MQTT Message sended. topic: [{}]  payload: [{}]", topic, request);
		} catch (MqttException e) {
			logger.error("Failed to send request. There is some problem in communication with CSE.");
		}
	}
	
}
