package com.randioo.mahjong_public_server.httpserver;

import java.io.IOException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class DefaultLiteFilter implements LiteFilter {
	@Override
	public void doFilter(HttpExchange exchange) {
		Headers responseHeaders = exchange.getResponseHeaders();
		try {
			exchange.sendResponseHeaders(200, 0);
			responseHeaders.set("Content-Type", "text/plain");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
