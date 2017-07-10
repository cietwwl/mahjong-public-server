package com.randioo.mahjong_public_server.randioo_race_sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParameter {

	private Map<String, List<String>> map = new HashMap<>();

	public void put(String key, String value) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<String>());
		}

		List<String> list = map.get(key);
		list.add(value);
	}

	public void remove(String key, String value) {
		if (map.containsKey(key)) {
			List<String> list = map.get(key);
			for (int i = list.size() - 1; i >= 0; i--) {
				if (list.get(i).equals(value)) {
					list.remove(i);
					break;
				}
			}
		}
	}

	public Map<String, List<String>> getParameterMap() {
		return map;
	}
}
