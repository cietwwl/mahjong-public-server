package com.randioo.mahjong_public_server.httpserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.randioo_server_base.log.HttpUtils;
import com.randioo.randioo_server_base.template.Function;

public class TestHttp {
	public static void main(String[] args) {
		Map<String, List<String>> map = new HashMap<>();
		map.put("test", new ArrayList<String>(1));
		map.get("test").add("wcy");
		map.get("test").add("wcy");
		map.get("test").add("wcy");

		try {
			HttpUtils.get("http://localhost:20006/test", map, new Function() {

				@Override
				public Object apply(Object... params) {
					System.out.println(params[0]);
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
