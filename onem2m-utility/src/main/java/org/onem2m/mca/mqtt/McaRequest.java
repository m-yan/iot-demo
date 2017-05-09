package org.onem2m.mca.mqtt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;


/**
 * oneM2M Mca Requestを扱うオブジェクト。<br>
 * JSONシリアライズされたリクエストをparseしてリクエストに指定されたパラメータを返すユーティリティメソッドなどを提供する。<br>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public final class McaRequest {

	private static final Logger logger = LoggerFactory.getLogger(McaRequest.class);
	private static final OneM2MMcaProperties prop = OneM2MMcaProperties.getInstance();
	
	// リソースに対する操作の種類（1:Create, 2:Retrieve, 3:Update, 4:Delete, 5:Notify）　必須
	@Getter 
	@JsonProperty("op")
	private final String operation;
	
	// 操作対象のリソースのID　必須
	@Getter 
	@JsonProperty("to")
	private final String to;
	
	// リクエストの送信元のID　必須
	@Getter 
	@JsonProperty("fr")
	private final String from;
	
	// リクエストのID　必須
	@Getter
	@JsonProperty("rqi")
	private final String requestId;

	// 対象対象のリソースの種別（op=1のみ指定。それ以外は指定NG）
	@Getter
	@JsonProperty("ty") 
	private final String resourceType;
	
	// リクエスト本文（op=1,3,5で必須、2はオプション、4は指定NG）
	@JsonProperty("pc")
	private final Map<String, Map<String, String>> primitiveContent;

	@JsonCreator
	private McaRequest(
			@JsonProperty("op") @NonNull String operation, 
			@JsonProperty("to") @NonNull String to,
			@JsonProperty("fr") @NonNull String from, 
			@JsonProperty("rqi") @NonNull String requestId,
			@JsonProperty("ty") String resourceType, 
			@JsonProperty("pc") Map<String, 
			Map<String, String>> primitiveContent) {
		this.operation = operation;
		this.to = to;
		this.from = from;
		this.requestId = requestId;
		this.resourceType = resourceType;
		this.primitiveContent = primitiveContent;
	};

	
	/**
	 * JSONシリアライズされたMca Requestをparseしてインスタンスを生成するスタティックファクトリメソッド
	 * 
	 * @param jsonRequestPayload	JSONシリアライズされたMca Request
	 * @return	jsonRequestPayloadをパースして得たインスタンスを返す。jsonRequestPayloadが不正などでパースに失敗した場合はnullを返す。
	 */
	public static McaRequest valueOf(String jsonRequestPayload) {
		try {
			McaRequest req = new ObjectMapper().readValue(jsonRequestPayload, McaRequest.class);
			return req;
		} catch (JsonParseException e) {
			logger.warn("Received JSON format is invalid.");
			return null;
		} catch (JsonMappingException e) {
			logger.warn("Received Request does not conform to the terms of oneM2M.");
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
	
	/**
	 * 指定されたcontainerの下にcontentInstanceを作成するMca Requestを生成するスタティックファクトリメソッド
	 * 
	 * @param to	containerのresouceId
	 * @param content	contentInstanceの内容
	 * @return	指定されたcontainerの下にcontentInstanceを作成するMca Request
	 */
	public static McaRequest ofCreateContentInstance(String to, String content) {
		String requestId = UUID.randomUUID().toString();
		@SuppressWarnings("serial")
		Map<String, String> contentInstance = new HashMap<String, String>() {
			{put("con", content);}
		};
		@SuppressWarnings("serial")
		Map<String, Map<String, String>> primitiveContent = new HashMap<String, Map<String, String>>() {
			{put("cin", contentInstance);}
		};
		return new McaRequest("1", to, prop.getInAeId(), requestId, "4", primitiveContent);
	}
	
	/**
	 * @return	pc（リクエスト本文）に指定されたrn（resourceName）を返す。該当するものがなければnullを返す。
	 */
	@JsonIgnore
	public String getResourceName(){
		return this.primitiveContent.values().stream().findFirst().get().get("rn");
	}

	/**
	 * @return	pc（リクエスト本文）に指定されたpi（parentId）を返す。該当するものがなければnullを返す。
	 */
	@JsonIgnore
	public String getParentId(){
		return this.primitiveContent.values().stream().findFirst().get().get("pi");
	}
	
	/**
	 * @return	pc（リクエスト本文）に指定されたリソースがcin(contentInstance)の場合、con（content）を返す。該当するものがなければnullを返す。
	 */
	@JsonIgnore
	public String getCotentInstanceContent(){
		String resourceTypeName = this.primitiveContent.keySet().stream().findFirst().get();
		if (resourceTypeName == "cin") {
			return this.primitiveContent.values().stream().findFirst().get().get("con");
		} else {
			return null;
		}
	}
	
	/**
	 * @return	インスタンスをJSONシリアライズした文字列を返す。何らかの理由でにJSONシリアライズに失敗した場合はnullを返す。
	 */
	public String toJson() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}

}
