package com.randioo.mahjong_public_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.mahjong_public_server.cache.file.GameRoundConfigCache;

public class GameRoundConfig{
	public static final String urlKey="game_config_need_money.tbl";
	/** 编号 */
	public int id;
	/** 局数 */
	public int round;
	/** 所需花费 */
	public int needMoney;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			GameRoundConfig config = new GameRoundConfig();
			config.id=buffer.getInt();
			config.round=buffer.getInt();
			config.needMoney=buffer.getInt();
			
			GameRoundConfigCache.putConfig(config);
		}
	}
}
