package com.randioo.mahjong_public_server.module.fight.component.fly;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

@Component
public class ZhamaChecker {
    /**
     * 计算苍蝇
     * 
     * @param game
     * @return
     * @author wcy 2017年7月31日
     */
    public ZhamaResult calculateZhamas(Game game) {
        ZhamaResult zhamaResult = new ZhamaResult();
        List<Integer> zhamas = getZhamas(game);
        List<Integer> resultZhamas = getTargetZhamas(game, zhamas);
        int score = this.getScore(resultZhamas, game);

        zhamaResult.setScore(score);
        zhamaResult.getTouchCards().addAll(zhamas);
        zhamaResult.getResultZhamas().addAll(resultZhamas);

        return zhamaResult;
    }

    /**
     * 抓牌
     * 
     * @param game
     * @return
     * @author wcy 2017年7月31日
     */
    private List<Integer> getZhamas(Game game) {
        GameConfigData config = game.getGameConfig();
        int catchCount = config.getZhamaCount();
        // 没有可以抓的苍蝇直接返回
        if (catchCount == 0)
            return new ArrayList<Integer>();

        List<Integer> zhamas = new ArrayList<>(catchCount);
        for (int j = 0; j < catchCount; j++) {
            // 没苍蝇就算了
            try {
                int zhamaCard = game.getRemainCards().remove(0);
                zhamas.add(zhamaCard);
            } catch (Exception e) {
                break;
            }
        }

        return zhamas;
    }

    /**
     * 获得命中的苍蝇
     * 
     * @param game
     * @param zhamas
     * @return
     * @author wcy 2017年7月31日
     */
    private List<Integer> getTargetZhamas(Game game, List<Integer> zhamas) {
        GameConfigData gameConfigData = game.getGameConfig();

        List<Integer> result = new ArrayList<>(zhamas.size());

        List<Integer> zhamaValueList = gameConfigData.getZhamaValueList();
        for (Integer i : zhamas) {
            if (this.isNumSame(zhamaValueList, i))
                result.add(i);
        }

        return result;
    }

    /**
     * 获得分数
     * 
     * @param zhamas
     * @param game
     * @return
     * @author wcy 2017年7月31日
     */
    private int getScore(List<Integer> zhamas, Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        int score = gameConfigData.getZhamaScore();
        return zhamas.size() * score;
    }

    /**
     * 如果数字相同
     * 
     * @param zhamaValues
     * @param zhama
     * @return
     * @author wcy 2017年7月31日
     */
    private boolean isNumSame(List<Integer> zhamaValues, int zhama) {
        return zhamaValues.contains(zhama %= 10);
    }
}
