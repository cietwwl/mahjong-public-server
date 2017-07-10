package com.randioo.mahjong_public_server.httpserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.randioo_server_base.utils.HttpUtils;
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

}
