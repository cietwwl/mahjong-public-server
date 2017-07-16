package com.randioo.mahjong_public_server.util.vote;

import java.util.Map;

import com.randioo.randioo_server_base.template.Function;

public interface VoteStrategy<T> {
	public void vote(Map<T, Boolean> map, int totalCount, VoteListener voteListener, Function generatedFunction,
			T applyer);
}
