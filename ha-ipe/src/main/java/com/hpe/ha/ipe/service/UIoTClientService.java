package com.hpe.ha.ipe.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hpe.ha.ipe.util.ApplicationProperties;

@Service
public class UIoTClientService {

	private static final Logger logger = LoggerFactory.getLogger(UIoTClientService.class);

	@Autowired
	private ApplicationProperties prop;

	public void sendRequest(String to, String body) {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			URI fowardingURL = null;
			try {
				fowardingURL = new URIBuilder().setScheme("http").setHost(prop.getDavHostname())
						.setPort(prop.getDavPort()).setPath(to).build();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}

			// 一意なrequestIdを生成
			String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			String randomString = RandomStringUtils.randomAlphabetic(5);
			String requestId = now + randomString;

			new HttpPost();
			HttpPost httpPost = new HttpPost(fowardingURL);
			httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
			httpPost.addHeader("Content-Type", "application/vnd.onem2m-res+json; ty=4");
			httpPost.addHeader("Accept", "application/vnd.onem2m-res+json");
			httpPost.addHeader("X-M2M-RI", requestId);
			httpPost.addHeader("X-M2M-Origin", prop.getAeId());
			httpPost.addHeader("Authorization", prop.getAeAuthToken());

			try {
				CloseableHttpResponse response = httpclient.execute(httpPost);
				logger.info("Request is sended to UIoT. Result [{}] ", response.getStatusLine().toString());
			} catch (ClientProtocolException e) {
				logger.error(e.getMessage(),e);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}

		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

}
