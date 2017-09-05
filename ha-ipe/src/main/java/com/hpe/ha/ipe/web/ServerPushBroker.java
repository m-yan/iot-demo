package com.hpe.ha.ipe.web;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.hpe.ha.ipe.util.ApplicationProperties;

import lombok.Getter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ServerPushBroker extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(ServerPushBroker.class);
	
	@Autowired
	ApplicationProperties prop;
	
	@Getter
	private Map<String, WebSocketSession> sessionsSubscribeingEnvironmentalInfo = new ConcurrentHashMap<>();
	
	@Getter
	private Map<String, WebSocketSession> sessionsSubscribingMonitoringMode = new ConcurrentHashMap<>();
	
	@Getter
	private Map<String, WebSocketSession> sessionsSubscribingEvents = new ConcurrentHashMap<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String path = session.getUri().getPath();
		if (path.equals(prop.getWsEnvironmentalInfo())) {
			this.sessionsSubscribeingEnvironmentalInfo.put(session.getId(), session);
		} else if (path.equals(prop.getWsMonitoringMode())) {
			this.sessionsSubscribingMonitoringMode.put(session.getId(), session);
		} else if (path.equals(prop.getWsEvents())) {
			this.sessionsSubscribingEvents.put(session.getId(), session);
		}
		logger.info("WebSocket connection established. [{}]", session.getUri().getPath());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String path = session.getUri().getPath();
		if (path.equals(prop.getWsEnvironmentalInfo())) {
			this.sessionsSubscribeingEnvironmentalInfo.remove(session.getId(), session);
		} else if (path.equals(prop.getWsMonitoringMode())) {
			this.sessionsSubscribingMonitoringMode.remove(session.getId(), session);
		} else if (path.equals(prop.getWsEvents())) {
			this.sessionsSubscribingEvents.remove(session.getId(), session);
		}
		logger.info("WebSocket connection closed. [{}]", session.getUri().getPath());
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	}
}