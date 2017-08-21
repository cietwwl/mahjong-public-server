package com.randioo.mahjong_public_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.protocol.Entity.ClientCard;
import com.randioo.randioo_server_base.utils.RandomUtils;

/**
 * 客户端分牌
 * 
 * @author wcy 2017年8月14日
 *
 */
@Component
public class ClientDispatcher implements Dispatcher {

    @Override
    public List<CardPart> dispatch(Game game, List<Integer> originCards, int partCount, int everyPartCount) {

        List<CardPart> cardParts = new ArrayList<>();
        // 先将用户选定的牌全部生成
        List<ClientCard> clientCards = game.getClientCards();
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = new CardPart();
            try {
                ClientCard cards = clientCards.get(i);
                for (int card : cards.getCardsList()) {
                    cardPart.add(card);
                    int index = originCards.indexOf(card);
                    if (index != -1) {
                        originCards.remove(index);
                    }
                }
            } catch (Exception e) {

            }

            cardParts.add(cardPart);

        }
        // 再检查卡牌数量是否正确，不正确的就补牌
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = cardParts.get(i);
            for (int j = cardPart.size(); j < everyPartCount; j++) {
                int index = RandomUtils.getRandomNum(originCards.size());
                int card = originCards.get(index);
                cardPart.add(card);
            }
        }
        return cardParts;
    }

}
