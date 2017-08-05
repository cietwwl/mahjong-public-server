package com.randioo.mahjong_public_server.module.fight.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.CardList;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Gang;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Hu;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Peng;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Step5Hu;

/**
 * 检查其他人有没有要叫牌的但是还没有选择,callCardList必须按照胡杠碰吃的顺序排好<br>
 * 如果有好几个胡，就要等待别人选择胡<br>
 * <br>
 * myseatedIndex = 2 <br>
 * clazz = Chi.class<br>
 * <br>
 * callCardLists: { <br>
 * Hu.class seatedIndex = 1<br>
 * Hu.class seatedIndex = 2<br>
 * Peng.class seatedIndex = 3<br>
 * Chi.class seatedIndex = 2<br>
 * }<br>
 * 上例表示需要等待别人做出选择<br>
 * 
 * @param gameId
 * @return true表示存在
 * @author wcy 2017年6月13日
 */

@Component
public class WaitOtherCallCardListChecker {
    /**
     * 是否要等待他人选择
     * 
     * @param callCardLists 所有可以叫的牌
     * @param seatedIndex 当前的座位号
     * @return true 需要等待 false 不需要等待
     */
    public boolean needWaitOtherChoice(List<CallCardList> callCardLists, int seatedIndex) {

        // 等待的选择叫牌数量是0,则无需等待
        if (callCardLists.size() == 0) {
            return false;
        }
        // 先只查第一个，如果不是自己，并且不是胡则返回true
        CallCardList callCardList0 = callCardLists.get(0);
        CardList cardList = callCardList0.cardList;

        // 如果卡组是胡
        if (cardList instanceof Hu) {
            // 是否包含胡
            boolean containsHu = false;
            // 都叫了胡
            boolean allCallHu = true;
            // 检查是否有并列的胡,并且还没有叫过,则还需要等待
            for (int i = 0; i < callCardLists.size(); i++) {
                CallCardList callCardList = callCardLists.get(i);
                CardList targetCardList = callCardList.cardList;
                // 如果不是胡的类型了,说明没有胡牌类型了
                if (!this.checkHuInstance(targetCardList)) {
                    if (containsHu && allCallHu)
                        return false;
                    else {
                        // 不是胡类型的第一个叫牌不是自己
                        if (seatedIndex != callCardList.masterSeat) {
                            // 没叫就要等待
                            return true;
                        }
                    }

                }
                containsHu = true;
                // 是胡牌 座位不是自己,并且还没有叫过的胡就要等待
                if (seatedIndex != callCardList.masterSeat) {
                    if (!callCardList.call) {
                        allCallHu = false;
                        return true;
                    }
                }
            }

        } else if (seatedIndex != callCardList0.masterSeat) {
            // 卡组不是胡，就不可能存在并列问题，直接检查位置是否是自己,不是就返回需要等待
            return true;
        }

        return false;
    }

    /**
     * 是否是胡的实例
     * 
     * @param cardList
     * @return
     * @author wcy 2017年8月2日
     */
    private boolean checkHuInstance(CardList cardList) {
        return cardList instanceof Hu;
    }

    public static void main(String[] args) {
        WaitOtherCallCardListChecker checker = new WaitOtherCallCardListChecker();
        List<CallCardList> callCardLists = new ArrayList<>();
        {
            CallCardList callCardList = new CallCardList();
            callCardList.cardList = new Step5Hu();
            callCardList.masterSeat = 0;
            callCardList.call = true;
            callCardLists.add(callCardList);
        }
//        {
//            CallCardList callCardList = new CallCardList();
//            callCardList.cardList = new Step5Hu();
//            callCardList.masterSeat = 0;
//            callCardList.call = true;
//            callCardLists.add(callCardList);
//        }
//        {
//            CallCardList callCardList = new CallCardList();
//            callCardList.cardList = new Step5Hu();
//            callCardList.masterSeat = 3;
//            callCardList.call = true;
//            callCardLists.add(callCardList);
//        }
        {
            CallCardList callCardList = new CallCardList();
            callCardList.cardList = new Gang();
            callCardList.masterSeat = 2;
            callCardList.call = false;
            callCardLists.add(callCardList);
        }

        boolean needWait = checker.needWaitOtherChoice(callCardLists, 2);
        System.out.println(needWait ? "需要等待" : "不需要等待");
    }
}
