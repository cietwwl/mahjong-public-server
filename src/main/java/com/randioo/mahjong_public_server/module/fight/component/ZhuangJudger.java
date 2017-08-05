package com.randioo.mahjong_public_server.module.fight.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.module.fight.component.score.round.RoundOverResult;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;

@Component
public class ZhuangJudger {
    /**
     * 获得下一局庄的位置
     * 
     * @param roundOverResultMap
     * @return
     * @author wcy 2017年8月3日
     */
    public int getZhuangSeat(Map<Integer, RoundOverResult> roundOverResultMap) {
        int huCount = 0;
        int dianChongSeat = -1;
        int targetZhuangSeat = -1;
        int size = roundOverResultMap.size();
        for (int seat = 0; seat < size; seat++) {
            RoundOverResult roundOverResult = roundOverResultMap.get(seat);

            OverMethod overMethod = roundOverResult.overMethod;
            if (overMethodIsHu(overMethod)) {
                huCount++;
                if (huCount == 1) {
                    targetZhuangSeat = seat;
                }
            }

            if (overMethod == OverMethod.OVER_CHONG) {
                dianChongSeat = seat;
            }

        }

        if (huCount == 1) {
            return targetZhuangSeat;
        } else if (huCount > 1) {
            return dianChongSeat;
        }
        return -1;
    }

    private boolean overMethodIsHu(OverMethod overMethod) {
        return overMethod == OverMethod.OVER_HU || overMethod == OverMethod.OVER_MO_HU;
    }

    public static void main(String[] args) {
        RoundOverResult r1 = new RoundOverResult();
        r1.overMethod = OverMethod.OVER_LOSS;

        RoundOverResult r2 = new RoundOverResult();
        r2.overMethod = OverMethod.OVER_LOSS;

        RoundOverResult r3 = new RoundOverResult();
        r3.overMethod = OverMethod.OVER_LOSS;

        RoundOverResult r4 = new RoundOverResult();
        r4.overMethod = OverMethod.OVER_LOSS;

        Map<Integer, RoundOverResult> map = new HashMap<>();
        map.put(0, r1);
        map.put(1, r2);
        map.put(2, r3);
        map.put(3, r4);

        ZhuangJudger checker = new ZhuangJudger();
        int seat = checker.getZhuangSeat(map);
        System.out.println(seat);
    }
}
