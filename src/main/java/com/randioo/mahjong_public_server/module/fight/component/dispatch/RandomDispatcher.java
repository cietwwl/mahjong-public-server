package com.randioo.mahjong_public_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.util.CardTools;
import com.randioo.mahjong_public_server.util.Lists;
import com.randioo.randioo_server_base.utils.RandomUtils;

@Component
public class RandomDispatcher implements Dispatcher {

    @Override
    public List<CardPart> dispatch(List<Integer> cards, int partCount, int everyPartCount) {

        List<CardPart> cardParts = new ArrayList<>(partCount);
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = new CardPart();
            cardParts.add(cardPart);
            for (int j = 0; j < everyPartCount; j++) {
                int index = RandomUtils.getRandomNum(cards.size());
                cardPart.add(cards.get(index));
                cards.remove(index);
            }
        }
        return cardParts;
    }

    public static void main(String[] args) {
        List<Integer> remainCards = new ArrayList<>(CardTools.CARDS.length);
        Lists.fillList(remainCards, CardTools.CARDS);
        RandomDispatcher dispatcher = new RandomDispatcher();
        List<CardPart> list = dispatcher.dispatch(remainCards, 4, 13);
        System.out.println(list);
        System.out.println(remainCards);
    }

}
