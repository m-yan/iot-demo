package org.onem2m.cse.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.onem2m.cse.domain.HomeStatus;
import org.onem2m.cse.repository.HomeStatusRepository;
import org.onem2m.mca.mqtt.client.MqttMessageProcessable;
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
	private HomeStatusRepository homeStatusRepo;
	
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

			String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").format(new Date());
			String eventTypeId = null;
			// TODO: 複数世帯からメッセージを受ける時はトピック名で判定し、hgw_idも可変にする必要あり
			HomeStatus homeStatus = homeStatusRepo.findOne("12345678");
			if (strPayload.equals("1")) {
				eventTypeId = "1";
				homeStatus.setMotionDetectionStatus(1);
			} else if (strPayload.equals("0")) {
				eventTypeId = "2";
				homeStatus.setMotionDetectionStatus(2);
			} else {
				logger.error("message is invalid.");
				return;
			}
				
			homeStatusRepo.save(homeStatus);
			
			String postBody = String.format("<?xmlversion='1.0' encoding='UTF-8'?><cin><cnf>application/xml:0</cnf><con><hgw_id>12345678</hgw_id><device_id>2501</device_id><event_type_id>%s</event_type_id><occurred_at>%s</occurred_at></con></cin>", eventTypeId, now);	
			logger.info("HTTP POST body [{}]", postBody);
			this.notifyToServer(postBody);
			
			if (homeStatus.getMonitoringMode() == 3) {
				postBody = String.format("<?xmlversion='1.0' encoding='UTF-8'?><cin><cnf>application/xml:0</cnf><con><hgw_id>12345678</hgw_id><device_id>2501</device_id><event_type_id>%s</event_type_id><occurred_at>%s</occurred_at></con></cin>", "3", now);	
				logger.info("HTTP POST body [{}]", postBody);
				this.notifyToServer(postBody);
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

		private void notifyToServer(String postBody) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			// TODO: 設定ファイルから取得
			credsProvider.setCredentials(new AuthScope("localhost", 10080),
					new UsernamePasswordCredentials("hires", "gV5pNB*mUQ!x"));
			CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

			try {
				// TODO: 設定ファイルから取得
				HttpPost httpPost  = new HttpPost("http://localhost:10080/CSE0001/events/");
				httpPost.setHeader("Content-Type", "application/xml; charset=utf-8; ty=4");
				httpPost.setEntity(new StringEntity(postBody, StandardCharsets.UTF_8));

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

	}
}
