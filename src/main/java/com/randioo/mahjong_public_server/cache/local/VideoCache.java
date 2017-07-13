package com.randioo.mahjong_public_server.cache.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.randioo.mahjong_public_server.entity.bo.Video;

public class VideoCache {
	private static Map<Integer, Video> videoMap = new ConcurrentHashMap<>();

	public static Map<Integer, Video> getVideoMap() {
		return videoMap;
	}
}
