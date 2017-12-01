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
	
	public void sendInfrared(String target, String infraredId) {
		String command = new StringBuilder().append("*is;").append(infraredId).toString();
		this.sendIRemoconCommand(target, command);
	}
	
	public void learnInfrared(String target, String infraredId) {
		String command = new StringBuilder().append("*ic;").append(infraredId).toString();
		this.sendIRemoconCommand(target, command);
	}
		
	private void sendIRemoconCommand(String target, String command) {
		String to = new StringBuilder().append(prop.getInCseId()).append("/").append(target).append("/container").toString();
		String from = prop.getInCseId();
		ContentInstance primitiveContent = new ContentInstance(command);

		String request = RequestPrimitive.newCreateRequest(to, from, primitiveContent).toJson();
		
		String topic = TopicReference.getTopicForRequest(prop.getInCseId(), target);
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
