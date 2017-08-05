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
    /** 明杠分数 */
    public int mingGangScore;
    /** 明杠次数 */
    public int mingGangCount;
    /** 暗杠分数 */
    public int darkGangScore;
    /** 暗杠次数 */
    public int dardGangCount;
    /** 补杠分数 */
    public int addGangScore;
    /** 补杠次数 */
    public int addGangCount;

    @Override
    public String toString() {
        return "GameOverResult [huCount=" + huCount + ", moHuCount=" + moHuCount + ", zhuaHuCount=" + zhuaHuCount
                + ", dianChong=" + dianChong + ", score=" + score + ", mingGangScore=" + mingGangScore
                + ", mingGangCount=" + mingGangCount + ", darkGangScore=" + darkGangScore + ", dardGangCount="
                + dardGangCount + ", addGangScore=" + addGangScore + ", addGangCount=" + addGangCount + "]";
    }

}
