package com.randioo.mahjong_public_server.module.fight.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.cache.local.RaceCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.AIChooseCallCardListTimeEvent;
import com.randioo.mahjong_public_server.entity.po.AISendCardTimeEvent;
import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.entity.po.Race;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.entity.po.cardlist.Chi;
import com.randioo.mahjong_public_server.entity.po.cardlist.Gang;
import com.randioo.mahjong_public_server.entity.po.cardlist.Hu;
import com.randioo.mahjong_public_server.entity.po.cardlist.Peng;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.module.match.service.MatchServiceImpl;
import com.randioo.mahjong_public_server.protocol.Entity.CallCardListData;
import com.randioo.mahjong_public_server.protocol.Entity.CallHuData;
import com.randioo.mahjong_public_server.protocol.Entity.CardListData;
import com.randioo.mahjong_public_server.protocol.Entity.CardListType;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;
import com.randioo.mahjong_public_server.protocol.Entity.PaiNum;
import com.randioo.mahjong_public_server.protocol.Entity.RoleGameOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoundCardsData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightGangResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightGuoResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightHuResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightPengResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightReadyResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightSendCardResponse;
import com.randioo.mahjong_public_server.protocol.Fight.SCAgreeExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCountdown;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightHu;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList.Builder;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightPointSeat;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightRoundOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightStart;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTouchCard;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.CardTools;
import com.randioo.mahjong_public_server.util.Lists;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.entity.GlobalConfigFunction;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Function;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private EventScheduler eventScheduler;

	private Scanner in = new Scanner(System.in);

	@Override
	public void init() {
		List<Class<? extends CardList>> lists = new ArrayList<>();
		lists.add(Gang.class);
		lists.add(Peng.class);
		lists.add(Chi.class);
		lists.add(Hu.class);

		Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();
		for (Class<? extends CardList> clazz : lists)
			cardLists.put(clazz, ReflectUtils.newInstance(clazz));

		// GameCache.getCheckCardListSequence().add(Hu.class);
		GameCache.getCheckCardListSequence().add(Gang.class);
		GameCache.getCheckCardListSequence().add(Peng.class);

		GameCache.getCheckSelfCardList().add(Hu.class);
		GameCache.getCheckSelfCardList().add(Gang.class);

		GameCache.getCheckGangCardList().add(Hu.class);

		GameCache.getParseCardListToProtoFunctionMap().put(Chi.class, new Function() {
			@Override
			public Object apply(Object... params) {
				return parseChi((Chi) params[0]);
			}
		});

		// 各种转换方法
		GameCache.getParseCardListToProtoFunctionMap().put(Peng.class, new Function() {
			@Override
			public Object apply(Object... params) {
				return parsePeng((Peng) params[0]);
			}
		});
		GameCache.getParseCardListToProtoFunctionMap().put(Gang.class, new Function() {
			@Override
			public Object apply(Object... params) {
				return parseGang((Gang) params[0]);
			}
		});
		GameCache.getParseCardListToProtoFunctionMap().put(Hu.class, new Function() {
			@Override
			public Object apply(Object... params) {
				return parseHu((Hu) params[0]);
			}
		});

		// 添加proto数据结构加入方法
		GameCache.getNoticeChooseCardListFunctionMap().put(Chi.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				CardListData chiData = (CardListData) params[2];
				builder.addCallCardListData(CallCardListData.newBuilder().setCallId(callId).setCardListData(chiData));
				return null;
			}
		});
		GameCache.getNoticeChooseCardListFunctionMap().put(Peng.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				CardListData pengData = (CardListData) params[2];
				builder.addCallCardListData(CallCardListData.newBuilder().setCallId(callId).setCardListData(pengData));
				return null;
			}
		});
		GameCache.getNoticeChooseCardListFunctionMap().put(Gang.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				CardListData gangData = (CardListData) params[2];
				builder.addCallCardListData(CallCardListData.newBuilder().setCallId(callId).setCardListData(gangData));
				return null;
			}
		});
		GameCache.getNoticeChooseCardListFunctionMap().put(Hu.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				RoundCardsData huData = (RoundCardsData) params[2];
				builder.addCallHuData(CallHuData.newBuilder().setHuData(huData).setCallId(callId));
				return null;
			}
		});
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(FightConstant.APPLY_LEAVE)) {
			RoleGameInfo info = (RoleGameInfo) args[1];
			if (info.roleId == 0) {
				agreeExit((int) args[0], info.gameRoleId, true);
			}
		}
		// if (msg.equals(FightConstant.NEXT_GAME_ROLE_SEND_CARD)) {
		// int gameId = (int) args[0];
		// this.checkAutoAI(gameId);
		// }

		if (msg.equals(FightConstant.FIGHT_NOTICE_SEND_CARD)) {
			int gameId = (int) args[0];
			int seat = (int) args[1];
			this.ifAIAutoSendCard(gameId, seat);
		}

		if (msg.equals(FightConstant.FIGHT_GANG_PENG_HU)) {
			int gameId = (int) args[0];
			int seat = (int) args[1];
			SCFightNoticeChooseCardList scFightNoticeChooseCardList = (SCFightNoticeChooseCardList) args[2];
			this.ifAIAutoGangPengHu(gameId, seat, scFightNoticeChooseCardList);
		}
	}

	@Override
	public void initService() {
		this.addObserver(this);
	}

	@Override
	public void readyGame(Role role) {
		logger.info("readyGame" + role.getAccount());
		Game game = getGameById(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightReadyResponse(
									FightReadyResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		// 游戏准备
		// 返回本玩家收到该消息
		SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder())
				.build());

		// 游戏准备
		roleGameInfo.ready = true;
		SC scFightReady = SC.newBuilder()
				.setSCFightReady(SCFightReady.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId))).build();

		synchronized (game) {
			// 通知其他所有玩家，该玩家准备完毕
			this.sendAllSeatSC(game, scFightReady);
			notifyObservers(FightConstant.FIGHT_READY, scFightReady, game.getGameId());
		}

		boolean matchAI = GlobleConfig.Boolean("matchai");
		// 检查是否全部都准备完毕,全部准备完毕
		if (matchAI ? this.checkAllReadyAI(game) : this.checkAllReady(game)) {
			// 开始游戏
			logger.info("startGame " + game.getGameId());
			if (matchAI)
				matchService.fillAI(game);

			this.gameStart(game);
		}
	}

	/**
	 * 检查全部准备完毕
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月31日
	 */
	private boolean checkAllReady(Game game) {
		logger.info("checkAllReady");
		GameConfigData gameConfigData = game.getGameConfig();
		if (game.getRoleIdMap().size() < gameConfigData.getMaxCount())
			return false;

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready)
				return false;
		}
		return true;
	}

	private boolean checkAllReadyAI(Game game) {
		logger.info("checkAllReadyAI");
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready)
				return false;
		}
		return true;
	}

	@Override
	public void gameStart(Game game) {
		logger.info("gameStart=>game:" + game.getGameId());
		GameConfigData gameConfigData = game.getGameConfig();

		game.setGameState(GameState.GAME_START_START);
		// 游戏初始化
		this.gameInit(game);
		// 检查庄家
		this.checkZhuang(game);
		// 发牌
		this.dispatchCard(game);
		// 设置出牌玩家索引
		game.setCurrentRoleIdIndex(game.getZhuangSeat());

		// 设置每个人的座位和卡牌的数量
		SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
		for (int i = 0; i < gameConfigData.getMaxCount(); i++) {
			RoleGameInfo gameRoleInfo = game.getRoleIdMap().get(game.getRoleIdList().get(i));
			scFightStartBuilder.addPaiNum(PaiNum.newBuilder().setSeat(i).setNum(gameRoleInfo.cards.size()));
		}

		scFightStartBuilder.setTimes(game.getMultiple());
		scFightStartBuilder.setRemainCardCount(game.getRemainCards().size());
		scFightStartBuilder.setZhuangSeat(game.getZhuangSeat());
		// 发送给每个玩家
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			// 通知所有人游戏开始，并把自己的牌告诉场上的玩家
			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setSCFightStart(scFightStartBuilder.clone().addAllPai(roleGameInfo.cards)).build());
		}

		// 庄家发一张牌
		this.touchCard(game);

		this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
	}

	/**
	 * 游戏初始化
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	private void gameInit(Game game) {
		GameConfigData config = game.getGameConfig();
		game.setMultiple(1);

		// 卡牌初始化
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			info.isGang = false;
			info.cards.clear();
			info.qiangGang = null;
		}

		// 剩余牌清空
		game.getRemainCards().clear();

		// 清空桌上的牌
		Map<Integer, List<Integer>> desktopCardMap = game.getDesktopCardMap();
		Map<Integer, List<Integer>> sendDesktopCardMap = game.getSendDesktopCardMap();
		for (int i = 0; i < config.getMaxCount(); i++) {
			List<Integer> list = desktopCardMap.get(i);
			List<Integer> sendList = sendDesktopCardMap.get(i);
			if (list == null) {
				list = new ArrayList<>();
				desktopCardMap.put(i, list);
			}
			if (sendList == null) {
				sendList = new ArrayList<>();
				sendDesktopCardMap.put(i, sendList);
			}
			list.clear();
			sendList.clear();
		}

		// 临时列表清空
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

	}

	/**
	 * 检查庄家是否存在，不存在就赋值
	 * 
	 * @param gameId
	 */
	private void checkZhuang(Game game) {
		boolean debug = true;
		int zhuangGameRoleId = game.getZhuangSeat();
		// 如果没有庄家，则随机一个
		if (zhuangGameRoleId == -1) {
			int index = debug ? 0 : RandomUtils.getRandomNum(game.getRoleIdMap().size());
			game.setZhuangSeat(index);
		}

	}

	@Override
	public void dispatchCard(Game game) {
		// 赋值所有牌,然后随机一个个取
		List<Integer> remainCards = game.getRemainCards();
		Lists.fillList(remainCards, CardTools.CARDS);

		if (GlobleConfig.Boolean("dispatch")) {
			this.dispatchCardDebug(game);
		} else {
			this.dispatchCardRandom(game);
		}
		// 每个玩家卡牌排序
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			Collections.sort(roleGameInfo.cards);
			logger.info(roleGameInfo.gameRoleId + "," + roleGameInfo.cards);
		}
	}

	/**
	 * 随机发牌
	 * 
	 * @param gameId
	 */
	private void dispatchCardRandom(Game game) {
		List<Integer> remainCards = game.getRemainCards();
		for (int i = 0; i < 13; i++) {
			// 每个人先发出13张牌
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				int index = RandomUtils.getRandomNum(remainCards.size());
				roleGameInfo.cards.add(remainCards.get(index));
				remainCards.remove(index);
			}
		}
	}

	/**
	 * 指定出牌
	 * 
	 * @param game
	 */
	private void dispatchCardDebug(Game game) {
		List<Integer> remainCards = game.getRemainCards();
		// 杠冲
//		int[][] arrs = { { 11, 11, 16, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23 },
//				{ 12, 13, 36, 37, 38, 37, 38, 39, 23, 24, 25, 22, 22 },
//				{ 26, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
//				{ 25, 27, 27, 37, 18, 21, 26, 29, 27, 28, 33, 34, 39 } };
		// 一炮多响
		// int[][] arrs = { { 11, 11, 11, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23
		// },
		// { 13, 13, 13, 36, 37, 38, 37, 38, 39, 24, 25, 22, 22 },
		// { 26, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 26, 29, 27, 28, 33, 34, 39 } };
		// // gangkai
		// int[][] arrs = { { 11, 11, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23, 23
		// },
		// { 13, 13, 13, 36, 37, 38, 37, 38, 39, 14, 15, 16, 22 },
		// { 22, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 22, 29, 27, 28, 33, 34, 39 } };
		 // hu
		int[][] arrs = { { 11, 11, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23, 23 },
				{ 13, 13, 13, 36, 37, 38, 37, 38, 39, 14, 15, 16, 22 },
				{ 22, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
				{ 25, 27, 27, 37, 18, 21, 22, 29, 27, 28, 33, 34, 39 } };
		// peng
		// int[][] arrs = { { 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 81
		// },
		// { 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 81 },
		// { 22, 25, 25, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };

		// gang
		// int[][] arrs = { { 11, 11, 11, 13, 13, 13, 14, 14, 14, 16, 16, 16, 81
		// },
		// { 12, 12, 12, 12, 13, 22, 14, 15, 15, 15, 15, 16, 81 },
		// { 22, 25, 25, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };
		// int[][] arrs = { { 11, 11, 11, 13, 13, 13, 14, 14, 14, 15, 15, 15, 81
		// },
		// { 14, 21, 25, 25, 26, 29, 29, 31, 33, 36, 38, 81, 38 },
		// { 22, 12, 16, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
		// { 12, 16, 16, 37, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };
		List<Integer> removeList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String gameRoleId = game.getRoleIdList().get(i);
			RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
			roleGameInfo.cards.clear();
			for (int j = 0; j < 13; j++) {
				roleGameInfo.cards.add(arrs[i][j]);
				removeList.add(arrs[i][j]);
			}
		}

		Lists.removeElementByList(remainCards, removeList);

		Collections.sort(remainCards);
	}

	@Override
	public GeneratedMessage exitGame(Role role) {
		Game game = this.getGameById(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightExitGameResponse(
							FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		int seat = game.getRoleIdList().indexOf(gameRoleId);

		GameState gameState = game.getGameState();
		// 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
		if (gameState == GameState.GAME_STATE_PREPARE) {
			// 若是房主，则直接解散
			if (game.getMasterRoleId() == role.getRoleId()) {
				game.setGameState(GameState.GAME_START_END);

				SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scDismiss);
				GameCache.getGameLockStringMap().remove(game.getLockString());
				// 将游戏从缓存池中移除
				GameCache.getGameMap().remove(game.getGameId());
			} else {
				// 该玩家退出
				SC scExit = SC.newBuilder().setSCFightExitGame(SCFightExitGame.newBuilder().setSeat(seat)).build();
				for (RoleGameInfo info : game.getRoleIdMap().values())
					SessionUtils.sc(info.roleId, scExit);
				game.getRoleIdMap().remove(gameRoleId);
			}

		}
		// 如果游戏已经开始,则要申请退出
		else if (gameState == GameState.GAME_START_START) {
			if (game.getOnlineRoleCount() != 0) {
				return SC
						.newBuilder()
						.setFightExitGameResponse(
								FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
						.build();
			}
			SC scApplyExit = SC.newBuilder()
					.setSCFightApplyExitGame(SCFightApplyExitGame.newBuilder().setCountDown(FightConstant.COUNTDOWN))
					.build();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (SessionCache.getSessionById(info.roleId) == null
						|| SessionCache.getSessionById(info.roleId).isConnected()) {
					game.setOnlineRoleCount(game.getOnlineRoleCount() + 1);
				}
				info.agreeLeave = null;
				if (info.roleId != role.getRoleId()) {
					SessionUtils.sc(info.roleId, scApplyExit);
				}
				this.notifyObservers(FightConstant.APPLY_LEAVE, game.getGameId(), info);
			}
			agreeExit(role, true);
		}

		return SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build();
	}

	@Override
	public GeneratedMessage agreeExit(Role role, boolean agree) {
		System.out.println(role.getRoleId() + "" + agree);
		Game game = this.getGameById(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightAgreeExitGameResponse(
							FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		} else {
			String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
			return agreeExit(game.getGameId(), roleInfoStr, agree);
		}
	}

	public GeneratedMessage agreeExit(int gameId, String roleInfoStr, boolean agree) {
		Game game = this.getGameById(gameId);
		RoleGameInfo roleInfo = game.getRoleIdMap().get(roleInfoStr);
		roleInfo.agreeLeave = agree;
		game.getRoleIdMap().put(roleInfoStr, roleInfo);
		int flag = 0;
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.agreeLeave != null && info.agreeLeave == false) {
				game.setOnlineRoleCount(0);
				for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
					SessionUtils.sc(
							roleGameInfo.roleId,
							SC.newBuilder().setSCAgreeExitGame(
									SCAgreeExitGame.newBuilder().setName(
											info.roleId == 0 ? "ROBOT" + info.gameRoleId : RoleCache.getRoleById(
													info.roleId).getName())));
				}
				return SC
						.newBuilder()
						.setFightAgreeExitGameResponse(
								FightAgreeExitGameResponse.newBuilder()
										.setErrorCode(ErrorCode.APPLY_REJECT.getNumber())).build();
			}
			if (info.agreeLeave != null && info.agreeLeave) {
				flag += 1;
			}
		}
		if (flag == game.getOnlineRoleCount()) {
			game.setGameState(GameState.GAME_START_END);
			SC scDismiss = SC.newBuilder().setSCFightGameDismiss(SCFightGameDismiss.newBuilder()).build();
			for (RoleGameInfo info : game.getRoleIdMap().values())
				SessionUtils.sc(info.roleId, scDismiss);

			// 将游戏从缓存池中移除
			GameCache.getGameLockStringMap().remove(game.getLockString());
			GameCache.getGameMap().remove(game.getGameId());
		}
		return SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build();
	}

	@Override
	public void touchCard(Game game) {
		int seat = game.getCurrentRoleIdIndex();
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);

		List<Integer> remainCards = game.getRemainCards();
		if (remainCards.size() > 0) {

			if (GlobleConfig.Boolean("artifical")) {
				final int finalSeat = seat;
				final Game finalGame = game;
				final RoleGameInfo finalRoleGameInfo = roleGameInfo;
				Thread t = new Thread(new Runnable() {
					public void run() {
						input_TouchCard(finalGame, finalRoleGameInfo);
						touchCardProcess2(finalGame, finalSeat, finalRoleGameInfo);
					};

				});
				t.start();
			} else {
				roleGameInfo.newCard = remainCards.remove(0);
				touchCardProcess2(game, seat, roleGameInfo);
			}
		} else {// 牌出完了，则游戏结束
			this.over(game, seat);
		}

	}

	private void touchCardProcess2(Game game, int seat, RoleGameInfo roleGameInfo) {
		// 通知该玩家摸到的是什么牌
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			int touchCard = 0;
			// 如果是玩家自己,则把牌赋值
			if (game.getRoleIdList().indexOf(roleGameInfo.gameRoleId) == seat)
				touchCard = roleGameInfo.newCard;

			SessionUtils.sc(
					info.roleId,
					SC.newBuilder()
							.setSCFightTouchCard(
									SCFightTouchCard.newBuilder().setSeat(seat)
											.setRemainCardCount(game.getRemainCards().size()).setTouchCard(touchCard))
							.build());

		}

		// 清空临时卡牌
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

		// 检查杠胡卡牌
		this.checkMineCallCardList(game, game.getCurrentRoleIdIndex(), roleGameInfo.newCard,
				GameCache.getCheckSelfCardList());

		this.noticeCountDown(game, 10);
		// 检查有没有可以杠胡的牌
		if (game.getCallCardLists().size() > 0) {
			this.sendGangPengHuMsg2Role(game);
		} else {
			// 通知出牌
			this.noticeSendCard(game);
		}
		// 通知转向
		this.noticePointSeat(game, seat);

	}

	private void input_TouchCard(Game game, RoleGameInfo roleGameInfo) {
		List<Integer> remainCards = game.getRemainCards();
		boolean success = false;
		logger.info(game.toString());
		logger.info("gameRoleId:" + roleGameInfo.gameRoleId + " please server touch a card to "
				+ roleGameInfo.gameRoleId + ":1<int remainCard>");
		while (!success) {
			try {
				String command = in.nextLine();
				String[] args = command.split(" ");
				int card = Integer.parseInt(args[0]);

				GET_SUCCESS: {
					for (int i = remainCards.size() - 1; i >= 0; i--) {
						if (remainCards.get(i) == card) {
							success = true;
							remainCards.remove(i);
							break GET_SUCCESS;
						}
					}
					success = false;
				}
				if (success) {
					roleGameInfo.newCard = card;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通知出牌
	 * 
	 * @param gameId
	 * @author wcy 2017年6月16日
	 */
	private void noticeSendCard(Game game) {
		RoleGameInfo roleGameInfo = this.getCurrentRoleGameInfo(game);
		int index = game.getRoleIdList().indexOf(roleGameInfo.gameRoleId);

		this.sendAllSeatSC(game,
				SC.newBuilder().setSCFightNoticeSendCard(SCFightNoticeSendCard.newBuilder().setSeat(index)).build());

		this.notifyObservers(FightConstant.FIGHT_NOTICE_SEND_CARD, game.getGameId(), index);
	}

	private void ifAIAutoSendCard(int gameId, int seat) {
		Game game = this.getGameById(gameId);
		RoleGameInfo nextRoleGameInfo = this.getCurrentRoleGameInfo(game);
		if (nextRoleGameInfo.roleId != 0) {
			return;
		}
		try {
			if (GlobleConfig.Boolean("artifical")) {
				final Game finalGame = game;
				final RoleGameInfo finalNextRoleGameInfo = nextRoleGameInfo;
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						input_SendCard(finalGame, finalNextRoleGameInfo);

					}
				});
				t.start();
			} else {
				AISendCardTimeEvent evt = new AISendCardTimeEvent() {

					@Override
					public void update(TimeEvent timeEvent) {
						Game game = getGameById(gameId);
						RoleGameInfo roleGameInfo = getCurrentRoleGameInfo(game);

						int cardIndex = RandomUtils.getRandomNum(roleGameInfo.cards.size());
						gameRoleIdSendCard(roleGameInfo.cards.get(cardIndex), game, roleGameInfo.gameRoleId, false);

						for (RoleGameInfo info : game.getRoleIdMap().values()) {
							logger.info(info + "");
						}
					}
				};
				evt.setEndTime(TimeUtils.getNowTime() + 1);
				evt.setGameId(game.getGameId());
				eventScheduler.addEvent(evt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void input_SendCard(Game game, RoleGameInfo nextRoleGameInfo) {
		boolean success = false;
		logger.info(game.toString());
		logger.info("gameRoleId:" + nextRoleGameInfo.gameRoleId
				+ " please send a card:1<int card> 2<bool isSendTouchCard>");
		while (!success) {
			try {
				String command = in.nextLine();
				String[] args = command.split(" ");
				String cardStr = args[0];
				int card = Integer.parseInt(cardStr);
				String isSendTouchCardStr = args[1];
				boolean isSendTouchCard = Boolean.parseBoolean(isSendTouchCardStr);
				success = true;
				gameRoleIdSendCard(card, game, nextRoleGameInfo.gameRoleId, isSendTouchCard);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendCard(Role role, int card, boolean isTouchCard) {
		int gameId = role.getGameId();
		Game game = this.getGameById(gameId);
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightSendCardResponse(
									FightSendCardResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
			return;
		}
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		if (roleGameInfo.roleId != role.getRoleId()) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightSendCardResponse(
									FightSendCardResponse.newBuilder()
											.setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())).build());
			return;
		}

		if (!roleGameInfo.cards.contains(card)) {
			if (roleGameInfo.newCard != card) {
				SessionUtils.sc(
						role.getRoleId(),
						SC.newBuilder()
								.setFightSendCardResponse(
										FightSendCardResponse.newBuilder().setErrorCode(
												ErrorCode.FIGHT_MORE_CARD.getNumber())).build());
				return;
			}
		}

		// 发送卡牌
		logger.info("" + SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder())
				.build());

		// 自动出牌解除
		roleGameInfo.auto = 0;

		// 该玩家出牌
		this.gameRoleIdSendCard(card, game, gameRoleId, isTouchCard);

	}

	/**
	 * 卡牌指针移动
	 * 
	 * @param gameId
	 * @author wcy 2017年6月16日
	 */
	private void jumpCardSeat(Game game) {
		game.setCurrentCardSeatIndex(game.getCurrentRoleIdIndex());
	}

	private void sendGangPengHuMsg2Role(Game game) {
		Map<Integer, SCFightNoticeChooseCardList.Builder> map = new HashMap<>();

		List<CallCardList> callCardLists = game.getCallCardLists();
		for (CallCardList callCardList : callCardLists) {
			SCFightNoticeChooseCardList.Builder builder = map.get(callCardList.masterSeat);
			if (builder == null) {
				builder = SCFightNoticeChooseCardList.newBuilder();
				map.put(callCardList.masterSeat, builder);
			}

			CardList cardList = callCardList.cardList;
			Class<? extends CardList> clazz = cardList.getClass();

			Function parseCardListToProtoFunction = GameCache.getParseCardListToProtoFunctionMap().get(clazz);
			Function addProtoFunction = GameCache.getNoticeChooseCardListFunctionMap().get(clazz);

			Object cardListProtoData = parseCardListToProtoFunction.apply(callCardList.cardList);
			addProtoFunction.apply(builder, callCardList.cardListId, cardListProtoData);
		}

		// 发送给对应的人
		for (Map.Entry<Integer, SCFightNoticeChooseCardList.Builder> entrySet : map.entrySet()) {
			int sendSeat = entrySet.getKey();
			SCFightNoticeChooseCardList.Builder builder = entrySet.getValue();

			int roleId = this.getRoleGameInfoBySeat(game, sendSeat).roleId;
			SCFightNoticeChooseCardList scFightNoticeChooseCardList = builder.setTempGameCount(game.getSendCardCount())
					.build();

			SessionUtils
					.sc(roleId, SC.newBuilder().setSCFightNoticeChooseCardList(scFightNoticeChooseCardList).build());

			if (game.getGameState() != GameState.GAME_START_START)
				break;
			this.notifyObservers(FightConstant.FIGHT_GANG_PENG_HU, game.getGameId(), sendSeat,
					scFightNoticeChooseCardList);
		}

	}

	private void ifAIAutoGangPengHu(int gameId, int seat, SCFightNoticeChooseCardList scFightNoticeChooseCardList) {
		Game game = this.getGameById(gameId);
		RoleGameInfo tempRoleGameInfo = this.getRoleGameInfoBySeat(game, seat);
		int gameSendCount = scFightNoticeChooseCardList.getTempGameCount();

		if (tempRoleGameInfo.roleId != 0) {
			return;
		}
		if (GlobleConfig.Boolean("artifical")) {
			final int finalSeat = seat;
			final Game finalGame = game;
			final RoleGameInfo finalRoleGameInfo = tempRoleGameInfo;
			final int finalGameSendCount = gameSendCount;
			// Thread t = new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// input_SendHuGangPengGuo(finalSeat, finalGame, finalRoleGameInfo,
			// finalGameSendCount);
			// }
			//
			// });
			// t.start();
			input_SendHuGangPengGuo(finalSeat, finalGame, finalRoleGameInfo, finalGameSendCount);
		} else {
			// 机器人处理杠碰胡
			AIChooseCallCardListTimeEvent chooseTimeEvent = new AIChooseCallCardListTimeEvent() {

				@Override
				public void update(TimeEvent timeEvent) {
					SCFightNoticeChooseCardList sc = (SCFightNoticeChooseCardList) message;
					Game game = getGameById(gameId);
					guo(game, AISeat, sc.getTempGameCount());
				}
			};
			chooseTimeEvent.setEndTime(TimeUtils.getNowTime() + 1);
			chooseTimeEvent.setMessage(scFightNoticeChooseCardList);
			chooseTimeEvent.setGameId(game.getGameId());
			chooseTimeEvent.setAISeat(seat);
			eventScheduler.addEvent(chooseTimeEvent);
		}
	}

	private void input_SendHuGangPengGuo(int seat, Game game, RoleGameInfo tempRoleGameInfo, int gameSendCount) {
		boolean success = false;
		logger.info(game.toString());
		logger.info("gameRoleId:" + tempRoleGameInfo.gameRoleId
				+ " please choose gang peng guo:1<int callCardListId> 2<string hu,gang,peng,guo>");
		while (!success) {
			try {
				String command = in.nextLine();
				String[] args = command.split(" ");
				int callCardListId = Integer.parseInt(args[0]);
				String choose = args[1];
				switch (choose) {
				case "hu": {
					success = true;
					this.hu(game, seat, gameSendCount, callCardListId);
					break;
				}
				case "gang": {
					success = true;
					this.gang(game, seat, gameSendCount, callCardListId);
					break;
				}
				case "peng": {
					success = true;
					this.peng(game, seat, gameSendCount, callCardListId);
					break;
				}
				case "guo":
					success = true;
					this.guo(game, seat, gameSendCount);
					break;
				}
				logger.info("callCardListId=>" + callCardListId + " chooes=>" + choose);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 移除我方的所有选择
	 * 
	 * @param callCardLists
	 * @param seatIndex
	 * @author wcy 2017年6月17日
	 */
	private void deleteAllCallCardListBySeat(List<CallCardList> callCardLists, int seatIndex) {
		this.deleteCallCardListBySeatBesidesCallCardListId(callCardLists, seatIndex, 0, true);
	}

	/**
	 * 移除自己除了选定的牌型id之外的所有牌
	 * 
	 * @param callCardLists
	 * @param seatIndex
	 * @param callCardListId
	 * @author wcy 2017年6月17日
	 */
	private CallCardList deleteCallCardListBySeatBesidesCallCardListId(List<CallCardList> callCardLists, int seatIndex,
			int callCardListId) {
		return this.deleteCallCardListBySeatBesidesCallCardListId(callCardLists, seatIndex, callCardListId, false);
	}

	/**
	 * 移除自己除了选定的牌型id之外的所有牌
	 * 
	 * @param callCardLists
	 * @param seatIndex
	 * @param callCardListId
	 * @param allDelete 如果为true，则全部删除
	 * @author wcy 2017年6月17日
	 */
	private CallCardList deleteCallCardListBySeatBesidesCallCardListId(List<CallCardList> callCardLists, int seatIndex,
			int callCardListId, boolean allDelete) {
		CallCardList targetCallCardList = null;
		for (int i = callCardLists.size() - 1; i >= 0; i--) {
			CallCardList callCardList = callCardLists.get(i);
			if (callCardList.masterSeat == seatIndex) {
				if (callCardListId != callCardList.cardListId || allDelete) {
					callCardLists.remove(i);
				} else {
					targetCallCardList = callCardList;
				}
			}
		}
		return targetCallCardList;
	}

	@Override
	public void peng(Role role, int gameSendCount, int cardListId) {

		int gameId = role.getGameId();
		Game game = this.getGameById(gameId);

		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.peng(game, seatIndex, gameSendCount, cardListId);
	}

	/**
	 * 碰
	 * 
	 * @param seat
	 * @param gameSendCount
	 * @author wcy 2017年6月17日
	 */
	private void peng(Game game, int seat, int gameSendCount, int callCardListId) {
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
		// 杠标记取消
		roleGameInfo.isGang = false;

		// 出牌数必须相同
		if (game.getSendCardCount() != gameSendCount) {
			SessionUtils.sc(
					roleGameInfo.roleId,
					SC.newBuilder()
							.setFightPengResponse(
									FightPengResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount) {
				SessionUtils.sc(
						roleGameInfo.roleId,
						SC.newBuilder()
								.setFightPengResponse(
										FightPengResponse.newBuilder().setErrorCode(
												ErrorCode.FIGHT_TIME_PASS.getNumber())).build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightPengResponse(FightPengResponse.newBuilder())
					.build());

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seat, callCardListId);

			// 标记为已经叫过了
			callCardList.call = true;

			if (!needWaitOtherCallCardListAction(game.getCallCardLists(), seat)) {

				// 牌归自己
				Peng peng = (Peng) callCardList.cardList;

				roleGameInfo.cards.add(peng.card);

				// 移除手牌
				Lists.removeElementByList(roleGameInfo.cards, peng.getCards());

				// 显示到我方已碰的桌面上
				roleGameInfo.showCardLists.add(peng);

				CardListData pengData = this.parsePeng(peng);

				// 通知其他玩家自己碰
				this.sendAllSeatSC(
						game,
						SC.newBuilder()
								.setSCFightCardList(
										SCFightCardList.newBuilder().setCardListData(pengData).setSeat(seat)).build());

				// 跳转到当前碰的人
				this.jumpToIndex(game, seat);
				// 通知出牌
				this.noticeSendCard(game);
				// 倒计时
				this.noticeCountDown(game, 10);
				// 通知转向
				this.noticePointSeat(game, seat);
			}
		}
	}

	@Override
	public void gang(Role role, int gameSendCount, int callCardListId) {
		int gameId = role.getGameId();
		Game game = this.getGameById(gameId);

		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.gang(game, seatIndex, gameSendCount, callCardListId);
	}

	private void gang(Game game, int seat, int gameSendCount, int callCardListId) {
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
		// 出牌数必须相同
		if (game.getSendCardCount() != gameSendCount) {
			SessionUtils.sc(
					roleGameInfo.roleId,
					SC.newBuilder()
							.setFightGangResponse(
									FightGangResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount) {
				SessionUtils.sc(
						roleGameInfo.roleId,
						SC.newBuilder()
								.setFightGangResponse(
										FightGangResponse.newBuilder().setErrorCode(
												ErrorCode.FIGHT_TIME_PASS.getNumber())).build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightGangResponse(FightGangResponse.newBuilder())
					.build());

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seat, callCardListId);

			// 标记为已经叫过了
			callCardList.call = true;

			if (!needWaitOtherCallCardListAction(game.getCallCardLists(), seat)) {

				// 牌归自己
				Gang gang = (Gang) callCardList.cardList;

				if (gang.peng != null) {
					// 检查抢杠
					if (this.checkQiangGang(game, seat, roleGameInfo, gang))
						return;

					this.addGangSuccess(roleGameInfo, gang);
				} else {
					// 明杠或暗杠
					if (!gang.dark) {
						// 明杠
						roleGameInfo.cards.add(gang.card);
					} else {
						// 暗杠
						// 如果新摸得牌是用于暗杠,则新摸得牌赋值成空，否则新摸的牌加入手牌
						if (roleGameInfo.newCard == gang.card) {
							roleGameInfo.cards.add(gang.card);
							roleGameInfo.newCard = 0;
						} else {
							this.newCardAdd2Cards(roleGameInfo);
						}
					}
					Lists.removeElementByList(roleGameInfo.cards, gang.getCards());
				}

				this.gangProcess2(game, seat, roleGameInfo, gang);

			}
		}
	}

	private void gangProcess2(Game game, int seat, RoleGameInfo roleGameInfo, Gang gang) {
		roleGameInfo.showCardLists.add(gang);
		// 标记杠
		roleGameInfo.isGang = true;

		CardListData gangData = this.parseGang(gang);

		// 通知其他玩家自己杠
		this.sendAllSeatSC(game,
				SC.newBuilder()
						.setSCFightCardList(SCFightCardList.newBuilder().setCardListData(gangData).setSeat(seat))
						.build());
		// 跳转到当前杠的人
		this.jumpToIndex(game, seat);

		// 摸一张牌
		this.touchCard(game);
		// 通知转向
		this.noticePointSeat(game, seat);
	}

	private void addGangSuccess(RoleGameInfo roleGameInfo, Gang gang) {
		// 补杠
		this.removeGangTargetCard(roleGameInfo, gang);
		gang.setTargetSeat(gang.peng.getTargetSeat());
		roleGameInfo.showCardLists.remove(gang.peng);
	}

	/**
	 * 移除杠的
	 * 
	 * @param roleGameInfo
	 * @param gang
	 * @author wcy 2017年6月26日
	 */
	private void removeGangTargetCard(RoleGameInfo roleGameInfo, Gang gang) {
		// 如果新摸得牌用于补杠，则新牌复制成空，否则要把新摸得牌放到手牌中
		if (roleGameInfo.newCard == gang.card)
			roleGameInfo.newCard = 0;
		else {
			this.newCardAdd2Cards(roleGameInfo);
			Lists.removeElementByList(roleGameInfo.cards, Arrays.asList(gang.card));
		}
	}

	/**
	 * 检查抢杠
	 * 
	 * @param game
	 * @param seat
	 * @param roleGameInfo
	 * @param gang
	 * @return
	 * @author wcy 2017年6月26日
	 */
	private boolean checkQiangGang(Game game, int seat, RoleGameInfo roleGameInfo, Gang gang) {
		// 清空
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			if (seat == i)
				continue;
			this.checkOtherCallCardList(game, i, gang.card, GameCache.getCheckGangCardList());
		}
		List<CallCardList> callCardLists = game.getCallCardLists();
		if (callCardLists.size() != 0) {
			roleGameInfo.qiangGang = gang;
			this.sendGangPengHuMsg2Role(game);
			return true;
		}
		return false;
	}

	@Override
	public void hu(Role role, int gameSendCount, int callCardListId) {
		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);

		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.hu(game, seatIndex, gameSendCount, callCardListId);
	}

	private void hu(Game game, int seat, int gameSendCount, int callCardListId) {
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
		// 出牌数必须相同
		if (game.getSendCardCount() != gameSendCount || game.getGameState() != GameState.GAME_START_START) {
			SessionUtils.sc(
					roleGameInfo.roleId,
					SC.newBuilder()
							.setFightHuResponse(
									FightHuResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount || game.getGameState() != GameState.GAME_START_START) {
				SessionUtils.sc(
						roleGameInfo.roleId,
						SC.newBuilder()
								.setFightHuResponse(
										FightHuResponse.newBuilder()
												.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber())).build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightHuResponse(FightHuResponse.newBuilder())
					.build());

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seat, callCardListId);

			// 标记为已经叫过了
			callCardList.call = true;

			// if (!needWaitOtherCallCardListAction(game.getCallCardLists(),
			// seat)) {

			// 通知其他玩家自己胡
			Hu hu = (Hu) callCardList.cardList;
			// 如果前面玩家杠了又胡则为杠开
			hu.gangKai = roleGameInfo.isGang;

			this.accumlateSendCardCount(game);
			// 其他同样可以胡的人都胡
			List<CallCardList> huCallCardLists = game.getHuCallCardLists();
			for (CallCardList huCallCardList : huCallCardLists) {
				Hu everyHu = (Hu) huCallCardList.cardList;
				logger.info(hu.toString());
				int masterSeat = huCallCardList.masterSeat;
				RoundCardsData huData = this.parseHu(everyHu);
				RoleGameInfo huRoleGameInfo = getRoleGameInfoBySeat(game, masterSeat);
				huRoleGameInfo.roundCardsData = huData;
				this.sendAllSeatSC(game,
						SC.newBuilder().setSCFightHu(SCFightHu.newBuilder().setSeat(masterSeat).setHuData(huData))
								.build());
			}

			// 如果胡的牌是抢杠, 杠的人要移除杠
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (info.qiangGang != null) {
					this.removeGangTargetCard(info, info.qiangGang);
				}
			}

			// 通知转向
			this.noticePointSeat(game, seat);
			this.over(game, seat);
		}
		// }
	}

	@Override
	public void guo(Role role, int gameSendCount) {

		int gameId = role.getGameId();
		Game game = getGameById(gameId);
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.guo(game, seatIndex, gameSendCount);
	}

	/**
	 * 
	 * @param gameId
	 * @param seatIndex 发送过的人的座位号
	 * @param gameSendCount 有客户端传送过来进行验证的标记
	 * @author wcy 2017年6月17日
	 */
	private void guo(Game game, int seatIndex, int gameSendCount) {
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seatIndex);
		// 杠标记取消
		roleGameInfo.isGang = false;

		if (game.getSendCardCount() != gameSendCount) {
			SessionUtils.sc(
					roleGameInfo.roleId,
					SC.newBuilder()
							.setFightGuoResponse(
									FightGuoResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}

		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount) {
				SessionUtils.sc(
						roleGameInfo.roleId,
						SC.newBuilder()
								.setFightGuoResponse(
										FightGuoResponse.newBuilder().setErrorCode(
												ErrorCode.FIGHT_TIME_PASS.getNumber())).build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightGuoResponse(FightGuoResponse.newBuilder())
					.build());

			this.deleteAllCallCardListBySeat(game.getCallCardLists(), seatIndex);

			if (!needWaitOtherCallCardListAction(game.getCallCardLists(), seatIndex)) {

				int index = game.getCurrentRoleIdIndex();
				// 如果过的是自己，那要再出一张牌
				if (index == seatIndex) {
					this.noticeSendCard(game);
				} else {
					// 获得之前一个人的牌
					CallCardList preCallCardList = this.getPreviousCallCardList(game);

					if (preCallCardList == null) {
						// 如果没有其他人有选择权,先检查这人当前的人有没有摸牌权力，有则摸牌，并通知，则直接顺序下一个人
						RoleGameInfo currentRoleGameInfo = this.getCurrentRoleGameInfo(game);
						if (currentRoleGameInfo.qiangGang != null) {
							this.addGangSuccess(currentRoleGameInfo, currentRoleGameInfo.qiangGang);
							Gang gang = currentRoleGameInfo.qiangGang;
							currentRoleGameInfo.qiangGang = null;
							this.gangProcess2(game, game.getCurrentRoleIdIndex(), roleGameInfo, gang);
						} else {
							this.nextIndex(game);
							this.touchCard(game);
						}
					} else {
						// 如果做过判断了，则就是这个人的选择了
						if (preCallCardList.call) {
							this.jumpToIndex(game, preCallCardList.masterSeat);
							this.touchCard(game);
						}
					}
				}
			}
		}
	}

	@Override
	public CallCardList getPreviousCallCardList(Game game) {
		List<CallCardList> callCardLists = game.getCallCardLists();
		for (CallCardList callCardList : callCardLists)
			return callCardList;

		return null;
	}

	/**
	 * 检查其他人有没有要叫牌的但是还没有选择,callCardList必须按照胡杠碰吃的顺序排好<br>
	 * 
	 * myseatedIndex = 2 <br>
	 * clazz = Chi.class<br>
	 * 
	 * callCardLists: { <br>
	 * Hu.class seatedIndex = 1<br>
	 * Hu.class seatedIndex = 2<br>
	 * Peng.class seatedIndex = 3<br>
	 * Chi.class seatedIndex = 2<br>
	 * }<br>
	 * 上例表示需要等待别人做出选择<br>
	 * 
	 * @param gameId
	 * @return true表示存在
	 * @author wcy 2017年6月13日
	 */
	private boolean needWaitOtherCallCardListAction(List<CallCardList> callCardLists, int seatedIndex) {
		for (CallCardList callCardList : callCardLists) {
			if (seatedIndex != callCardList.masterSeat) {// 不是自己说明别人优先级比自己高，需要别人进行选择
				return true;
			}
		}

		return false;
	}

	/**
	 * 结束
	 * 
	 * @param game
	 * @param seat
	 * @author wcy 2017年6月22日
	 */
	private void over(Game game, int seat) {
		logger.info("over");
		GameConfigData gameConfigData = game.getGameConfig();
		String endTimeStr = gameConfigData.getEndTime();
		String nowTimeStr = TimeUtils.getHHmmssDateFormat().format(new Date());

		try {
			boolean isPassTime = TimeUtils.compareHHmmss(nowTimeStr, endTimeStr) >= 0;
			game.setGameState(isPassTime ? GameState.GAME_START_END : GameState.GAME_STATE_PREPARE);
			roundOver(game, seat);
			if (isPassTime) {
				gameOver(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 回合结束
	 * 
	 * @param game
	 * @param seat
	 * @author wcy 2017年6月22日
	 */
	private void roundOver(Game game, int seat) {

		SCFightRoundOver.Builder scFightRoundOverBuilder = SCFightRoundOver.newBuilder();
		GameConfigData config = game.getGameConfig();
		int minScore = config.getMinStartScore();

		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
			GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
			RoundCardsData gameCardsData = this.parseRoundCardsData(game, roleGameInfo);
			boolean containsHu = false;
			// 查胡
			for (CallCardList callCardList : game.getHuCallCardLists()) {
				if (callCardList.masterSeat != i)
					continue;

				containsHu = true;
				Hu hu = (Hu) callCardList.cardList;

				OverMethod overMethod = OverMethod.OVER_HU;
				if (hu.isMine)
					overMethod = OverMethod.OVER_MO_HU;

				// 抓苍蝇
				List<Integer> flys = getFlys(game);

				RoleRoundOverInfoData.Builder builder = RoleRoundOverInfoData.newBuilder()
						.setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData).setMinScore(minScore)
						.setGangKai(hu.gangKai).setOverMethod(overMethod).addAllFlyCards(flys);
				scFightRoundOverBuilder.addRoleRoundOverInfoData(builder);
			}

			// 没胡就是输，检查点冲
			if (!containsHu) {
				OverMethod overMethod = OverMethod.OVER_LOSS;
				// 检查是否被点冲
				for (CallCardList huCallCardList : game.getHuCallCardLists()) {
					Hu hu = (Hu) huCallCardList.cardList;
					if (hu.getTargetSeat() == i) {
						// 点冲
						overMethod = OverMethod.OVER_CHONG;
						break;
					}
				}
				scFightRoundOverBuilder.addRoleRoundOverInfoData(RoleRoundOverInfoData.newBuilder()
						.setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData).setOverMethod(overMethod)
						.setMinScore(minScore));
			}
		}

		SCFightRoundOver scFightRoundOver = scFightRoundOverBuilder.build();

		// 所有人发结算通知
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightRoundOver(scFightRoundOver).build());
		// 所有人发准备通知
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build());

		notifyObservers(FightConstant.ROUND_OVER, scFightRoundOver, game);
	}

	private List<Integer> getFlys(Game game) {
		GameConfigData config = game.getGameConfig();
		int catchCount = config.getEndCatchCount();
		if (catchCount == 0)
			return new ArrayList<>();
		List<Integer> flys = new ArrayList<>(catchCount);
		for (int j = 0; j < catchCount; j++) {
			// 没苍蝇就算了
			try {
				int flyCard = game.getRemainCards().remove(0);
				flys.add(flyCard);
			} catch (Exception e) {
				break;
			}
		}
		return flys;
	}

	/**
	 * 游戏结束
	 * 
	 * @param game
	 * @author wcy 2017年6月22日
	 */
	private void gameOver(Game game) {
		SCFightGameOver.Builder fightGameOverBuilder = SCFightGameOver.newBuilder();
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);

			int huCount = 4;
			int moHuCount = 4;
			int zhuaHuCount = 4;
			int dianChong = 4;
			int totalGameScore = 666;
			RoleGameOverInfoData roleGameOverInfoData = RoleGameOverInfoData.newBuilder().setGameRoleData(gameRoleData)
					.setHuCount(huCount).setZhuaHuCount(zhuaHuCount).setMoHuCount(moHuCount)
					.setDianChongCount(dianChong).setGameScore(totalGameScore).build();

			fightGameOverBuilder.addRoleGameOverInfoData(roleGameOverInfoData);
		}

		// 所有人发结算通知
		SCFightGameOver fightGameOver = fightGameOverBuilder.build();

		this.sendAllSeatSC(game, SC.newBuilder().setSCFightGameOver(fightGameOver).build());
		notifyObservers(FightConstant.GAME_OVER, fightGameOver);
	}

	private RoundCardsData parseRoundCardsData(Game game, RoleGameInfo roleGameInfo) {
		List<CardList> cardLists = roleGameInfo.showCardLists;
		RoundCardsData.Builder gameCardsDataBuilder = RoundCardsData
				.newBuilder()
				.setHuCard(
						roleGameInfo.roundCardsData == null ? roleGameInfo.newCard : roleGameInfo.roundCardsData
								.getHuCard()).addAllHandCards(roleGameInfo.cards);
		for (CardList cardList : cardLists) {
			Function function = GameCache.getParseCardListToProtoFunctionMap().get(cardList.getClass());
			CardListData cardListData = (CardListData) function.apply(cardList);
			gameCardsDataBuilder.addCardListData(cardListData);
		}

		return gameCardsDataBuilder.build();
	}

	/**
	 * 某玩家出牌
	 * 
	 * @param card
	 * @param gameId
	 * @param gameRoleId
	 */
	private void gameRoleIdSendCard(int card, Game game, String gameRoleId, boolean isSendTouchCard) {
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		// 设置当前的牌
		List<Integer> sendDesktopCards = game.getSendDesktopCardMap().get(game.getCurrentRoleIdIndex());
		sendDesktopCards.add(card);

		// 从手上减掉牌
		if (isSendTouchCard) {
			roleGameInfo.newCard = 0;
		} else {
			Lists.removeElementByList(roleGameInfo.cards, Arrays.asList(card));
		}

		// 设置当前的出牌
		this.jumpCardSeat(game);

		// 通知所有人,此人出的牌
		this.sendAllSeatSC(
				game,
				SC.newBuilder()
						.setSCFightSendCard(
								SCFightSendCard.newBuilder().setSeat(game.getCurrentRoleIdIndex()).setCard(card)
										.setIsTouchCard(isSendTouchCard)).build());

		// 如果有摸得牌还在要加入到手牌
		if (!isSendTouchCard) {
			this.newCardAdd2Cards(roleGameInfo);
		}

		// 清空临时列表
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

		// 保存场上除了本人的杠碰胡
		for (int index = 0; index < game.getRoleIdList().size(); index++) {
			// 自己不能碰自己
			if (index == game.getCurrentRoleIdIndex())
				continue;

			this.checkOtherCallCardList(game, index, card, GameCache.getCheckCardListSequence());
		}

		// 如果没有可以杠碰胡则通知下一个人，如果有则发送通知并等待反馈
		if (game.getCallCardLists().size() == 0) {
			// 下一个人
			this.nextIndex(game);
			// 摸牌
			this.touchCard(game);

		} else {
			logger.info("sendGangPengHuMsg2Role ");
			this.noticeCountDown(game, 10);
			this.sendGangPengHuMsg2Role(game);
		}

	}

	/**
	 * 新牌加入到手牌
	 * 
	 * @param roleGameInfo
	 * @author wcy 2017年6月19日
	 */
	private void newCardAdd2Cards(RoleGameInfo roleGameInfo) {
		if (roleGameInfo.newCard == 0) {
			return;
		}
		roleGameInfo.cards.add(roleGameInfo.newCard);
		roleGameInfo.newCard = 0;
		Collections.sort(roleGameInfo.cards);
	}

	/**
	 * 发送倒计时
	 * 
	 * @param gameId
	 * @param countdown
	 * @author wcy 2017年6月17日
	 */
	private void noticeCountDown(Game game, int countdown) {
		// 发送倒计时
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightCountdown(SCFightCountdown.newBuilder().setCountdown(10))
				.build());
	}

	/**
	 * 座位指针
	 * 
	 * @param game
	 * @param seat
	 * @author wcy 2017年6月21日
	 */
	private void noticePointSeat(Game game, int seat) {
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightPointSeat(SCFightPointSeat.newBuilder().setSeat(seat))
				.build());
	}

	/**
	 * 检查叫碰杠胡的动作
	 * 
	 * @param game
	 * @param hasGangPengHuSeatedIndex
	 * @param card
	 * @param list 需要获得的牌型
	 * @author wcy 2017年6月14日
	 */
	private void checkMineCallCardList(Game game, int hasGangPengHuSeatedIndex, int card,
			List<Class<? extends CardList>> list) {
		int currentRoleIdSeat = game.getCurrentRoleIdIndex();
		// 获得该卡组的人
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, hasGangPengHuSeatedIndex);

		// 填充卡组
		CardSort cardSort = new CardSort(4);
		List<CardList> cardLists = new ArrayList<>();

		List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
		cards.add(card);

		cardSort.fillCardSort(cards);

		List<CallCardList> callCardLists = game.getCallCardLists();
		List<CallCardList> huCallCardLists = game.getHuCallCardLists();

		for (Class<? extends CardList> clazz : list) {
			CardList templateCardList = GameCache.getCardLists().get(clazz);
			templateCardList.check(cardLists, cardSort, card, roleGameInfo.showCardLists, true);

			for (CardList cardList : cardLists) {
				cardList.setTargetSeat(currentRoleIdSeat);

				CallCardList callCardList = new CallCardList();
				callCardList.cardListId = callCardLists.size() + 1;
				callCardList.masterSeat = hasGangPengHuSeatedIndex;
				callCardList.cardList = cardList;

				callCardLists.add(callCardList);
				// 如果是胡放到另一个数组中
				if (clazz == Hu.class)
					huCallCardLists.add(callCardList);
			}

			cardLists.clear();
		}
	}

	/**
	 * 检查叫碰杠胡的动作
	 * 
	 * @param game
	 * @param hasGangPengHuSeatedIndex
	 * @param card
	 * @param list 需要获得的牌型
	 * @author wcy 2017年6月14日
	 */
	private void checkOtherCallCardList(Game game, int hasGangPengHuSeatedIndex, int card,
			List<Class<? extends CardList>> list) {
		int currentRoleIdSeat = game.getCurrentRoleIdIndex();
		// 获得该卡组的人
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, hasGangPengHuSeatedIndex);

		// 填充卡组
		CardSort cardSort = new CardSort(4);
		List<CardList> cardLists = new ArrayList<>();

		List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
		cards.add(card);

		cardSort.fillCardSort(cards);

		List<CallCardList> callCardLists = game.getCallCardLists();
		List<CallCardList> huCallCardLists = game.getHuCallCardLists();

		for (Class<? extends CardList> clazz : list) {
			CardList templateCardList = GameCache.getCardLists().get(clazz);
			templateCardList.check(cardLists, cardSort, card, roleGameInfo.showCardLists, false);

			for (CardList cardList : cardLists) {
				cardList.setTargetSeat(currentRoleIdSeat);

				CallCardList callCardList = new CallCardList();
				callCardList.cardListId = callCardLists.size() + 1;
				callCardList.masterSeat = hasGangPengHuSeatedIndex;
				callCardList.cardList = cardList;

				callCardLists.add(callCardList);
				// 如果是胡放到另一个数组中
				if (clazz == Hu.class)
					huCallCardLists.add(callCardList);
			}

			cardLists.clear();
		}
	}

	private void sendAllSeatSC(Game game, SC sc) {
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			SessionUtils.sc(roleGameInfo.roleId, sc);
		}
	}

	//
	// private void checkAutoAI(int gameId) {
	// Game game = GameCache.getGameMap().get(gameId);
	// // 发送等待消息
	// RoleGameInfo info = this.getCurrentRoleGameInfo(game);
	// if (info.auto >= 2) {
	// autoSendCard(gameId, info.gameRoleId);
	// return;
	// }
	//
	// SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {
	//
	// @Override
	// public void update(TimeEvent timeEvent) {
	// timeUp((SendCardTimeEvent) timeEvent);
	// }
	// };
	//
	// sendCardTimeEvent.setSendCardCount(game.getSendCardCount());
	// sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() +
	// FightConstant.SEND_CARD_WAIT_TIME);
	// sendCardTimeEvent.setGameId(gameId);
	//
	// eventScheduler.addEvent(sendCardTimeEvent);
	// }
	//
	// private void timeUp(SendCardTimeEvent event) {
	// int gameId = event.getGameId();
	// Game game = GameCache.getGameMap().get(gameId);
	// // 如果出牌数已经改变,或者游戏已经结束,则直接返回
	// if (game.getSendCardCount() != event.getSendCardCount())
	// return;
	//
	// String gameRoleId =
	// game.getRoleIdList().get(game.getCurrentRoleIdIndex());
	//
	// this.autoSendCard(gameId, gameRoleId);
	// RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
	// roleGameInfo.auto++;
	//
	// System.out.println("time up");
	// }

	/**
	 * 自动出牌
	 * 
	 * @param gameId
	 * @param gameRoleId
	 * @author wcy 2017年6月2日
	 */
	private void autoSendCard(int gameId, String gameRoleId) {
		// 否则进行自动出牌
		List<Integer> paiList = this.getAutoPaiList(gameId);
		List<Integer> t = new ArrayList(1);

	}

	/**
	 * 实现自动出牌
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private List<Integer> getAutoPaiList(int gameId) {

		return null;
	}

	/**
	 * 跳转到下一个人
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月14日
	 */
	private void nextIndex(Game game) {
		int index = game.getCurrentRoleIdIndex();
		jumpToIndex(game, (index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
	}

	/**
	 * 跳转到固定的某个人
	 * 
	 * @param gameId
	 * @param seatedIndex
	 * @return
	 * @author wcy 2017年6月14日
	 */
	private void jumpToIndex(Game game, int seatedIndex) {
		game.setCurrentRoleIdIndex(seatedIndex);
		// 出牌次数加1
		this.accumlateSendCardCount(game);
	}

	// 累计出牌数
	private void accumlateSendCardCount(Game game) {
		game.setSendCardCount(game.getSendCardCount() + 1);
	}

	/**
	 * 获得当前玩家的信息
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private RoleGameInfo getCurrentRoleGameInfo(Game game) {
		int index = game.getCurrentRoleIdIndex();
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, index);
		return roleGameInfo;
	}

	private RoleGameInfo getRoleGameInfoBySeat(Game game, int seat) {
		String gameRoleId = game.getRoleIdList().get(seat);
		return game.getRoleIdMap().get(gameRoleId);
	}

	/**
	 * 获得游戏
	 * 
	 * @param gameId
	 * @return
	 */
	private Game getGameById(int gameId) {
		return GameCache.getGameMap().get(gameId);
	}

	public CardListData parseChi(Chi chi) {
		CardListData.Builder chiDataBuilder = CardListData.newBuilder();
		chiDataBuilder.setCard(chi.card);
		chiDataBuilder.setTargetCard(chi.targetCard);
		chiDataBuilder.setTargetSeat(chi.getTargetSeat());
		chiDataBuilder.setCardListType(CardListType.CARD_LIST_TYPE_CHI);

		return chiDataBuilder.build();
	}

	private CardListData parseGang(Gang gang) {
		CardListData.Builder gangDataBuilder = CardListData.newBuilder();
		gangDataBuilder.setCard(gang.card);
		gangDataBuilder.setTargetCard(gang.card);
		gangDataBuilder.setTargetSeat(gang.getTargetSeat());
		gangDataBuilder
				.setCardListType(gang.dark ? CardListType.CARD_LIST_TYPE_GANG_DARK : gang.peng == null ? CardListType.CARD_LIST_TYPE_GANG_LIGHT : CardListType.CARD_LIST_TYPE_GANG_ADD);

		return gangDataBuilder.build();
	}

	private CardListData parsePeng(Peng peng) {
		CardListData.Builder pengDataBuilder = CardListData.newBuilder();
		pengDataBuilder.setCardListType(CardListType.CARD_LIST_TYPE_PENG);
		pengDataBuilder.setTargetSeat(peng.getTargetSeat());
		pengDataBuilder.setCard(peng.card);
		pengDataBuilder.setTargetCard(peng.card);

		return pengDataBuilder.build();
	}

	private RoundCardsData parseHu(Hu hu) {
		RoundCardsData.Builder huDataBuilder = RoundCardsData.newBuilder();
		huDataBuilder.setTargetSeat(hu.getTargetSeat());
		huDataBuilder.setHuCard(hu.card);
		huDataBuilder.setTouchCard(hu.isMine ? hu.card : 0);
		huDataBuilder.addAllHandCards(hu.handCards);
		for (CardList cardList : hu.showCardList) {
			CardListData cardListData = (CardListData) GameCache.getParseCardListToProtoFunctionMap()
					.get(cardList.getClass()).apply(cardList);
			huDataBuilder.addCardListData(cardListData);
		}
		return huDataBuilder.build();
	}

	/*
	 * 排队
	 */
	public void changeRole(int gameId, int roleId) {
		Game game = GameCache.getGameMap().get(gameId);
		Race race = RaceCache.getRaceMap().get(gameId);

		game.getRoleIdMap().remove(gameId + "_" + roleId);
		matchService.joinGame((Role) RoleCache.getRoleById(race.getRoleIdQueue().get(0)), gameId);

		race.getRoleIdQueue().remove(0);
		race.getRoleIdQueue().add(roleId);

	}

	public static void main(String[] args) {

		GlobleConfig.initParam(new GlobalConfigFunction() {

			@Override
			public void init(Map<String, Object> map, List<String> list) {
				String[] params = { "artifical", "dispatch", "racedebug", "matchai" };
				for (String param : params) {
					GlobleConfig.initBooleanValue(param, list);
				}
			}
		});

		GlobleConfig.init("10006", "debug", "artifical", "true", "dispatch", "true", "racedebug", "true", "matchai",
				"true");
		Game game = new Game();
		game.setGameId(1);
		GameCache.getGameMap().put(1, game);
		GameConfigData config = GameConfigData.newBuilder().setEndTime("4:00:00").setMaxCount(4).build();
		game.setGameConfig(config);

		MatchServiceImpl matchService = new MatchServiceImpl();

		RoleGameInfo r1 = new RoleGameInfo();
		RoleGameInfo r2 = new RoleGameInfo();
		RoleGameInfo r3 = new RoleGameInfo();
		RoleGameInfo r4 = new RoleGameInfo();

		r1.gameRoleId = "1_0_0";
		r2.gameRoleId = "1_0_1";
		r3.gameRoleId = "1_0_2";
		r4.gameRoleId = "1_0_3";

		game.getRoleIdMap().put(r1.gameRoleId, r1);
		game.getRoleIdMap().put(r2.gameRoleId, r2);
		game.getRoleIdMap().put(r3.gameRoleId, r3);
		game.getRoleIdMap().put(r4.gameRoleId, r4);

		game.getRoleIdList().add(r1.gameRoleId);
		game.getRoleIdList().add(r2.gameRoleId);
		game.getRoleIdList().add(r3.gameRoleId);
		game.getRoleIdList().add(r4.gameRoleId);

		FightServiceImpl fightService = new FightServiceImpl();
		fightService.init();
		fightService.initService();
		fightService.matchService = matchService;

		fightService.gameStart(game);

		// game.setGameConfig(GameConfigData.newBuilder().setEndTime("19:02:00").build());
		// fightService.over(game, 1);

	}
	// public static void main(String[] args) {
	// FightServiceImpl fightService = new FightServiceImpl();
	// fightService.init();
	// fightService.initService();
	//
	// Game game = new Game();
	//
	// game.setGameConfig(GameConfigData.newBuilder().setEndTime("19:02:00").build());
	// fightService.over(game, 1);
	// }

}
