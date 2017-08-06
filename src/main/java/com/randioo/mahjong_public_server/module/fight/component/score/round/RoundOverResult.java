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
	/** 自摸 */
	public int moScore;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoundOverResult [seat=").append(seat).append(", score=").append(score).append(", overMethod=")
				.append(overMethod).append(", gangKai=").append(gangKai).append(", mingGangScore=")
				.append(mingGangScore).append(", mingGangCount=").append(mingGangCount).append(", darkGangScore=")
				.append(darkGangScore).append(", dardGangCount=").append(dardGangCount).append(", addGangScore=")
				.append(addGangScore).append(", addGangCount=").append(addGangCount).append(", moScore=")
				.append(moScore).append("]");
		return builder.toString();
	}

}
