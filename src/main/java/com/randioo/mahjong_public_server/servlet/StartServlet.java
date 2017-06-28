package com.randioo.mahjong_public_server.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.httpserver.LiteServlet;
import com.randioo.mahjong_public_server.module.race.action.RaceKickAction;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

@Service
public class StartServlet extends LiteServlet {

	@Autowired
	private RaceKickAction raceKickAction;

	@Override
	protected void doGet(HttpExchange exchange) {
		Headers responseHeaders = exchange.getResponseHeaders();
		try {
			exchange.sendResponseHeaders(200, 0);
			responseHeaders.set("Content-Type", "text/plain");
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream outputStream = exchange.getResponseBody();
		try (BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {
			try {
				raceKickAction.execute(exchange, null);
				bos.write("ok".getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				bos.write("kick failed".getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpExchange liteRequest) {

	}
}
