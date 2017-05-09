package org.onem2m.mca.mqtt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * oneM2M Mca Responseを扱うオブジェクト。<br>
 * com.kddi.telematics.onem2m.mca.McaRequestのインスタンスを元に成功（or失敗）
 * 応答を生成するスタティックファクトリメソッドや、<br>
 * インスタンスをJSONシリアライズするユーティリティメソッド等を提供する<br>
 * 
 */
@Getter
@ToString
public final class McaResponse {

	private static final Logger logger = LoggerFactory.getLogger(McaResponse.class);
	private static final OneM2MMcaProperties prop = OneM2MMcaProperties.getInstance();

	public static enum ResponseStatus {
		OK("2000"), CREATED("2001"), BAD_REQUEST("4000"), INTERNAL_SERVER_ERROR("5000");

		@Getter
		private final String statusCode;

		ResponseStatus(final String statusCode) {
			this.statusCode = statusCode;
		}
	}

	// リソース操作の結果を表すコード。（HTTP StatusCodeに類似） 必須
	@JsonProperty("rsc")
	@Getter
	private final String responseStatusCode;

	// レスポンスに対応するリクエストのID　必須
	@JsonProperty("rqi")
	@Getter
	private final String requestId;

	// レスポンスの送信先（＝対応するリクエストのfrom）
	@JsonIgnore
	@Getter
	private final String to;

	// レスポンスの送信元ID
	@JsonIgnore
	@Getter
	private final String from;

	@JsonCreator
	private McaResponse(
			@JsonProperty("rsc") @NonNull String responseStatusCode, 
			@JsonProperty("rqi") @NonNull String requestId,
			@JsonProperty("to") String to, 
			@JsonProperty("from") String from) {
		this.responseStatusCode = responseStatusCode;
		this.requestId = requestId;
		this.to = to;
		this.from = from;
	}

	public McaResponse(ResponseStatus responseStatus, McaRequest request) {
		this.responseStatusCode = responseStatus.getStatusCode();
		this.requestId = request.getRequestId();
		this.to = request.getFrom();
		this.from = prop.getInAeId();
	}

	/**
	 * JSONシリアライズされたMca Resposeをparseしてインスタンスを生成するスタティックファクトリメソッド
	 * 
	 * @param jsonResponsePayload
	 *            JSONシリアライズされたMca Response
	 * @return jsonResponsePayloadをパースして得たインスタンスを返す。
	 *         jsonResponsePayloadが不正などでパースに失敗した場合はnullを返す。
	 */
	public static McaResponse valueOf(String jsonResponsePayload) {
		try {
			McaResponse resp = new ObjectMapper().readValue(jsonResponsePayload, McaResponse.class);
			return resp;
		} catch (JsonParseException e) {
			logger.warn("Received JSON format is invalid.");
			return null;
		} catch (JsonMappingException e) {
			logger.warn("Received Response does not conform to the terms of oneM2M.");
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @return インスタンスをJSONシリアライズした文字列を返す。何らかの理由でにJSONシリアライズに失敗した場合はnullを返す。
	 */
	public String toJson() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}
