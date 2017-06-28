package com.randioo.mahjong_public_server.httpserver;

import com.sun.net.httpserver.HttpExchange;

public interface LiteFilter {
	public void doFilter(HttpExchange exchange);
}
