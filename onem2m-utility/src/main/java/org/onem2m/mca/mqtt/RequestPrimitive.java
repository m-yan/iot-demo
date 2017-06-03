package org.onem2m.mca.mqtt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.onem2m.mca.datatype.FilterCriteria;
import org.onem2m.resource.Resource;
import org.onem2m.resource.Resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * oneM2M RequestPrimitiveを扱うオブジェクト。<br>
 * 
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RequestPrimitive extends Primitive {

	private static final Logger logger = LoggerFactory.getLogger(RequestPrimitive.class);

	// リソースに対する操作の種類（1:Create, 2:Retrieve, 3:Update, 4:Delete, 5:Notify）
	@JsonProperty("op")
	@NonNull
	private Integer operation;

	// メッセージの送信対象のリソースのID
	@JsonProperty("to")
	@NonNull
	private String to;

	// リクエストの送信元のID
	@JsonProperty("fr")
	@NonNull
	private String from;

	// CREATEするリソースの種別 CREATE時のみ指定する
	@JsonProperty("ty")
	private Integer resourceType;
	
	// Responseの形式
	@JsonProperty("rcn")
	private Integer resultContent;
	
	// Requestの優先転送の制御
	@JsonProperty("ec")
	private Integer eventCategory;
	
	// RETRIEVEの検索条件
	@JsonProperty("fc")
	private FilterCriteria filterCriteria;	
	

	@JsonProperty("ty")
	public void setResourceType(Integer value) {
		this.resourceType = value;
	}
	

	public void setResourceType(ResourceType type) {
		this.resourceType = type.getValue();
	}
	
	@JsonProperty("rcn")
	public void setResultContent(Integer value) {
		this.resultContent = value;
	}
	
	public void setResultContent(ResultContent resultContent) {
		this.resultContent = resultContent.getValue();
	}
	
	private <T extends Resource> RequestPrimitive(@NonNull Operation op, @NonNull String to, @NonNull String from, T content) {
		this.operation = op.getValue();
		this.to = to;
		this.from = from;
		
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String randomString = RandomStringUtils.randomAlphabetic(5);
		this.requestId = now + randomString;
		if (content != null) {
			try {
				this.content = mapper.readValue(content.toJson(), new TypeReference<Map<String, Map<String, String>>>() {});
			} catch (IOException e) {
				logger.warn("Received Resource does not conform to the terms of oneM2M.");
				this.content = null;
			}
		}
	}

	/**
	 * CREATE Requestを生成するstaticファクトリメソッド
	 * @param to		操作対象のリソースのURI
	 * @param from		送信元のIN-AE-ID
	 * @param content	操作対象のリソースの下に作成する子リソース
	 * @return			CREATE request
	 */
	public static <T extends Resource> RequestPrimitive newCreateRequest(String to, String from, T content) {
		RequestPrimitive request = new RequestPrimitive(Operation.CREATE, to, from, content);
		request.setResourceType(content.getResourceType());
		return request;
	}
	
	/**
	 * RETRIEVE Requestを生成するstaticファクトリメソッド
	 * @param to		取得対象のリソースのURI	 
	 * @param from		送信元のIN-AE-ID
	 * @return			RETRIEVE Request
	 */
	public static <T extends Resource> RequestPrimitive newRetrieveRequest(String to, String from) {
		return new RequestPrimitive(Operation.RETRIEVE, to, from, null);
	}
	
	/**
	 * UPDATE Requestを生成するstaticファクトリメソッド
	 * @param to		操作対象のリソースのURI
	 * @param from		送信元のIN-AE-ID
	 * @param content	更新後の操作対象のリソース
	 * @return			UPDATE request
	 */
	public static <T extends Resource> RequestPrimitive newUpdateRequest(String to, String from, T content) {
		return new RequestPrimitive(Operation.UPDATE, to, from, content);
	}
	
	/**
	 * DELETE Requestを生成するstaticファクトリメソッド
	 * @param to		削除対象のリソースのURI
	 * @param from		送信元のIN-AE-ID
	 * @return			DELETE Request
	 */
	public static <T extends Resource> RequestPrimitive newDeleteRequest(String to, String from) {
		return new RequestPrimitive(Operation.DELETE, to, from, null);
	}
	
	/**
	 * JSONシリアライズされたRequestPrimitiveをparseしてインスタンスを生成するスタティックファクトリメソッド
	 * 
	 * @param jsonRequest
	 *            JSONシリアライズされたResponse
	 * @return jsonRequestをparseして得たインスタンスを返す。
	 *         jsonRequestが不正などでparseに失敗した場合はnullを返す。
	 */
	public static RequestPrimitive valueOf(String jsonRequest) {
		try {
			return mapper.readValue(jsonRequest, RequestPrimitive.class);
		} catch (JsonParseException e) {
			logger.warn("Received JSON format is invalid.");
			return null;
		} catch (JsonMappingException e) {
			logger.warn("Received Resource does not conform to the terms of oneM2M.");
			logger.warn(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}


}
