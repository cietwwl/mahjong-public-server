package com.randioo.mahjong_public_server.httpserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.randioo_server_base.log.HttpUtils;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class DefaultFilter extends Filter {

	@Override
	public void doFilter(HttpExchange httpexchange, Chain chain) throws IOException {
		System.out.println("filter");
		String param = httpexchange.getRequestURI().getRawQuery();
		Map<String, List<String>> map = new HashMap<>();
		HttpUtils.getParams(map, param);
		for (Map.Entry<String, List<String>> entrySet : map.entrySet()) {
			String key = entrySet.getKey();
			List<String> values = entrySet.getValue();
			httpexchange.setAttribute(key, values.size() > 1 ? values : values.get(0));
		}

		chain.doFilter(httpexchange);
	}

	@Override
	public String description() {
		return "defaultFilter";
	}

	@SuppressWarnings("unchecked")
	private static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String[] param = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}

	public static void main(String[] args) {
		Map<String, Object> parameters = new HashMap<>();
		String query = "name=\"test\"wcy";
		try {
			parseQuery(query, parameters);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(parameters);
	}

}
