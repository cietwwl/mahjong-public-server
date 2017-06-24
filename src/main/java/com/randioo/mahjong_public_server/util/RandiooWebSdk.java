package com.randioo.mahjong_public_server.util;

import java.util.Map;

public class RandiooWebSdk {
	public static int getMoney(String ip, Map<String, String> paramMap) throws Exception {
		String urlStr = "http://10.0.51.17/APPadmin/gateway/MaJiang/getMoney.php?key=f4f3f65d6d804d138043fbbd1843d510"
				+ formatUrlParam(paramMap);
		String result = HttpConnnection.sendMessageGet(urlStr);
		if (result == null) {
			return 0;
		}
		String params[] = result.split("\"");
		int gold = 0;
		for (int i = 0; i < params.length; i++) {
			if (params[i].equals("randioo_money")) {
				gold = Integer.parseInt(params[i + 2]);
				break;
			}
		}
		if (gold < 0) {
			gold = 0;
		}
		return gold;
	}

	public static void achieve(Map<String, String> paramMap) throws Exception {
		String urlStr = "" + formatUrlParam(paramMap);
		String result = HttpConnnection.sendMessageGet(urlStr);

	}

	public static String formatUrlParam(Map<String, String> map) {
		StringBuffer urlParam = new StringBuffer();
		int i = 0;
		for (String key : map.keySet()) {
			if (i != 0)
				urlParam.append("&");
			urlParam.append(key + "=" + map.get(key));
			i++;
		}
		return urlParam.toString();
	}
}
