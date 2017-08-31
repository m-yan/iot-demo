package com.hpe.ha.ipe.service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.onem2m.mca.mqtt.RequestPrimitive;
import org.onem2m.mca.mqtt.client.MqttMessageProcessable;
import org.onem2m.resource.ContentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class MessageProcessor implements MqttMessageProcessable {

	private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
	private ExecutorService threadPool = Executors.newCachedThreadPool();

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UIoTClientService uiotClient;

	@Autowired
	private EventLogService eventLogger;
	
	@Override
	public boolean process(String topic, int id, int qos, byte[] payload) {
		Request request = (Request) context.getBean(Request.class);
		request.setParams(topic, payload);
		threadPool.execute(request);
		return true;
	}

	public void shutdown() {
		threadPool.shutdown();
		logger.info("Message Processor is shutdown.");
	}

	@Service
	@Scope("prototype")
	private class Request implements Runnable {

		private final Logger logger = LoggerFactory.getLogger(Request.class);
		private String topic;
		private byte[] bytePayload;

		public void setParams(String topic, byte[] payload) {
			this.topic = topic;
			this.bytePayload = payload;
		}

		@Override
		public void run() {
			String strPayload = this.encodeToString(this.bytePayload);
			logger.info("MQTT Message received. topic: [{}]  payload: [{}]", topic, strPayload);

			RequestPrimitive request = RequestPrimitive.valueOf(strPayload);
			if (request == null) {
				logger.warn("There was something wrong with the received request.");
				return;
			}
			String to = request.getTo();

			ContentInstance cin = request.getContentCastedBy(ContentInstance.class);
			String cinContent = null;
			if (cin != null) {
				cinContent = cin.getContent();
			}
			if (to == null || cin == null || cinContent == null) {
				logger.warn("There was no 'to' or 'content' in the received request.");
				return;
			}

			uiotClient.sendRequest(to, cin.toJson());
			
			if (to.contains("motionSensorData")) {
				switch(cin.getContent()) {
				case "0":
					eventLogger.writeLog("誰もいなくなりました。");
					break;
				case "1":
					eventLogger.writeLog("誰かが来ました。");
					break;
				}
			}
		}

		private String encodeToString(byte[] byte_array) {
			try {
				String rawString = new String(byte_array, "UTF-8");
				String compactString = rawString.replaceAll(" ", "").replaceAll("\n", "");
				return compactString;
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to encode byte array into character string.");
				logger.error(e.getMessage(), e);
				return null;
			}
		}		
	}
}
