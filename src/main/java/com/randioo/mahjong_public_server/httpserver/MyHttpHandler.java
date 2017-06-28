package com.randioo.mahjong_public_server.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class MyHttpHandler extends LiteHttpHandler {

	private void handlerGet1(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, 0);

			OutputStream responseBody = exchange.getResponseBody();
			Headers requestHeaders = exchange.getRequestHeaders();
			Set<String> keySet = requestHeaders.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				List values = requestHeaders.get(key);
				String s = key + " = " + values.toString() + "\n";
				System.out.println(s);
				responseBody.write(s.getBytes());
			}
			responseBody.close();
		}
	}

	private void handlerGet2(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, 0);
			String query = exchange.getRequestURI().getQuery();
			System.out.println(query);

			BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
			String temp = null;
			StringBuilder sb = new StringBuilder();
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}
			System.out.println(sb);

			OutputStream responseBody = exchange.getResponseBody();
			Headers requestHeaders = exchange.getRequestHeaders();
			Set<String> keySet = requestHeaders.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				List values = requestHeaders.get(key);
				String s = key + " = " + values.toString() + "\n";
				// System.out.println(s);
				responseBody.write(s.getBytes());
			}
			responseBody.close();
		}
	}

	private void handler2(HttpExchange httpExchange) throws IOException {
		InputStream in = httpExchange.getRequestBody(); // 获得输入流
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String temp = null;
		StringBuilder sb = new StringBuilder();
		while ((temp = reader.readLine()) != null) {
			sb.append(temp).append("\n");
		}
		System.out.println(sb);

		String responseMsg = "ok"; // 响应信息
		httpExchange.sendResponseHeaders(200, responseMsg.length()); // 设置响应头属性及响应信息的长度
		OutputStream out = httpExchange.getResponseBody(); // 获得输出流
		out.write(responseMsg.getBytes());
		out.flush();
		httpExchange.close();
	}
}
