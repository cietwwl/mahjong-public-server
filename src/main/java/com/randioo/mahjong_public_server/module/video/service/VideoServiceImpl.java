package com.randioo.mahjong_public_server.module.video.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.dao.VideoDao;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.bo.VideoData;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.mahjong_public_server.module.match.MatchConstant;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.Entity.GameVideoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoundVideoData;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.VideoUtils;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.template.Observer;

@Service("videoService")
public class VideoServiceImpl extends ObserveBaseService implements VideoService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private FightService fightService;

	@Autowired
	private VideoDao videoDao;

	@Autowired
	private GameDB gameDB;

	@Override
	public void initService() {
		fightService.addObserver(this);
	}

	// 所有执行的操作
	private void allRecord(Object... args) {
		SC sc = (SC) args[0];
		Game game = (Game) args[1];
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			List<SC> list = roleGameInfo.getCurrentSCList(game
					.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
			list.add(sc);
			roleGameInfo.roundSCList.add(sc);
		}
	}
    
	// 所有执行的操作
		private void OnlyOneRecord(Object... args) {
			SC sc = (SC) args[0];
			Game game = (Game) args[1];
			RoleGameInfo Info = (RoleGameInfo) args[2];
			Info.roundSCList.add(sc);
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				List<SC> list = roleGameInfo.getCurrentSCList(game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
				list.add(sc);
			}
		}
	@Override
	public void update(Observer observer, String msg, Object... args) {
		//开始游戏
		if (FightConstant.FIGHT_START.equals(msg)) {
			OnlyOneRecord(args);
		}
		//摸牌
		if (FightConstant.FIGHT_TOUCH_CARD.equals(msg)) {
			OnlyOneRecord(args);
		}
		//出牌
		if (FightConstant.FIGHT_SEND_CARD.equals(msg)) {
			allRecord(args);
		}
		//通知出牌
		if (FightConstant.FIGHT_NOTICE_SEND_CARD.equals(msg)) {
			allRecord(args);
		}
		//倒计绿时
		if (FightConstant.FIGHT_COUNT_DOWN.equals(msg)) {
			allRecord(args);
		}
		//座位指绿针
		if (FightConstant.FIGHT_POINT_SEAT.equals(msg)) {
			allRecord(args);
		}
		//投票退出
		if (FightConstant.FIGHT_VOTE_APPLY_EXIT.equals(msg)) {
		}
		//杠
		if (FightConstant.FIGHT_GANG.equals(msg)) {
			allRecord(args);
		}
		//碰
		if (FightConstant.FIGHT_PENG.equals(msg)) {
			allRecord(args);
		}
		//胡
		if (FightConstant.FIGHT_HU.equals(msg)) {
			allRecord(args);
		}
		//过
		if (FightConstant.FIGHT_GUO.equals(msg)) {
		}
		if (FightConstant.FIGHT_READY.equals(msg)) {
			
			SC sc = (SC) args[0];
			Game game = (Game) args[1];
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				List<SC> list = roleGameInfo.getCurrentSCList(game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
				list.add(sc);
				
				roleGameInfo.roundSCList.add(sc);
			}
		}
		if (MatchConstant.JOIN_GAME.equals(msg)) {
			SC sc = (SC) args[0];
			int gameId = (int) args[1];
			RoleGameInfo info = (RoleGameInfo) args[2];
			Game game = fightService.getGameById(gameId);
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				List<SC> list = roleGameInfo.getCurrentSCList(game
						.getFinishRoundCount()); // 此时玩家进入游戏时，认为
				list.add(sc);
				roleGameInfo.roundSCList.add(sc);
			}
		}
		if(FightConstant.ROUND_OVER.equals(msg)) {
			SC sc = (SC) args[0];
			Game game = (Game) args[1];
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				List<SC> list = roleGameInfo.getCurrentSCList(game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
				list.add(sc);
				roleGameInfo.roundSCList.clear();
			}
		}
		
		if (FightConstant.FIGHT_RECORD_SC.equals(msg)) {
		}
         
		if (FightConstant.FIGHT_GAME_OVER.equals(msg)) {
			SC sc = (SC) args[0];
			Game game = (Game) args[1];
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				List<SC> list = roleGameInfo.getCurrentSCList(game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
				list.add(sc);
			}
			this.saveVideo(game);
		}

		if (FightConstant.FIGHT_CANCEL_GAME.equals(msg)) {
			Game game = (Game) args[0];
			this.saveVideo(game);
		}
	}

	@Override
	public GeneratedMessage videoGet(Role role) {

		List<VideoData> videoDataList = videoDao.get(role.getRoleId());

		for (VideoData v : videoDataList) {
			VideoUtils.parseVideoData(v);
		}
		return null;

	}

	@Override
	public GeneratedMessage videoGetById(int id) {
		return null;
	}

	@Override
	public GeneratedMessage videoGetByRound(int id, int round) {
		return null;
	}

	/**
	 * 保存 录像
	 * 
	 * @param game
	 */

	private void saveVideo(Game game) {
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			GameVideoData.Builder gameVideoDataBuilder = GameVideoData.newBuilder();
			for (List<SC> list : info.videoData.getScList()) {
				RoundVideoData.Builder roundVideoData = RoundVideoData.newBuilder();
				for (SC sc : list) {
					roundVideoData.addSc(sc.toByteString());
				}
				gameVideoDataBuilder.addRoundVideoData(roundVideoData);
			}

			GameVideoData gameVideoData = gameVideoDataBuilder.build();
			VideoUtils.toVideoData(info, gameVideoData.toByteArray());

			gameDB.getInsertPool().execute(new EntityRunnable<VideoData>(info.videoData) {

				@Override
				public void run(VideoData entity) {
					videoDao.insert(entity);
				}
			});

		}
	}

}
