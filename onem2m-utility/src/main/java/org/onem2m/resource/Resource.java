package org.onem2m.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Resource {
	
	protected static final Logger logger = LoggerFactory.getLogger(Resource.class);

	protected static final ObjectMapper mapper = new ObjectMapper();	
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
	}
	
	@JsonIgnore
	public abstract ResourceType getResourceType();

	@JsonProperty("ty")
	public Integer getResourceTypeValue() {
		return this.getResourceType().getValue();
	}
	
	
	@JsonProperty("ri")
	private String resourceID;

	@JsonProperty("pi")
	private String parentID;

	@JsonProperty("ct")
	@JsonFormat(pattern = "yyyyMMdd'T'HHmmss,SSSSSS")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime creationTime;

	@JsonProperty("lt")
	@JsonFormat(pattern = "yyyyMMdd'T'HHmmss,SSSSSS")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime lastModifiedTime;

	@JsonProperty("lbl")
	private String labels;

	@JsonProperty("et")
	@JsonFormat(pattern = "yyyyMMdd'T'HHmmss,SSSSSS")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime expirationTime;

	@JsonProperty("st")
	private Integer stateTag;

	@JsonProperty("rn")
	private String resourceName;

	@JsonProperty("lnk")
	private String link;	
	
	@JsonProperty("ch")
	private List<Object> childResource;	


	/**
	 * @return インスタンスをJSONシリアライズした文字列を返す。何らかの理由でにJSONシリアライズに失敗した場合はnullを返す。
	 */
	public String toJson() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * JSONシリアライズされたResourceをparseしてインスタンスを生成するスタティックファクトリメソッド
	 * 
	 * @param jsonResource
	 *            JSONシリアライズされたResource
	 * @return jsonResourceをparseして得たインスタンスを返す。
	 *         jsonResourceが不正などでparseに失敗した場合はnullを返す。
	 */
	public static <T extends Resource> T parse(String jsonResource, Class<T> type) {
		try {
			return mapper.readValue(jsonResource, type);
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
	
	public static enum ResourceType {
		AE(2, "ae"), 
		container(3, "cnt"), 
		contentInstance(4, "cin"),
		remoteCSE(16, "csr"),
		AEAnnc(10002, "aeA"),
		nodeAnnc(10014, "nodA");

		@Getter
		private final Integer value;
		
		@Getter
		private final String shortname;

		ResourceType(Integer value, String shortname) {
			this.value = value;
			this.shortname = shortname;
		}

	}

}
