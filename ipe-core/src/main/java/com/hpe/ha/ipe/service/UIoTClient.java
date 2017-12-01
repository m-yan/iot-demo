package com.hpe.ha.ipe.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.onem2m.mca.primitive.RequestPrimitive;
import org.onem2m.mca.primitive.ResponsePrimitive;
import org.onem2m.mca.primitive.Primitive.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.ha.ipe.util.ApplicationProperties;

@Service
public class UIoTClient {

	private static final Logger logger = LoggerFactory.getLogger(UIoTClient.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ApplicationProperties prop;

	public ResponsePrimitive sendRequest(RequestPrimitive request) {

		HttpUriRequest httpRequest;
		try {
			httpRequest = this.createHttpRequest(request);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return new ResponsePrimitive(ResponseStatus.INTERNAL_SERVER_ERROR, request);
		}

		if (httpRequest == null) {
			return new ResponsePrimitive(ResponseStatus.BAD_REQUEST, request);
		}

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			try (CloseableHttpResponse response = httpclient.execute(httpRequest)) {
				logger.info("Request is sended to UIoT. Result [{}] ", response.getStatusLine().toString());
				
				Integer responseStatusCode = Integer.valueOf(response.getLastHeader("x-m2m-rsc").getValue());
				String requestId = response.getLastHeader("x-m2m-ri").getValue();
				Object content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				
				return new ResponsePrimitive(responseStatusCode, requestId, content);
			} catch (ClientProtocolException e) {
				logger.error(e.getMessage(), e);
				return new ResponsePrimitive(ResponseStatus.INTERNAL_SERVER_ERROR, request);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return new ResponsePrimitive(ResponseStatus.INTERNAL_SERVER_ERROR, request);
			}
		} catch (IOException e2) {
			e2.printStackTrace();
			return new ResponsePrimitive(ResponseStatus.INTERNAL_SERVER_ERROR, request);
		}

	}

	private HttpUriRequest createHttpRequest(RequestPrimitive request) throws URISyntaxException {
		Integer op = request.getOperation();

		String httpBody = null;
		if (op == 1 || op == 3 || op == 5) {
			Object content = request.getContent();
			if (content == null) {
				logger.warn("There was no content with the received request.");
				return null;
			}

			try {
				httpBody = mapper.writeValueAsString(content);
			} catch (JsonProcessingException e) {
				logger.warn("There was something wrong with the received request content.");
				return null;
			}
		}

		URI resourceUri = new URIBuilder().setScheme("http").setHost(prop.getDavHostname()).setPort(prop.getDavPort())
				.setPath(request.getTo()).build();

		HttpUriRequest httpRequest = null;

		switch (op) {
		case 1:
		case 5:
			HttpPost httpPost = new HttpPost(resourceUri);
			httpPost.setEntity(new StringEntity(httpBody, StandardCharsets.UTF_8));
			httpRequest = httpPost;
			break;
		case 2:
			HttpGet httpGet = new HttpGet(resourceUri);
			httpRequest = httpGet;
			break;
		case 3:
			HttpPut httpPut = new HttpPut(resourceUri);
			httpPut.setEntity(new StringEntity(httpBody, StandardCharsets.UTF_8));
			httpRequest = httpPut;
			break;
		case 4:
			HttpDelete httpDelete = new HttpDelete(resourceUri);
			httpRequest = httpDelete;
			break;
		}

		if (op == 1) {
			httpRequest.addHeader("Content-Type", "application/vnd.onem2m-res+json; ty=4");
		} else {
			httpRequest.addHeader("Content-Type", "application/vnd.onem2m-res+json");
		}

		httpRequest.addHeader("Accept", "application/vnd.onem2m-res+json");
		httpRequest.addHeader("X-M2M-RI", request.getRequestId());
		httpRequest.addHeader("X-M2M-Origin", prop.getAeId());
		httpRequest.addHeader("Authorization", prop.getAeAuthToken());

		return httpRequest;
	}

}
