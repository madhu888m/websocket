/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.vz.sim.CiscoWebSocketEndpoint;
import com.vz.sim.EcpWebSocketEndpoint;
import com.vz.sim.JunWebSocketEndpoint;

@Configuration
@EnableAutoConfiguration
@EnableWebSocket
public class SampleJettyWebSocketsApplication extends SpringBootServletInitializer
		implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SampleJettyWebSocketsApplication.class);
	}

	@Bean
	public EcpWebSocketEndpoint ecpWebSocketEndpoint() {
		return new EcpWebSocketEndpoint();
	}
	@Bean
	public JunWebSocketEndpoint junWebSocketEndpoint() {
		return new JunWebSocketEndpoint();
	}
	@Bean
	public CiscoWebSocketEndpoint ciscoWebSocketEndpoint() {
		return new CiscoWebSocketEndpoint();
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleJettyWebSocketsApplication.class, args);
	}

}
