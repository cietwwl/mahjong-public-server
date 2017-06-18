package com.randioo.mahjong_public_server.util;

import java.util.List;

public class Lists {
	/**
	 * 填充列表
	 * 
	 * @param list
	 * @param arr
	 * @return
	 */
	public static void fillList(List<Integer> list, int[] arr) {
		for (int i : arr)
			list.add(i);

	}
}
