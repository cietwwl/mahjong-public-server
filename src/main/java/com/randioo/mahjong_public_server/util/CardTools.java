package com.randioo.mahjong_public_server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;

public class CardTools {
	public static final int TONG = 1;
	public static final int TIAO = 2;
	public static final int WAN = 3;
	public static final int ZHONG = 8;

	public final static int[] CARDS = { 
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条

			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒

			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			// 41, 41, 41, 41,// 东
			// 51, 51, 51, 51,// 南
			// 61, 61, 61, 61,// 西
			// 71, 71, 71, 71,// 北
			81, 81, 81, 81,// 中
	// 91, 91, 91, 91,// 发
	// A1, A1, A1, A1,// 白
	// B1,// 梅
	// B2,// 兰
	// B3,// 竹
	// B4,// 菊
	// B5,// 春
	// B6,// 夏
	// B7,// 秋
	// B8,// 冬
	// B9,// 财神
	// BA,// 猫
	// BB,// 老鼠
	// BC,// 聚宝盆
	// C1,// 白搭
	// C1,// 白搭
	// C1,// 白搭
	// C1,// 白搭
			
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
			11, 12, 13, 14, 15, 16, 17, 18, 19, // 条

			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
			21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒

			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
			// 41, 41, 41, 41,// 东
			// 51, 51, 51, 51,// 南
			// 61, 61, 61, 61,// 西
			// 71, 71, 71, 71,// 北
			81, 81, 81, 81,// 中
	// 91, 91, 91, 91,// 发
	// A1, A1, A1, A1,// 白
	// B1,// 梅
	// B2,// 兰
	// B3,// 竹
	// B4,// 菊
	// B5,// 春
	// B6,// 夏
	// B7,// 秋
	// B8,// 冬
	// B9,// 财神
	// BA,// 猫
	// BB,// 老鼠
	// BC,// 聚宝盆
	// C1,// 白搭
	// C1,// 白搭
	// C1,// 白搭
	// C1,// 白搭
	};

	/**
	 * 去掉花色
	 * 
	 * @param pai
	 * @return
	 * @author wcy 2017年5月27日
	 */
	public static int toNum(int pai) {
		return pai & 0x0F;
	}

	public static int getType(int pai) {
		return pai & 0xF0 >> 4;
	}

	public static void main(String[] args) {
		CardSort cardSort = new CardSort(4);
		List<Integer> cards = new ArrayList<>();

		cards.add(1);
		cards.add(1);
		cards.add(1);
		cards.add(3);
		cards.add(4);
		cards.add(2);
		cards.add(2);
		cards.add(2);

		cardSort.fillCardSort(cards);
		for (Set<Integer> set : cardSort.getList()) {
			System.out.println(set);
		}
	}

}
