package com.randioo.mahjong_public_server.util.vote;

import java.util.HashMap;
import java.util.Map;

import org.springframework.aop.TargetClassAware;

import com.randioo.randioo_server_base.template.Function;

public class VoteBox<T> {

	private Map<T, Boolean> resultMap;
	private VoteListener voteListener;
	private boolean hasSetTotalCount;
	private int totalCount;
	private VoteStrategy<T> voteStrategy;
	private int voteTimes;
	private T applyTarget;
	private Function generateFunction;

	public VoteBox() {
		resultMap = new HashMap<>();
		generateFunction = new Function() {

			@Override
			public Object apply(Object... params) {
				generateVoteId();
				return null;
			}
		};
	}

	public void reset() {
		this.resultMap.clear();
		this.applyTarget = null;
	}

	public int applyVote(T t) {
		if (applyTarget != null)
			throw new RuntimeException("vote is running");
		return voteTimes;
	}

	private void generateVoteId() {
		voteTimes++;
	}

	public void agree(T t, int voteId) {
		if (voteId != voteTimes)
			return;
		this.vote(t, true);
	}

	public void reject(T t, int voteId) {
		if (voteId != voteTimes)
			return;
		this.vote(t, false);
	}

	private void vote(T t, boolean vote) {
		this.checkAllowVote();
		resultMap.put(t, vote);

		voteStrategy.vote(resultMap, totalCount, voteListener, generateFunction, applyTarget);
	}

	private void checkAllowVote() {
		if (!hasSetTotalCount)
			throw new RuntimeException("not set total count");
		if (voteStrategy == null)
			throw new RuntimeException("not set strategy");
	}

	public void setListener(VoteListener voteListener) {
		this.voteListener = voteListener;
	}

	public VoteBox<T> setTotalCount(int totalCount) {
		this.hasSetTotalCount = true;
		this.totalCount = totalCount;
		return this;
	}

}
