package com.randioo.mahjong_public_server.httpserver;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class LiteHttpHandler implements HttpHandler {

	private LiteHttpServer liteHttpServer;

	protected void setLiteHttpServer(LiteHttpServer liteHttpServer) {
		this.liteHttpServer = liteHttpServer;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		LiteServlet liteServlet = liteHttpServer.servletMap.get(path);

		if (requestMethod.equalsIgnoreCase("GET")) {
			doGet(liteServlet, exchange);
		} else if (requestMethod.equalsIgnoreCase("POST")) {
			doPost(liteServlet, exchange);
		}
	}

	private void doGet(LiteServlet liteServlet, HttpExchange exchange) {
		liteServlet.doGet(exchange);
	}

	private void doPost(LiteServlet liteServlet, HttpExchange exchange) {
		liteServlet.doPost(exchange);
	}
}
