package com.hpe.ha.ipe.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.onem2m.mca.mqtt.RequestPrimitive;
import org.onem2m.mca.mqtt.client.MqttMessageProcessable;
import org.onem2m.resource.ContentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.hpe.ha.ipe.util.ApplicationProperties;

@Service
public class MessageProcessor implements MqttMessageProcessable {

	private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
	private ExecutorService threadPool = Executors.newCachedThreadPool();

	@Autowired
	private ApplicationContext context;

	@Autowired
	private ApplicationProperties prop;
	
//	private CloseableHttpClient httpclient = null;
//	
//	@PostConstruct
//	public void init() {
//		CredentialsProvider credsProvider = new BasicCredentialsProvider();
//		credsProvider.setCredentials(new AuthScope(prop.getDavHostname(), prop.getDavPort()),
//				new UsernamePasswordCredentials(prop.getAeId(), prop.getAePassword()));
//		httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//	}

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

			
			URI fowardingURL = null;
			try {
				fowardingURL = new URIBuilder().setScheme("http").setHost(prop.getDavHostname()).setPort(prop.getDavPort()).setPath(request.getTo()).build();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(prop.getDavHostname(), prop.getDavPort()),
					new UsernamePasswordCredentials(prop.getAeId(), prop.getAePassword()));
			CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			
			try {
				new HttpPost();
				HttpPost httpPost = new HttpPost(fowardingURL);
				httpPost.setHeader("Content-Type", "application/vnd.onem2m-res+json; ty=4");
				httpPost.setHeader("Accept", "application/vnd.onem2m-res+json");
				httpPost.setHeader("X-M2M-RI", request.getRequestId());
				httpPost.setHeader("X-M2M-Origin", prop.getAeId());
				httpPost.setEntity(new StringEntity(request.getContent().toString(), StandardCharsets.UTF_8));
				
				try {
					httpclient.execute(httpPost);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
