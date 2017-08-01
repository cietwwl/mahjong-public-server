package com.randioo.mahjong_public_server.module.fight.component.score.round;

import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;

public class RoundOverResult {
    /** 座位号 */
    public int seat;
    /** 分数 */
    public int score;
    /** 结束原因 */
    public OverMethod overMethod;
    /** 是否杠开 */
    public boolean gangKai;

    @Override
    public String toString() {
        return "RoundOverResult [seat=" + seat + ", score=" + score + ", overMethod=" + overMethod + ", gangKai="
                + gangKai + "]";
    }

}
