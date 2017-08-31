package com.hpe.ha.ipe.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HomeSecurityService {
	private static final Logger logger = LoggerFactory.getLogger(HomeSecurityService.class);

	public void pushAlert() {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			
			String body = "{\"title\":\"HPE Home Automaiton Demo\",\"body\":\"自宅監視中に室内に誰かがいる事を検知しました。\",\"icon\":\"https://dashboard.push7.jp/assets/img/logo.svg\",\"url\":\"http://sp-uiot.japaneast.cloudapp.azure.com:10080/home-automation\",\"apikey\":\"2043540fd92d4f98ade134a76b38c308\"}";
			
			new HttpPost();
			HttpPost httpPost = new HttpPost("https://api.push7.jp/api/v1/c105e2930f6d4efaaadee8d7129d990d/send");
			httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
			httpPost.addHeader("Content-Type", "application/json");

			try {
				CloseableHttpResponse response = httpclient.execute(httpPost);
				logger.info("Request is sended to Push7. Result [{}] ", response.getStatusLine().toString());
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
