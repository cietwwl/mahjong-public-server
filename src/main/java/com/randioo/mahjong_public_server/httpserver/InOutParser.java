package com.randioo.mahjong_public_server.httpserver;

public interface InOutParser<I, O> {
	public void parse(I exchange, O liteRequest);
}
