package com.randioo.mahjong_public_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.util.Lists;
import com.randioo.mahjong_public_server.util.Sets;
import com.randioo.randioo_server_base.template.Ref;

public class ZLPBaiDaHu extends Hu {
    private Logger logger = LoggerFactory.getLogger(ZLPBaiDaHu.class.getSimpleName());

    @Override
    public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
            List<CardList> showCardList, boolean isMine) {

        Set<Integer> baiDaHu = new HashSet<>();
        baiDaHu.add(801);

        boolean hasHu = this.checkHu(gameConfigData, cardSort);
        if (!hasHu)
            return;

        // 这个胡是zlp想出来的，我只是负责实现，如果出现问题请找他改进算法
        ZLPBaiDaHu hu = new ZLPBaiDaHu();
        List<Integer> list = cardSort.toArray();
        hu.card = card;
        hu.isMine = isMine;
        Lists.removeElementByList(list, Arrays.asList(card));
        Collections.sort(list);
        hu.handCards.addAll(list);
        hu.showCardList.addAll(showCardList);

        cardLists.add(hu);

    }

    private boolean checkHu(GameConfigData gameConfigData, CardSort cardSort) {
        boolean debug = true;
        // 1.克隆牌组
        CardSort cardSort1 = cardSort.clone();

        List<Integer> l = cardSort1.toArray();
        Collections.sort(l);
        System.out.println(l);

        // 2.去除所有的白搭
        int baiDaCount = cardSort1.removeAll(801);

        // 3.三个一样的先拿走
        List<Integer> kezi_arr = new ArrayList<>(cardSort1.get(2));
        for (int kezi : kezi_arr)
            cardSort1.remove(kezi, kezi, kezi);

        // 4.以每个数字为基准,分别从头到尾吃一遍
        Set<Integer> indexSet = new HashSet<>();
        {
            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);

            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                logger.debug(startIndex + "");
                int step4chiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step4chiCount += getLoopChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step4chiCount=" + step4chiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));
                logger.debug("remain=" + cloneCards);
                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    // 可以胡
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }

        }

        System.out.println("//////////////////////////////");

        // 5.刻子拿回来以每个数字为基准,分别从头到尾吃一遍,但有四个相同的先拿走三个
        {
            for (int kezi : kezi_arr) {
                cardSort1.addCard(kezi);
                cardSort1.addCard(kezi);
                cardSort1.addCard(kezi);
            }

            List<Integer> gangzi_arr = new ArrayList<>(cardSort1.get(3));
            for (int gangzi : gangzi_arr)
                cardSort1.remove(gangzi, gangzi, gangzi);

            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);

            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                int step5chiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step5chiCount += getLoopChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step5chiCount=" + step5chiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));

                step5chiCount += check3(cloneCards);
                logger.debug("remain=" + cloneCards);
                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    // 可以胡
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }
        }

        // 6.如果都没有胡,则先选择碰,再选择吃,三个一样的先拿走
        for (int kezi : kezi_arr)
            cardSort1.remove(kezi, kezi, kezi);

        {
            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);
            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                int step6pengChiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step6pengChiCount += getLoopPengChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step6pengChiCount=" + step6pengChiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));
                logger.debug("remain=" + cloneCards);
                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }
        }

        // 7.刻子放回去,先选择碰,再选择吃,但有四个相同的先拿走三个
        {
            List<Integer> gangzi_arr = new ArrayList<>(cardSort1.get(3));
            for (int gangzi : gangzi_arr)
                cardSort1.remove(gangzi, gangzi, gangzi);

            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);
            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                int step7pengChiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step7pengChiCount += getLoopPengChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step6pengChiCount=" + step7pengChiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));
                logger.debug("remain=" + cloneCards);
                step7pengChiCount += check3(cloneCards);

                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查是否只剩下将牌
     * 
     * @param baiDaCountRef
     * @param remainCards
     * @return
     * @author wcy 2017年7月24日
     */
    private boolean checkOnlyJiangCards(Ref<Integer> baiDaCountRef, List<Integer> remainCards) {
        // 百搭牌数量
        int baiDaCount = baiDaCountRef.get();
        if ((baiDaCount + remainCards.size()) != 2)
            return false;

        // 如果剩余两张牌，比较一下之间的大小
        if (remainCards.size() == 2) {
            int card1 = remainCards.get(0);
            int card2 = remainCards.get(1);
            if (card1 == card2) {
                return true;
            }
        }

        // 其中一张是红中,那无论如何都能胡
        boolean hasBaiDa = baiDaCount == 1;

        return hasBaiDa;
    }

    private int check3(List<Integer> cloneCards) {
        int count = 0;
        for (int v = cloneCards.size() - 1; v >= 0; v--) {
            int remainCard = cloneCards.get(v);
            int value = Lists.containsCount(cloneCards, remainCard);
            if (value == 3) {
                count++;
                Lists.removeElementByList(cloneCards, Arrays.asList(remainCard, remainCard, remainCard));
                // 复位
                v = cloneCards.size();
            }
        }
        return count;
    }

    public int getLoopChiCount(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex, Set<Integer> indexSet) {
        indexSet.clear();
        int count1 = getChiCountAndRecordUseIndex(cards, baiDaCountRef, startIndex, cards.size(), indexSet);
        int count2 = getChiCountAndRecordUseIndex(cards, baiDaCountRef, 0, cards.size(), indexSet);
        return count1 + count2;
    }

    /**
     * 获得吃的数量并记录使用过的位置
     * 
     * @param cards
     * @param baiDaCountRef
     * @param startIndex
     * @param endIndex
     * @param indexSet
     * @return
     * @author wcy 2017年7月21日
     */
    public int getChiCountAndRecordUseIndex(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex,
            int endIndex, Set<Integer> indexSet) {
        int count = 0;
        for (int i = startIndex; i < endIndex; i++) {
            // 如果该索引已经使用过了,则直接继续
            if (indexSet.contains(i))
                continue;
            int c1 = cards.get(i);

            // // 超出边界则直接跳过
            // if ((c1 + 1) % 100 >= 10)
            // continue;
            //
            // if ((c1 - 1) % 100 >= 10)
            // continue;

            int c2Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 1);
            int c3Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 2);

            // 检查有没有这个吃
            if (c2Index >= 0 && c3Index >= 0) {
                // 加入index
                Sets.add(indexSet, i, c2Index, c3Index);
                count++;
                continue;
            } else if (c2Index == -1 && c3Index != -1) { // 如果有红中,则使用红中
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    Sets.add(indexSet, i, c3Index);
                    count++;
                    continue;
                }
            } else if (c2Index != -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    count++;
                    Sets.add(indexSet, i, c2Index);
                    continue;
                }
            } else if (c2Index == -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 2) {
                    baiDaCountRef.set(baiDaCountRef.get() - 2);
                    count++;
                    Sets.add(indexSet, i);
                    continue;
                }
            }

        }

        return count;
    }

    /**
     * 找到没有使用过的指定对象索引
     * 
     * @param cards
     * @param indexSet 使用过的对象索引
     * @param startIndex
     * @param card
     * @return
     * @author wcy 2017年7月21日
     */
    private int findUnuseCardIndex(List<Integer> cards, Set<Integer> indexSet, int startIndex, int endIndex, int card) {
        int c2Index = Lists.indexOf(cards, startIndex, endIndex, card);
        if (c2Index == -1)
            return -1;
        while (indexSet.contains(c2Index)) {
            c2Index++;
            if (c2Index > endIndex)
                return -1;
            c2Index = Lists.indexOf(cards, c2Index, endIndex, card);
        }

        return c2Index;
    }

    public int getLoopPengChiCount(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex,
            Set<Integer> indexSet) {
        indexSet.clear();
        int count1 = getPengAndChiCountAndRecordUnuseIndex(cards, baiDaCountRef, startIndex, cards.size(), indexSet);
        int count2 = getPengAndChiCountAndRecordUnuseIndex(cards, baiDaCountRef, 0, cards.size(), indexSet);
        return count1 + count2;
    }

    public int getPengAndChiCountAndRecordUnuseIndex(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex,
            int endIndex, Set<Integer> indexSet) {
        int count = 0;
        for (int i = startIndex; i < endIndex; i++) {
            // 如果该索引已经使用过了,则直接继续
            if (indexSet.contains(i))
                continue;
            int c1 = cards.get(i);

            if (i + 1 < endIndex) {
                // 找对子
                int c2 = cards.get(i + 1);
                if (c1 == c2) {
                    if (baiDaCountRef.get() > 0) {
                        baiDaCountRef.set(baiDaCountRef.get() - 1);
                        Sets.add(indexSet, i, i + 1);
                        count++;
                        continue;
                    }
                }

            }

            // 超出边界则直接跳过
            if ((c1 + 2) % 100 >= 10)
                continue;
            // 找吃
            int c2Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 1);
            int c3Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 2);

            // 检查有没有这个吃
            if (c2Index >= 0 && c3Index >= 0) {
                // 加入index
                Sets.add(indexSet, i, c2Index, c3Index);
                count++;
                continue;
            } else if (c2Index == -1 && c3Index != -1) { // 如果有红中,则使用红中
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    Sets.add(indexSet, i, c3Index);
                    count++;
                    continue;
                }
            } else if (c2Index != -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    Sets.add(indexSet, i, c2Index);
                    count++;
                    continue;
                }
            } else if (c2Index == -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 2) {
                    baiDaCountRef.set(baiDaCountRef.get() - 2);
                    Sets.add(indexSet, i);
                    count++;
                    continue;
                }
            }
        }
        return count;
    }

    @Override
    public void checkTing(CardSort cardSort, List<Integer> waitCards, GameConfigData gameConfigData) {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        return "cardList:hu=>gangkai=" + gangKai + ",isMine=" + isMine + ",card=" + card + "," + super.toString();
    }

    @Override
    public List<Integer> getCards() {
        return null;
    }

    // public static void main(String[] args) {
    //
    // ZLPBaiDaHu hu = new ZLPBaiDaHu();
    // for (int i = 0; i < 6; i++) {
    // int count = hu.getLoopChiCount(Arrays.asList(101, 101, 102, 102, 103,
    // 103), 0, i);
    // System.out.println(count);
    // }
    //
    // }

    public static void main(String[] args) {
        ZLPBaiDaHu hu = new ZLPBaiDaHu();
        CardSort cardSort = new CardSort(4);
        // cardSort.fillCardSort(Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302));

        // List<Integer> cards = Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);
        // List<Integer> cards = Arrays.asList(101, 102, 201, 302, 101, 102,
        // 201, 302, 801, 801, 302);
        // List<Integer> cards = Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);

        // List<Integer> cards = Arrays.asList(101, 102, 103, 801, 801, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);

        // List<Integer> cards = Arrays.asList(101, 102, 104, 104, 104, 107,
        // 107, 108, 108, 201, 203, 801, 801, 801);
        // List<Integer> cards = Arrays.asList(103, 104, 105, 205, 205, 206,
        // 801, 206);
        // List<Integer> cards = Arrays.asList(108, 109, 302, 302, 801, 801,
        // 801, 207);
        List<Integer> cards = Arrays.asList(108, 109, 201, 202, 203, 203, 203, 801);

        cardSort.fillCardSort(cards);

        long start = System.currentTimeMillis();
        hu.checkHu(null, cardSort);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    // public static void main(String[] args) {
    // List<Integer> indexList = new ArrayList<>(Arrays.asList(2, 3, 5,5));
    // List<Integer> sources = new ArrayList<>(Arrays.asList(100, 101, 102, 103,
    // 104, 105, 106));
    // Lists.removeAllIndex(sources, indexList);
    // System.out.println(sources);
    //
    // }

    // public static void main(String[] args) {
    // List<Integer> sources = new ArrayList<>(Arrays.asList(100, 101, 101, 103,
    // 104, 105, 106));
    // int count = Lists.containsCount(sources, 101);
    // System.out.println(count);
    //
    // }
}
