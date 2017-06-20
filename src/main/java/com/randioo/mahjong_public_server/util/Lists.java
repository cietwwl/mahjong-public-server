package com.randioo.mahjong_public_server.util;

import java.util.ArrayList;
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

	/**
	 * 
	 * @param souceList
	 * @param removeList
	 * @author wcy 2017年6月19日
	 */
	public static void removeElementByList(List<Integer> sourceList, List<Integer> removeList) {
		for (int card : removeList) {
			for (int i = sourceList.size() - 1; i >= 0; i--) {
				if (sourceList.get(i) == card) {
					sourceList.remove(i);
					break;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		list.add(14);
		list.add(14);
		list.add(14);
		list.add(14);
		List<Integer> removeList = new ArrayList<>();
		removeList.add(14);
		removeList.add(14);
		removeList.add(14);
		removeElementByList(list, removeList);
		System.out.println(list);
	}
}
