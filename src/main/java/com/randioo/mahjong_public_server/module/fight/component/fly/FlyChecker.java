package com.randioo.mahjong_public_server.module.fight.component.fly;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

@Component
public class FlyChecker {
    /**
     * 计算苍蝇
     * 
     * @param game
     * @return
     * @author wcy 2017年7月31日
     */
    public FlyResult calculateFlys(Game game) {
        FlyResult flyResult = new FlyResult();
        List<Integer> flys = getFlys(game);
        List<Integer> resultFlys = getTargetFlys(game, flys);
        int score = this.getScore(resultFlys, game);

        flyResult.setScore(score);
        flyResult.getTouchCards().addAll(flys);
        flyResult.getResultFlys().addAll(resultFlys);

        return flyResult;
    }

    /**
     * 抓牌
     * 
     * @param game
     * @return
     * @author wcy 2017年7月31日
     */
    private List<Integer> getFlys(Game game) {
        GameConfigData config = game.getGameConfig();
        int catchCount = config.getFlyCount();
        // 没有可以抓的苍蝇直接返回
        if (catchCount == 0)
            return new ArrayList<Integer>();

        List<Integer> flys = new ArrayList<>(catchCount);
        for (int j = 0; j < catchCount; j++) {
            // 没苍蝇就算了
            try {
                int flyCard = game.getRemainCards().remove(0);
                flys.add(flyCard);
            } catch (Exception e) {
                break;
            }
        }

        return flys;
    }

    /**
     * 获得命中的苍蝇
     * 
     * @param game
     * @param flys
     * @return
     * @author wcy 2017年7月31日
     */
    private List<Integer> getTargetFlys(Game game, List<Integer> flys) {
        GameConfigData gameConfigData = game.getGameConfig();

        List<Integer> result = new ArrayList<>(flys.size());

        List<Integer> flyValueList = gameConfigData.getFlyValueList();
        for (Integer i : flys) {
            if (this.isNumSame(flyValueList, i))
                result.add(i);
        }

        return result;
    }

    /**
     * 获得分数
     * 
     * @param flys
     * @param game
     * @return
     * @author wcy 2017年7月31日
     */
    private int getScore(List<Integer> flys, Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        int score = gameConfigData.getFlyScore();
        return flys.size() * score;
    }

    /**
     * 如果数字相同
     * 
     * @param flyValues
     * @param fly
     * @return
     * @author wcy 2017年7月31日
     */
    private boolean isNumSame(List<Integer> flyValues, int fly) {
        return flyValues.contains(fly %= 10);
    }
}
