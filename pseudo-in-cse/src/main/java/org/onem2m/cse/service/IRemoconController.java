package org.onem2m.cse.service;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.onem2m.cse.util.ApplicationProperties;
import org.onem2m.mca.mqtt.RequestPrimitive;
import org.onem2m.mca.mqtt.client.MqttConnection;
import org.onem2m.resource.ContentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
		RequestPrimitive request = RequestPrimitive.newCreateRequest(prop.getIrCommandsContainerUri(), prop.getInCseId(), cin);
		return request.toJson();
	}
	
	private void sendRequest(String request) {
		String topic = prop.getTopicForPublish();
		byte[] payload = request.getBytes();
		MqttConnection mqttConnection = mqttConnectionManager.getConnection();
		try {
			mqttConnection.publish(topic, payload);
		} catch (MqttException e) {
			logger.error("Failed to send request. There is some problem in communication with CSE.");
		}
	}
	
}
