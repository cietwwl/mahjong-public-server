package com.randioo.mahjong_public_server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CardTools {
	public static final int TONG = 1;
	public static final int TIAO = 2;
	public static final int WAN = 3;
	public static final int ZHONG = 8;
	
	public final static int[] CARDS = { 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,// 筒
		0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,// 筒
		0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,// 筒
		0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,// 筒
		
		0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,// 条
		0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,// 条
		0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,// 条
		0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,// 条
		
		0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,// 万
		0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,// 万
		0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,// 万
		0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,// 万
		// 0x41, 0x41, 0x41, 0x41,// 东
		// 0x51, 0x51, 0x51, 0x51,// 南
		// 0x61, 0x61, 0x61, 0x61,// 西
		// 0x71, 0x71, 0x71, 0x71,// 北
		0x81, 0x81, 0x81, 0x81,// 中
		// 0x91, 0x91, 0x91, 0x91,// 发
		// 0xA1, 0xA1, 0xA1, 0xA1,// 白
		// 0xB1,// 梅
		// 0xB2,// 兰
		// 0xB3,// 竹
		// 0xB4,// 菊
		// 0xB5,// 春
		// 0xB6,// 夏
		// 0xB7,// 秋
		// 0xB8,// 冬
		// 0xB9,// 财神
		// 0xBA,// 猫
		// 0xBB,// 老鼠
		// 0xBC,// 聚宝盆
		// 0xC1,// 白搭
		// 0xC1,// 白搭
		// 0xC1,// 白搭
		// 0xC1,// 白搭
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

	public static void fillCardSort(CardSort cardSort, int pai) {
		int num = toNum(pai);
		for (Set<Integer> set : cardSort.getCardSort()) {
			if (!set.contains(num)) {
				set.add(num);
				break;
			}
		}
	}

	public static void fillCardSort(CardSort cardSort, List<Integer> pais) {
		for (int pai : pais)
			fillCardSort(cardSort, pai);
	}

	public static void rmValues(CardSort cardSort, List<Integer> arr) {
		for (int value : arr) {
			rmValue(cardSort, value);
		}
	}

	public static void rmValue(CardSort cardSort, int value) {
		for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
			Set<Integer> set = cardSort.getCardSort().get(i);
			if (set.contains(value)) {
				set.remove(value);
			}
		}
	}

	public static void rmAllValues(CardSort cardSort, List<Integer> values) {
		for (int value : values) {
			rmAllValue(cardSort, value);
		}
	}

	public static void rmAllValue(CardSort cardSort, int value) {
		for (int i = 0; i < cardSort.getCardSort().size(); i++) {
			Set<Integer> set = cardSort.getCardSort().get(i);
			set.remove(value);
		}
	}

	public static void rmValue(CardSort cardSort, int value, int count) {
		int num = count;
		for (int i = cardSort.getCardSort().size() - 1; i >= 0; i--) {
			Set<Integer> set = cardSort.getCardSort().get(i);
			if (!set.contains(value))
				continue;
			if (num == 0)
				break;
			set.remove(value);
			num--;
		}
	}

	public static void recommandNumCommonTemplate(List<List<Integer>> recommandList, CardSort cardSort,
			CardList lastCardList, int lineIndex, Class<? extends A1> targetClass) {
		cardSort = cardSort.clone();
		// 从第三行往前查找，因为第四行表示炸弹，所以第四行不查
		if (lastCardList == null) {
			// 主动出牌
			for (int i = 2; i >= lineIndex; i--) {
				Set<Integer> set = cardSort.getCardSort().get(i);
				List<List<Integer>> lists = new ArrayList<>();
				for (int pai : set) {
					List<Integer> list = new ArrayList<>(lineIndex + 1);
					for (int j = 0; j < lineIndex + 1; j++)
						list.add(pai);
					lists.add(list);
				}
				List<Integer> temp = new ArrayList<>(set);
				CardTools.rmAllValues(cardSort, temp);
				recommandList.addAll(0, lists);
			}
		} else {
			// 被动出牌
			if (lastCardList.getClass() != targetClass) {
				return;
			}
			A1 a1 = (A1) lastCardList;
			int num = a1.getNum();

			for (int i = 2; i >= lineIndex; i--) {
				Set<Integer> set = cardSort.getCardSort().get(i);
				List<Integer> temp = new ArrayList<>(set);

				List<List<Integer>> lists = new ArrayList<>();
				for (int pai = num + 1; pai <= temp.get(temp.size() - 1); pai++) {
					if (set.contains(pai)) {
						List<Integer> list = new ArrayList<>(lineIndex + 1);
						for (int j = 0; j < lineIndex + 1; j++)
							list.add(pai);
						lists.add(list);
					}
				}
				CardTools.rmAllValues(cardSort, temp);
				recommandList.addAll(0, lists);
			}
		}
	}

	public static void recommandStartNumAndLenCommonTemplate(List<List<Integer>> recommandList, CardSort cardSort,
			CardList lastCardList, List<Integer> arr, int lineIndex, int loopAddCount) {
		if (lastCardList != null) {
			ABCDE abcde = (ABCDE) lastCardList;

			Set<Integer> set = cardSort.getCardSort().get(lineIndex);
			// 如果理论的最后一个值大于A则返回
			for (int startNum = abcde.getNum() + 1; /* 起始值 */(startNum < (startNum + abcde.getLength()))
					&& (startNum + abcde.getLength()) < CardTools.C_A; startNum++) {
				if (!set.contains(startNum))
					continue;

				NOT_HAVE: {
					List<Integer> list = new ArrayList<>();
					// 获得起始值
					for (int value = startNum; value < abcde.getNum() + abcde.getLength(); value++) {
						if (!set.contains(value))
							break NOT_HAVE;

						for (int loop = 0; loop < loopAddCount; loop++)
							list.add(value);
					}
					recommandList.add(list);
				}
			}

		}
	}

}
