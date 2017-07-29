package com.randioo.mahjong_public_server.module.match;

public class MatchConstant {

    public static final String JOIN_GAME = "match_join_game";
    public static final int HOURS = 4;

    /** 空玩家id的格式 */
    public static final String NULL_GAME_ROLE_ID_FORMAT = "{0}_-1_0";
    /** 游戏玩家id格式 */
    public static final String GAME_ROLE_ID_FORMAT = "{0}_{1}_0";
    /** 机器人玩家的格式 */
    public static final String AI_GAME_ROLE_ID_FORMAT = "{0}_0_{1}";
    /** 创建游戏 */
    public static final String MATCH_CREATE_GAME = "match_create_game";
}
