/*
 * Copyright 2012-2015 the original author or authors.
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

package com.vz.sim;

import java.io.IOException;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ecp")
public class EcpWebSocketEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("session.getId()   "+session.getId());
		AlarmProducer.initialize("ecp");
		AlarmProducer.add(session);
	}
	@OnMessage
	public void handleMessage(Session session, String message) throws IOException {
		session.getBasicRemote()
				.sendText("Reversed: " + new StringBuilder(message).reverse());
	}
}
