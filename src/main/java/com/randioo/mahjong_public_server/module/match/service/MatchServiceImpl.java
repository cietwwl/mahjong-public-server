package com.randioo.mahjong_public_server.module.match.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.dao.RoleDao;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.RoleMatchRule;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.module.match.MatchConstant;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.mahjong_public_server.protocol.Match.MatchAIResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchCreateGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchJoinAIGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchJoinGame;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchMineInfo;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.key.Key;
import com.randioo.mahjong_public_server.util.key.KeyStore;
import com.randioo.mahjong_public_server.util.key.RoomKey;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.lock.CacheLockUtil;
import com.randioo.randioo_server_base.module.match.MatchHandler;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.module.match.MatchRule;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private LoginService loginService;

	@Autowired
	private MatchModelService matchModelService;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private KeyStore keyStore;

	@Override
	public void init() {
		for (int i = 100000; i < 888888; i++) {
			Key key = new RoomKey();
			key.setValue(i);
			keyStore.putKey(key);
		}
	}

	@Override
	public void initService() {
		matchModelService.setMatchHandler(new MatchHandler() {

			@Override
			public void outOfTime(MatchRule matchRule) {
				RoleMatchRule roleMatchRule = (RoleMatchRule) matchRule;
				int roleId = roleMatchRule.getRoleId();

				System.out.println(TimeUtils.getNowTime() + " out of Time");
			}

			@Override
			public void matchSuccess(Map<String, MatchRule> matchMap) {
				List<RoleMatchRule> list = new ArrayList<>(matchMap.size());
				for (MatchRule matchRule : matchMap.values())
					list.add((RoleMatchRule) matchRule);

				Collections.sort(list);
				GameConfigData config = GameConfigData.newBuilder().build();
				Game game = createGame(list.get(0).getRoleId(), config);

				for (MatchRule matchRule : matchMap.values()) {
					RoleMatchRule rule = (RoleMatchRule) matchRule;

					addAccountRole(game, rule.getRoleId());
				}

			}

			@Override
			public boolean checkMatchRule(MatchRule rule1, MatchRule rule2) {
				RoleMatchRule roleRule1 = (RoleMatchRule) rule1;
				RoleMatchRule roleRule2 = (RoleMatchRule) rule2;

				return roleRule1.getMaxCount() == roleRule2.getMaxCount();
			}

			@Override
			public boolean checkArriveMaxCount(MatchRule rule, Map<String, MatchRule> matchRuleMap) {
				RoleMatchRule roleRule = (RoleMatchRule) rule;

				return matchRuleMap.size() == roleRule.getMaxCount();
			}
		});

		matchModelService.initService();
	}

	@Override
	public GeneratedMessage createRoom(Role role, GameConfigData gameConfigData) {
		loggerdebug(role, "createRoom -->start");
		if (!checkConfig(gameConfigData)) {
			return SC
					.newBuilder()
					.setMatchCreateGameResponse(
							MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.CREATE_FAILED.getNumber()))
					.build();
		}
		// try {
		// if(randiooPlatformSdk.getAccountInfo(role.getAccount()).randiooMoney>=gameConfigData.getCardNum()*20){
		// return SC
		// .newBuilder()
		// .setMatchCreateGameResponse(
		// MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.NOT_RANDIOOMONEY_ENOUGH.getNumber()))
		// .build();
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// 没设置 raceType ,gangScore
		// gameConfigData.toBuilder().setMaxCount(4).setEndTime(TimeUtils.getTimeStr(new
		// Date().getTime() +MatchConstant.hours*60*60*1000)).build();
		GameConfigData gameConfigData2 = gameConfigData.toBuilder().setMaxCount(4)
				.setEndTime(TimeUtils.getTimeStr(new Date().getTime() + MatchConstant.hours * 60 * 60 * 1000)).build();

		Game game = this.createGame(role.getRoleId(), gameConfigData2);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);
		loggerdebug(role, "createRoom-->end");
		return SC
				.newBuilder()
				.setMatchCreateGameResponse(
						MatchCreateGameResponse.newBuilder().setId(game.getLockKey().getValue() + "")
								.setGameRoleData(myGameRoleData)).build();
	}

	/**
	 * 创建游戏
	 * 
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月26日
	 */
	@Override
	public Game createGame(int roleId, GameConfigData gameConfigData) {
		Game game = this.createGameByGameConfig(gameConfigData);

		this.addAccountRole(game, roleId);

		return game;
	}

	@Override
	public Game createGameByGameConfig(GameConfigData gameConfigData) {
		Game game = new Game();
		int gameId = idClassCreator.getId(Game.class);
		game.setGameId(gameId);
		game.setGameType(GameType.GAME_TYPE_FRIEND);
		game.setGameState(GameState.GAME_STATE_PREPARE);

		game.setGameConfig(gameConfigData);

		// 获得钥匙
		RoomKey key = (RoomKey) this.getLockKey();
		key.setGameId(gameId);
		game.setLockKey(key);
		
		GameCache.getGameMap().put(gameId, game);
		GameCache.getGameLockStringMap().put(getLockString(key), gameId);

		return game;
	}

	/**
	 * 加入玩家
	 * 
	 * @param game
	 * @param roleId
	 * @author wcy 2017年5月26日
	 */
	private void addAccountRole(Game game, int roleId) {
		String gameRoleId = getGameRoleId(game.getGameId(), roleId);
		loggerdebug("game=>" + game.getGameId() + "=>" + gameRoleId);

		addRole(game, roleId, gameRoleId);
	}

	/**
	 * 加入ai
	 * 
	 * @param game
	 * @author wcy 2017年5月26日
	 */
	private String addAIRole(Game game) {
		String gameRoleId = this.getAIGameRoleId(game.getGameId());

		addRole(game, 0, gameRoleId);
		// 机器人自动准备完毕
		game.getRoleIdMap().get(gameRoleId).ready = true;
		return gameRoleId;
	}

	private void addRole(Game game, int roleId, String gameRoleId) {
		RoleGameInfo roleGameInfo = this.createRoleGameInfo(roleId, gameRoleId);
		// roleGameInfo.seatIndex = game.getRoleIdMap().size();
		loggerdebug("addRole" + game.getGameId() + " roleId =" + roleId);
		if (roleId != 0) {
			Role role = (Role) RoleCache.getRoleById(roleId);
			role.setGameId(game.getGameId());
		}
		game.getRoleIdMap().put(gameRoleId, roleGameInfo);
		game.getRoleIdList().add(gameRoleId);
	}

	/**
	 * 创建用户在游戏中的数据结构
	 * 
	 * @param roleId
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月25日
	 */
	private RoleGameInfo createRoleGameInfo(int roleId, String gameRoleId) {
		RoleGameInfo roleGameInfo = new RoleGameInfo();
		roleGameInfo.roleId = roleId;
		roleGameInfo.gameRoleId = gameRoleId;

		return roleGameInfo;
	}

	@Override
	public Role getRoleFromRoleGameInfo(RoleGameInfo info) {
		int roleId = info.roleId;
		if (roleId == 0) {
			Role role = new Role();

			role.setName("ROBOT" + info.roleId);
			return role;
		}
		return (Role) RoleCache.getRoleById(roleId);
	}

	@Override
	public void joinGame(Role role, String lockString) {
		Integer gameId = GameCache.getGameLockStringMap().get(lockString);
		loggerdebug("gameid:" + gameId + " join game");
		if (gameId == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setMatchJoinGameResponse(
									MatchJoinGameResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_JOIN_ERROR.getNumber())).build());
			return;
		}

		Game game = GameCache.getGameMap().get(gameId);
		loggerdebug("game:" + game);
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setMatchJoinGameResponse(
									MatchJoinGameResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_JOIN_ERROR.getNumber())).build());
			return;
		}
		String targetLock = this.getLockString(game.getLockKey());
		// 如果锁相同则可以进
		if (!targetLock.equals(lockString)) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setMatchJoinGameResponse(
									MatchJoinGameResponse.newBuilder().setErrorCode(
											ErrorCode.MATCH_ERROR_LOCK.getNumber())).build());
			return;
		}

		int maxCount = game.getGameConfig().getMaxCount();
		if (game.getRoleIdMap().size() >= maxCount) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setMatchJoinGameResponse(
									MatchJoinGameResponse.newBuilder().setErrorCode(
											ErrorCode.MATCH_MAX_ROLE_COUNT.getNumber())).build());
			return;
		}

		this.joinGame(role, gameId);

		String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);

		List<GameRoleData> gameRoleDataList = new ArrayList<>(game.getRoleIdMap().size());
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			GameRoleData gameRoleData = this.parseGameRoleData(info, game);
			gameRoleDataList.add(gameRoleData);
		}

		SessionUtils.sc(
				role.getRoleId(),
				SC.newBuilder()
						.setMatchJoinGameResponse(
								MatchJoinGameResponse.newBuilder().addAllGameRoleData(gameRoleDataList)
										.setSeat(myGameRoleData.getSeat())).build());
	}

	@Override
	public void joinGame(Role role, int gameId) {
		loggerdebug("joinGame" + role.getAccount());
		Game game = GameCache.getGameMap().get(gameId);
		loggerdebug("game-->" + game);
		this.addAccountRole(game, role.getRoleId());

		String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);
		SC scJoinGame = SC.newBuilder()
				.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData)).build();
		// 通知其他人加入房间
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (role.getRoleId() == info.roleId)
				continue;
			SessionUtils.sc(info.roleId, scJoinGame);
		}

		notifyObservers(MatchConstant.JOIN_GAME, scJoinGame, gameId);

	}

	@Override
	public void joinAICountGame(Role role, int aiCount) {
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setMatchJoinAIGameResponse(MatchJoinAIGameResponse.newBuilder()).build());

		int gameIdCount = 1;
		boolean joinSuccess = false;
		while (!joinSuccess) {
			Lock lock = CacheLockUtil.getLock(Game.class, gameIdCount);
			try {
				lock.lock();
				Game game = GameCache.getGameMap().get(gameIdCount);
				if (game == null) {
					GameConfigData gameConfigData = GameConfigData.newBuilder().setMaxCount(4).setEndTime("3:24:00")
							.build();
					game = createGame(role.getRoleId(), gameConfigData);
					String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());
					RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
					joinSuccess = true;

					SessionUtils.sc(
							role.getRoleId(),
							SC.newBuilder()
									.setSCMatchMineInfo(
											SCMatchMineInfo.newBuilder().setGameRoleData(
													this.parseGameRoleData(roleGameInfo, game))).build());
					SessionUtils.sc(role.getRoleId(),
							SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build());

					fillAI(aiCount, game);

				} else {
					GameConfigData gameConfigData = game.getGameConfig();
					int gameId = game.getGameId();
					if (game.getRoleIdMap().size() < gameConfigData.getMaxCount() - aiCount) {

						joinGame(role, gameId);
						String myGameRoleId = this.getGameRoleId(gameId, role.getRoleId());
						RoleGameInfo myRoleGameInfo = game.getRoleIdMap().get(myGameRoleId);
						joinSuccess = true;
						SessionUtils.sc(
								role.getRoleId(),
								SC.newBuilder()
										.setSCMatchMineInfo(
												SCMatchMineInfo.newBuilder().setGameRoleData(
														this.parseGameRoleData(myRoleGameInfo, game))).build());

						SessionUtils.sc(role.getRoleId(),
								SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build());

						// 发送给自己别人的信息
						for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
							if (roleGameInfo == myRoleGameInfo)
								continue;

							GameRoleData gameRoleData = this.parseGameRoleData(roleGameInfo, game);
							SessionUtils
									.sc(role.getRoleId(),
											SC.newBuilder()
													.setSCMatchJoinGame(
															SCMatchJoinGame.newBuilder().setGameRoleData(gameRoleData))
													.build());
						}
						// 发送给别人自己的信息
						GameRoleData myGameRoleData = this.parseGameRoleData(myRoleGameInfo, game);
						for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
							if (roleGameInfo.roleId == myRoleGameInfo.roleId)
								continue;
							SessionUtils.sc(
									roleGameInfo.roleId,
									SC.newBuilder()
											.setSCMatchJoinGame(
													SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData))
											.build());
						}

						fillAI(aiCount, game);

					} else {
						gameIdCount++;
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}

		}

	}

	private void fillAI(int aiCount, Game game) {
		GameConfigData gameConfigData = game.getGameConfig();
		// 检查是否可以加入npc
		if (game.getRoleIdMap().size() >= gameConfigData.getMaxCount() - aiCount) {
			// 先检查要发送给多少个真人
			List<RoleGameInfo> realRoleGameInfos = new ArrayList<>(game.getRoleIdMap().values());
			for (int i = game.getRoleIdMap().size(); i < gameConfigData.getMaxCount(); i++) {
				String aiGameRoleId = addAIRole(game);

				RoleGameInfo info = game.getRoleIdMap().get(aiGameRoleId);
				int index = game.getRoleIdList().indexOf(aiGameRoleId);
				parseGameRoleData(info, game);
				GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId)
						.setReady(info.ready).setSeat(index).setName("ROBOT" + info.gameRoleId).build();

				SC scJoinGame = SC.newBuilder()
						.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();
				for (RoleGameInfo roleGameInfo : realRoleGameInfos) {
					SessionUtils.sc(roleGameInfo.roleId, scJoinGame);
				}
			}
		}
	}

	@Override
	public void fillAI(Game game) {
		GameConfigData gameConfigData = game.getGameConfig();
		// 检查是否可以加入npc
		int needAllAICount = gameConfigData.getMaxCount() - game.getRoleIdMap().size();
		// 先检查要发送给多少个真人
		List<RoleGameInfo> realRoleGameInfos = new ArrayList<>(game.getRoleIdMap().values());
		for (int i = 0; i < needAllAICount; i++) {
			String aiGameRoleId = addAIRole(game);

			RoleGameInfo info = game.getRoleIdMap().get(aiGameRoleId);
			int index = game.getRoleIdList().indexOf(aiGameRoleId);
			parseGameRoleData(info, game);
			GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
					.setSeat(index).setName("ROBOT" + info.gameRoleId).build();

			SC scJoinGame = SC.newBuilder()
					.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();
			for (RoleGameInfo roleGameInfo : realRoleGameInfos) {
				SessionUtils.sc(roleGameInfo.roleId, scJoinGame);
			}
		}
	}

	@Override
	public GeneratedMessage match(Role role) {
		RoleMatchRule matchRule = new RoleMatchRule();
		matchRule.setId(idClassCreator.getId(RoleMatchRule.class) + "_" + role.getRoleId());
		matchRule.setWaitTime(60);
		matchRule.setAi(false);
		matchRule.setMatchTime(TimeUtils.getNowTime());
		matchModelService.matchRole(matchRule);
		return null;
	}

	@Override
	public void matchAI(Role role) {
		int roleId = role.getRoleId();
		GameConfigData config = GameConfigData.newBuilder().setMaxCount(4).build();
		Game game = createGame(roleId, config);
		RoleGameInfo tRoleGameInfo = game.getRoleIdMap().get(this.getGameRoleId(game.getGameId(), role.getRoleId()));

		GameRoleData myGameRoleData = this.parseGameRoleData(tRoleGameInfo, game);
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setMatchAIResponse(MatchAIResponse.newBuilder().setGameRoleData(myGameRoleData))
						.build());

		int maxCount = game.getGameConfig().getMaxCount();
		for (int i = game.getRoleIdMap().size(); i < maxCount; i++) {
			String gameRoleId = addAIRole(game);

			RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
			loggerdebug(role, info.toString());
			int index = game.getRoleIdList().indexOf(gameRoleId);
			GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
					.setSeat(index).setName("ROBOT" + info.gameRoleId).build();

			SC scJoinGame = SC.newBuilder()
					.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();
			SessionUtils.sc(role.getRoleId(), scJoinGame);
		}

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder())
				.build());

	}

	@Override
	public GameRoleData parseGameRoleData(RoleGameInfo info, Game game) {
		int index = game.getRoleIdList().indexOf(info.gameRoleId);

		Role role = (Role) RoleCache.getRoleById(info.roleId);
		if (role == null) {
			GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
					.setSeat(index).setName("ROBOT" + info.gameRoleId).build();
			return AIGameRoleData;
		}

		return GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready).setSeat(index)
				.setName(role.getName()).setHeadImgUrl(role.getHeadImgUrl()).setMoney(role.getMoney())
				.setSex(role.getSex()).setPoint(role.getPoint()).build();
	}

	/**
	 * 游戏内使用的玩家id
	 * 
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月24日
	 */
	@Override
	public String getGameRoleId(int gameId, int roleId) {
		return gameId + "_" + roleId + "_0";
	}

	/**
	 * 
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月24日
	 */
	private String getAIGameRoleId(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int aiCount = 0;
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			if (roleGameInfo.roleId == 0) {
				aiCount++;
			}
		}
		return gameId + "_0_" + aiCount;
	}

	private Key getLockKey() {
		return keyStore.getRandomKey();
	}

	@Override
	public String getLockString(Key key) {
		return key.getValue() + "";
	}

	public boolean checkConfig(GameConfigData gameConfigData) {
		// TODO
		return true;
	}

}
