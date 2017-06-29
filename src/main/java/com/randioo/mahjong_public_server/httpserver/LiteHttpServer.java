package com.randioo.mahjong_public_server.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.randioo.mahjong_public_server.servlet.StartServlet;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class LiteHttpServer {

	private static final Logger logger = LoggerFactory.getLogger(LiteHttpServer.class.getSimpleName());
	private int port;
	private String rootPath;
	private LiteHttpHandler liteHttpHandler;
	protected Map<String, LiteServlet> servletMap = new HashMap<>();
	private HttpServer httpServer = null;
	private HttpContext httpContext = null;

	public void init() throws IOException {
		InetSocketAddress addr = new InetSocketAddress(port);
		httpServer = HttpServer.create(addr, 0);
		this.liteHttpHandler = new LiteHttpHandler();
		httpContext = httpServer.createContext(rootPath, liteHttpHandler);
		httpContext.getFilters().add(new ParameterFilter());
		httpServer.setExecutor(Executors.newCachedThreadPool());
		liteHttpHandler.setLiteHttpServer(this);
		httpServer.start();
		logger.info("http Server is listening on port " + port);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRootPath(String context) {
		this.rootPath = context;
	}

	public String getRootPath() {
		return rootPath;
	}

	public HttpContext getHttpContext() {
		return httpContext;
	}

	public void addLiteServlet(String name, LiteServlet servlet) {
		servletMap.put(rootPath + name, servlet);
	}

	public static void main(String[] args) throws IOException {
		LiteHttpServer server = new LiteHttpServer();
		server.setPort(20006);
		server.setRootPath("/majiang");
		server.addLiteServlet("/start", new StartServlet());
		server.init();
	}
}
