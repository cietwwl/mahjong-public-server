/**
 * 
 */
package com.randioo.mahjong_public_server.comparator;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.CardList;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Gang;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Hu;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Peng;

/**
 * @Description: 叫牌的排序，胡>杠>碰>吃
 * @author zsy
 * @date 2017年8月14日 下午12:11:59
 */
@Component
public class CallCardListComparator implements Comparator<CallCardList> {

    @Override
    public int compare(CallCardList o1, CallCardList o2) {
        int pority1 = getCardListClazz(o1.cardList);
        int pority2 = getCardListClazz(o2.cardList);
        return pority1 - pority2;
    }

    private int getCardListClazz(CardList cardList) {
        if (cardList instanceof Hu) {
            return 0;
        } else if (cardList instanceof Gang) {
            return 1;
        } else if (cardList instanceof Peng) {
            return 2;
        } else {
            return 3;
        }
    }

}
