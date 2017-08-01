package com.randioo.mahjong_public_server.module.fight.component.score.round;

/**
 * 游戏总结果
 * 
 * @author wcy 2017年7月11日
 *
 */
public class GameOverResult extends RoundOverResult {
    /** 胡的次数 */
    public int huCount;
    /** 摸胡次数 */
    public int moHuCount;
    /** 抓胡次数 */
    public int zhuaHuCount;
    /** 点冲次数 */
    public int dianChong;
    /** 分数 */
    public int score;

    @Override
    public String toString() {
        return "GameOverResult [huCount=" + huCount + ", moHuCount=" + moHuCount + ", zhuaHuCount=" + zhuaHuCount
                + ", dianChong=" + dianChong + ", score=" + score + "]";
    }

}
