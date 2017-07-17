package com.randioo.mahjong_public_server.module.match.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.RoleMatchRule;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.module.match.MatchConstant;
import com.randioo.mahjong_public_server.module.video.service.VideoService;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.mahjong_public_server.protocol.Match.MatchCreateGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchCreateGame;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchJoinGame;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.key.Key;
import com.randioo.mahjong_public_server.util.key.KeyStore;
import com.randioo.mahjong_public_server.util.key.RoomKey;
import com.randioo.mahjong_public_server.util.vote.AllVoteExceptApplyerStrategy;
import com.randioo.mahjong_public_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_platform_sdk.utils.StringUtils;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.db.IdClassCreator;
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
	private KeyStore keyStore;

	@Autowired
	private VideoService videoService;

	@Override
	public void init() {
		// 初始化钥匙
		for (int i = 100000; i < 200000; i++) {
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
				game.setGameType(GameType.GAME_TYPE_MATCH);

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
		this.addObserver(videoService);
	}

	@Override
	public void createRoom(Role role, GameConfigData gameConfigData) {
		// 检查配置是否可以创建游戏
		gameConfigData = addPropGameConfigData(gameConfigData);
		if (!this.checkConfig(gameConfigData)) {
			SC sc = SC
					.newBuilder()
					.setMatchCreateGameResponse(
							MatchCreateGameResponse.newBuilder().setErrorCode(ErrorCode.CREATE_FAILED.getNumber()))
					.build();
			SessionUtils.sc(role.getRoleId(), sc);
			return;
		}

		// 加工游戏配置

		// 创建游戏
		Game game = this.createGame(role.getRoleId(), gameConfigData);
		// 标记房间为好友房间
		game.setGameType(GameType.GAME_TYPE_FRIEND);
		loggerinfo("create game =>" + game.getGameId());
		// 获得该玩家的id
		String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());
		// 获得该玩家的游戏数据
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		// 游戏数据转协议游戏数据
		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);

		// 获得房间锁
		String lockString = this.getLockString(game.getLockKey());
		// 创建游戏消息返回
		SC matchCreateGameResponse = SC
				.newBuilder()
				.setMatchCreateGameResponse(
						MatchCreateGameResponse.newBuilder().setId(lockString).setGameRoleData(myGameRoleData)).build();
		SessionUtils.sc(role.getRoleId(), matchCreateGameResponse);

		// 当收到创建房间的主推时就要显示准备按钮
		SessionUtils.sc(
				role.getRoleId(),
				SC.newBuilder()
						.setSCMatchCreateGame(
								SCMatchCreateGame.newBuilder().setLockString(lockString)
										.setGameId(String.valueOf(game.getGameId())).setGameRoleData(myGameRoleData)
										.setRoomType(GameType.GAME_TYPE_FRIEND.getNumber())
										.setRoundNum(gameConfigData.getRoundCount())).build());

	}

	private boolean checkConfig(GameConfigData gameConfigData) {
		// TODO
		boolean check = true;
		check &= gameConfigData.getMaxCount() >= 2;// 检查人数大于2
		check &= !StringUtils.isNullOrEmpty(gameConfigData.getEndTime());// 必须要有结束时间
		return check;
	}

	/**
	 * 为配置表增加属性
	 * 
	 * @param config
	 * @return
	 */
	private GameConfigData addPropGameConfigData(GameConfigData config) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, MatchConstant.hours);
		Date date = calendar.getTime();

		String endTime = TimeUtils.get_HHmmss_DateFormat().format(date);

		config = config.toBuilder().setMaxCount(4).setEndTime(endTime).build();
		return config;
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
		// 通过配置表创建游戏
		Game game = this.createGameByGameConfig(gameConfigData);
		// 将创建房间的人加入到该房间
		this.addAccountRole(game, roleId);
		// 设置房主
		game.setMasterRoleId(roleId);

		return game;
	}

	@Override
	public Game createGameByGameConfig(GameConfigData gameConfigData) {
		Game game = new Game();
		int gameId = idClassCreator.getId(Game.class);
		game.setGameId(gameId);
		game.setGameState(GameState.GAME_STATE_PREPARE);
		game.setFinishRoundCount(0);
		game.setGameConfig(gameConfigData);

		// 获得钥匙
		RoomKey key = (RoomKey) this.getLockKey();
		key.setGameId(gameId);
		game.setLockKey(key);
		String lockString = this.getLockString(key);
		game.getVoteBox().setStrategy(new AllVoteExceptApplyerStrategy() {

			@Override
			public VoteResult waitVote(String joiner) {
				int roleId = Integer.parseInt(joiner.split("_")[1]);
				IoSession session = SessionCache.getSessionById(roleId);
				if (session == null || !session.isConnected()) {
					return VoteResult.PASS;
				}
				return VoteResult.WAIT;
			}
		});

		GameCache.getGameMap().put(gameId, game);
		GameCache.getGameLockStringMap().put(lockString, gameId);

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

		this.addRole(game, roleId, gameRoleId);
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

	/**
	 * 添加玩家的接口,无论是否是人工智能
	 * 
	 * @param game
	 * @param roleId
	 * @param gameRoleId
	 */
	private void addRole(Game game, int roleId, String gameRoleId) {
		// 创建玩家游戏数据
		if (game.getRoleIdMap().containsKey(gameRoleId))
			return;
		RoleGameInfo roleGameInfo = this.createRoleGameInfo(roleId, gameRoleId);
		loggerdebug("addRole" + game.getGameId() + " roleId =" + roleId);
		game.getRoleIdMap().put(gameRoleId, roleGameInfo);
		game.getRoleIdList().add(gameRoleId);

		if (roleId != 0) {
			Role role = loginService.getRoleById(roleId);
			role.setGameId(game.getGameId());
		}
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

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchJoinGameResponse(MatchJoinGameResponse.newBuilder())
				.build());

	}

	@Override
	public void joinGame(Role role, int gameId) {
		loggerdebug("joinGame" + role.getAccount());
		Game game = GameCache.getGameMap().get(gameId);
		loggerdebug("game-->" + game);
		this.addAccountRole(game, role.getRoleId());

		String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		// 先把自己的信息返回给客户端
		GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);
		SC scJoinGame = SC.newBuilder()
				.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData)).build();
		SessionUtils.sc(role.getRoleId(), scJoinGame);

		// 告诉该玩家准备
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder())
				.build());

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (role.getRoleId() == info.roleId)
				continue;

			// 通知自己当前房间里面其他玩家的信息
			GameRoleData gameRoleData = this.parseGameRoleData(info, game);
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(gameRoleData))
							.build());

			// 告诉其他玩家自己进入房间
			SessionUtils.sc(info.roleId, scJoinGame);
			this.notifyObservers(MatchConstant.JOIN_GAME, scJoinGame, gameId, info);
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
			this.parseGameRoleData(info, game);
			GameRoleData AIGameRoleData = GameRoleData.newBuilder().setGameRoleId(info.gameRoleId).setReady(info.ready)
					.setSeat(index).setName("ROBOT" + info.gameRoleId).build();

			SC scJoinGame = SC.newBuilder()
					.setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(AIGameRoleData)).build();
			for (RoleGameInfo roleGameInfo : realRoleGameInfos) {
				SessionUtils.sc(roleGameInfo.roleId, scJoinGame);
			}
		}
	}

	public static void main(String[] args) {
		MatchServiceImpl impl = new MatchServiceImpl();
		Game game = new Game();
		game.setGameId(1);
		GameCache.getGameMap().put(1, game);
		game.setGameConfig(GameConfigData.newBuilder().setMaxCount(4).build());
		impl.fillAI(game);
		System.out.println(game.getRoleIdList());
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
	public void cancelMatch(Role role) {

	}

	@Override
	public void serviceCancelMatch(Role role) {

	}

	@Override
	public GameRoleData parseGameRoleData(RoleGameInfo info, Game game) {
		int index = game.getRoleIdList().indexOf(info.gameRoleId);

		Role role = loginService.getRoleById(info.roleId);

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

}
