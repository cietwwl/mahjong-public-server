package com.randioo.mahjong_public_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.util.Lists;

@Component
public class DebugDispatcher implements Dispatcher {

    @Override
    public List<CardPart> dispatch(Game game, List<Integer> originCards, int partCount, int everyPartCount) {

        List<Integer> remainCards = originCards;
        // 杠冲
        // int[][] arrs = { { 11, 11, 16, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23
        // },
        // { 12, 13, 36, 37, 38, 37, 38, 39, 23, 24, 25, 22, 22 },
        // { 26, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
        // { 25, 27, 27, 37, 18, 21, 26, 29, 27, 28, 33, 34, 39 } };
        // 一炮多响
        // int[][] arrs = { { 103, 103, 103, 306, 307, 308, 307, 308, 309, 204,
        // 205, 202, 202 },
        // { 203, 207, 207, 307, 108, 201, 206, 209, 207, 208, 303, 304, 309 },
        // { 203, 203, 204, 801, 205, 301, 301, 301, 303, 303, 303, 305, 203 },
        // { 101, 101, 101, 102, 102, 102, 103, 104, 105, 201, 201, 201, 202 }
        // };
        // // gangkai
        // int[][] arrs = { { 11, 11, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23, 23
        // },
        // { 13, 13, 13, 36, 37, 38, 37, 38, 39, 14, 15, 16, 22 },
        // { 22, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
        // { 25, 27, 27, 37, 18, 21, 22, 29, 27, 28, 33, 34, 39 } };
        // hu
        // int[][] arrs = { { 101, 101, 102, 102, 102, 103, 104, 105, 201, 201,
        // 201, 203, 203 },
        // { 103, 103, 103, 306, 307, 308, 307, 308, 309, 104, 105, 106, 202,
        // 205 },
        // { 202, 205, 205, 107, 108, 206, 209, 204, 207, 304, 305, 306, 801 },
        // { 103, 207, 207, 307, 108, 201, 202, 209, 207, 208, 303, 304, 309 }
        // };
        // peng
        // int[][] arrs = { { 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 81
        // },
        // { 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 81 },
        // { 22, 25, 25, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
        // { 25, 27, 27, 37, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };

        // gang
        // int[][] arrs = { { 101, 101, 101, 101, 103, 103, 104, 104, 104, 104,
        // 106, 106, 801 },
        // { 102, 102, 102, 102, 103, 202, 103, 105, 105, 105, 105, 106, 801 },
        // { 202, 205, 205, 107, 108, 201, 203, 204, 207, 304, 305, 306, 801 },
        // { 205, 207, 207, 307, 108, 201, 202, 203, 207, 208, 303, 304, 309 }
        // };
        // 四个红中直接胡
        int[][] arrs = { { 101, 101, 101, 101, 103, 801, 104, 104, 104, 104, 801, 801, 801 },
                { 102, 102, 102, 102, 103, 202, 103, 105, 105, 105, 105, 106, 106 },
                { 202, 205, 205, 107, 108, 201, 203, 204, 207, 304, 305, 306, 106 },
                { 205, 207, 207, 307, 108, 201, 202, 203, 207, 208, 303, 304, 309 } };
        // int[][] arrs = { { 11, 11, 11, 13, 13, 13, 14, 14, 14, 15, 15, 15, 81
        // },
        // { 14, 21, 25, 25, 26, 29, 29, 31, 33, 36, 38, 81, 38 },
        // { 22, 12, 16, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
        // { 12, 16, 16, 37, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };

        List<CardPart> cardParts = new ArrayList<>();
        List<Integer> removeList = new ArrayList<>();
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = new CardPart();
            cardParts.add(cardPart);
            String gameRoleId = game.getRoleIdList().get(i);
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            roleGameInfo.cards.clear();

            for (int j = 0; j < everyPartCount; j++) {
                cardPart.add(arrs[i][j]);
                removeList.add(arrs[i][j]);
            }
        }

        Lists.removeElementByList(remainCards, removeList);
        // remainCards.clear();
        // remainCards.add(203);

        Collections.sort(remainCards);
        return cardParts;
    }

}
