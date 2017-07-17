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
		matchService.addObserver(this);
		// fightService.addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (FightConstant.FIGHT_READY.equals(msg)) {
			SC sc = (SC) args[0];
			int gameId = (int) args[1];
			Game game = fightService.getGameById(gameId);
		}
		if (MatchConstant.JOIN_GAME.equals(msg)) {
			SC sc = (SC) args[0];
			int gameId = (int) args[1];
			RoleGameInfo info = (RoleGameInfo) args[2];
			Game game = fightService.getGameById(gameId);
			List<SC> list = info.getCurrentSCList(game.getFinishRoundCount()); // 此时玩家进入游戏时，认为
																				// finishRound为-1
			list.add(sc);
		}
		if (FightConstant.FIGHT_RECORD_SC.equals(msg)) {
			SC sc = (SC) args[0];
			int gameId = (int) args[1];
			RoleGameInfo info = (RoleGameInfo) args[2];
			Game game = fightService.getGameById(gameId);
			List<SC> list = info.getCurrentSCList(game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
																					// finishRound为-1
			list.add(sc);
		}

		if (FightConstant.FIGHT_GAME_OVER.equals(msg)) {
			Game game = (Game) args[0];
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
