package com.hpe.ha.ipe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hpe.ha.ipe.web.ServerPushBroker;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	ServerPushBroker serverPushBroker;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registory) {
		// TODO Auto-generated method stub
		registory.addHandler(serverPushBroker, "/monitorng_mode");
		registory.addHandler(serverPushBroker, "/environmental_info");
		registory.addHandler(serverPushBroker, "/events");
	}

}
