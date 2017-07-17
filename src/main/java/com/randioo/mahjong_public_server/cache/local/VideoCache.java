package com.randioo.mahjong_public_server.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.bo.VideoData;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;

public class VideoCache {
	private static Map<Integer, VideoData> videoMap = new HashMap<>();

	public static Map<Integer, VideoData> getVideoMap() {
		return videoMap;
	}

	public static List<SC> getCurrentSCList(int gameId, int finishRound) {
		List<SC> list = null;
		if (videoMap.get(gameId) == null) {
			VideoData videoData = new VideoData();
			videoMap.put(gameId, videoData);
		}
		if (finishRound + 2 > videoMap.get(gameId).getScList().size()) {
			list = new ArrayList<>();
			videoMap.get(gameId).getScList().add(list);
		}
		list = videoMap.get(gameId).getScList().get(finishRound + 1);
		return list;
	}
}
