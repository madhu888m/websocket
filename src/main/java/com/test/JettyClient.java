package com.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class JettyClient extends WebSocketAdapter{
	public JettyClient() {
		openWobSocket();
	}
	public static void main(String[] args) {
		JettyClient clitnt = new JettyClient();
	}
	private void openWobSocket() {
		try {
			WebSocketClient client = new WebSocketClient();
			client.start();
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			Future<Session> future = client.connect(this, new URI("ws://localhost:8080/ecp"), request);
			System.out.println(future.get().getUpgradeResponse().getStatusCode());
			TimeUnit.SECONDS.sleep(50);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
	}
	@Override
	public void onWebSocketClose(int statusCode, String reason) {
	}
	@Override
	public void onWebSocketConnect(Session sess) {
	}
	@Override
	public void onWebSocketError(Throwable cause) {
	}
	@Override
	public void onWebSocketText(String message) {
		System.out.println(message);
	}
}
