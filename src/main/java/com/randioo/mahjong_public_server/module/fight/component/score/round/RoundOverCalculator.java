package com.randioo.mahjong_public_server.module.fight.component.score.round;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Hu;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;

@Component
public class RoundOverCalculator {

    /**
     * 根据结果参数获得最后的回合结果集
     * 
     * @param roundOverParameter
     * @return
     * @author wcy 2017年8月1日
     */
    public Map<Integer, RoundOverResult> getRoundOverResults(RoundOverParameter roundOverParameter) {
        boolean checkHu = roundOverParameter.checkHu;
        int minScore = roundOverParameter.minScore;
        int flyScore = roundOverParameter.flyScore;
        List<CallCardList> huCallCardLists = roundOverParameter.huCallCardList;
        List<String> roleIdList = roundOverParameter.roleIdList;

        Map<Integer, RoundOverResult> results = new HashMap<>();
        for (int i = 0; i < roleIdList.size(); i++) {
            RoundOverResult result = new RoundOverResult();
            result.seat = i;
            results.put(result.seat, result);
        }

        for (int seat = 0; seat < roleIdList.size(); seat++) {
            RoundOverResult result = results.get(seat);
            boolean containsHu = false;
            if (checkHu) {
                // 查胡
                for (CallCardList callCardList : huCallCardLists) {
                    if (callCardList.masterSeat != seat)
                        continue;

                    containsHu = true;
                    Hu hu = (Hu) callCardList.cardList;

                    result.overMethod = OverMethod.OVER_HU;

                    if (hu.isMine) {// 自摸 的人底分x3,苍蝇x3，如果是杠开还要再乘2，每家都扣分
                        result.overMethod = OverMethod.OVER_MO_HU;
                        result.score = minScore * (3 * (hu.gangKai ? 2 : 1)) + flyScore * 3;
                        result.moScore = result.score;

                        for (RoundOverResult roundOverResult : results.values()) {
                            if (roundOverResult.seat == seat) {
                                continue;
                            }

                            roundOverResult.score += -(minScore * (hu.gangKai ? 2 : 1) + flyScore);
                        }
                    } else if (hu.gangChong) {// 杠冲底分x3,苍蝇x3,被杠冲的人扣相同分数
                        result.score += (minScore + flyScore) * 3;
                        RoundOverResult targetRoundOverResult = results.get(hu.getTargetSeat());
                        targetRoundOverResult.score += -(minScore + flyScore) * 3;
                    } else {
                        result.score += minScore + flyScore;
                        RoundOverResult targetRoundOverResult = results.get(hu.getTargetSeat());
                        targetRoundOverResult.score += -(minScore + flyScore);
                    }

                    result.gangKai = hu.gangKai;

                }
            }

            // 没胡就是输，检查点冲
            if (!containsHu) {
                result.overMethod = OverMethod.OVER_LOSS;
                if (checkHu) {
                    // 检查是否被点冲
                    for (CallCardList huCallCardList : huCallCardLists) {
                        Hu hu = (Hu) huCallCardList.cardList;
                        if (hu.getTargetSeat() == seat) {
                            // 点冲
                            result.overMethod = OverMethod.OVER_CHONG;
                            break;
                        }
                    }
                }

            }
        }

        return results;
    }

    public static void main(String[] args) {

    }

}
