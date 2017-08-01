package com.randioo.mahjong_public_server.module.fight.component.fly;

import java.util.ArrayList;
import java.util.List;

/**
 * 苍蝇结果集
 * 
 * @author wcy 2017年7月31日
 *
 */
public class FlyResult {
    private List<Integer> touchCards = new ArrayList<>();
    private int score;
    private List<Integer> resultFlys = new ArrayList<>();

    /**
     * 获得摸得牌
     * 
     * @return
     * @author wcy 2017年7月31日
     */
    public List<Integer> getTouchCards() {
        return touchCards;
    }

    /**
     * 获得苍蝇分数
     * 
     * @return
     * @author wcy 2017年7月31日
     */
    public int getScore() {
        return score;
    }

    /**
     * 设置分数
     * 
     * @param score
     * @author wcy 2017年7月31日
     */
    protected void setScore(int score) {
        this.score = score;
    }

    /**
     * 获得摸到的苍蝇
     * 
     * @return
     * @author wcy 2017年7月31日
     */
    public List<Integer> getResultFlys() {
        return resultFlys;
    }
}
