package com.hpe.ha.ipe.web;

import org.springframework.web.socket.handler.TextWebSocketHandler;
import lombok.Getter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ServerPushBroker extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(ServerPushBroker.class);
	
	@Getter
	private Map<String, WebSocketSession> sessionsSubscribeingEnvironmentalInfo = new ConcurrentHashMap<>();
	
	@Getter
	private Map<String, WebSocketSession> sessionsSubscribingMonitoringMode = new ConcurrentHashMap<>();
	
	@Getter
	private Map<String, WebSocketSession> sessionsSubscribingEvents = new ConcurrentHashMap<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		switch(session.getUri().getPath()) {
		case "/environmental_info":
			this.sessionsSubscribeingEnvironmentalInfo.put(session.getId(), session);
			break;
		case "/monitorng_mode":
			this.sessionsSubscribingMonitoringMode.put(session.getId(), session);
			break;
		case "/events":
			this.sessionsSubscribingEvents.put(session.getId(), session);
			break;
		}
		logger.info("WebSocket connection established. [{}]", session.getUri().getPath());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		switch(session.getUri().getPath()) {
		case "/environmental_info":
			this.sessionsSubscribeingEnvironmentalInfo.remove(session.getId(), session);
			break;
		case "/monitorng_mode":
			this.sessionsSubscribingMonitoringMode.remove(session.getId(), session);
			break;
		case "/events":
			this.sessionsSubscribingEvents.remove(session.getId(), session);
			break;
		}
		logger.info("WebSocket connection closed. [{}]", session.getUri().getPath());
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	}
}