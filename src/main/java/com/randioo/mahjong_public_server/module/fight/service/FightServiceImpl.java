package com.randioo.mahjong_public_server.module.fight.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.GlobleConstant;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.cache.local.RaceCache;
import com.randioo.mahjong_public_server.cache.local.VideoCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.AIChooseCallCardListTimeEvent;
import com.randioo.mahjong_public_server.entity.po.AISendCardTimeEvent;
import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.entity.po.Race;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.module.ServiceConstant;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.fight.component.WaitOtherCallCardListChecker;
import com.randioo.mahjong_public_server.module.fight.component.ZhuangJudger;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.CardList;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Chi;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Gang;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Hu;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.Peng;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.ZLPBaiDaHu;
import com.randioo.mahjong_public_server.module.fight.component.dispatch.CardPart;
import com.randioo.mahjong_public_server.module.fight.component.dispatch.Dispatcher;
import com.randioo.mahjong_public_server.module.fight.component.fly.FlyChecker;
import com.randioo.mahjong_public_server.module.fight.component.fly.FlyResult;
import com.randioo.mahjong_public_server.module.fight.component.score.round.GameOverResult;
import com.randioo.mahjong_public_server.module.fight.component.score.round.RoundOverCalculator;
import com.randioo.mahjong_public_server.module.fight.component.score.round.RoundOverParameter;
import com.randioo.mahjong_public_server.module.fight.component.score.round.RoundOverResult;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.module.role.service.RoleService;
import com.randioo.mahjong_public_server.protocol.Entity.CallCardListData;
import com.randioo.mahjong_public_server.protocol.Entity.CallHuData;
import com.randioo.mahjong_public_server.protocol.Entity.CardListData;
import com.randioo.mahjong_public_server.protocol.Entity.CardListType;
import com.randioo.mahjong_public_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameOverMethod;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;
import com.randioo.mahjong_public_server.protocol.Entity.PaiNum;
import com.randioo.mahjong_public_server.protocol.Entity.RoleGameOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoundCardsData;
import com.randioo.mahjong_public_server.protocol.Entity.ScoreData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightApplyExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightGangResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightGuoResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightHuResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightPengResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightQueryGameConfigResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightReadyResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightSendCardResponse;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitResult;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightChooseCardListOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightClearRoomId;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCountdown;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightDisconnect;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightHu;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList.Builder;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightPointSeat;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightRoundOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightScore;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightStart;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTouchCard;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.CardTools;
import com.randioo.mahjong_public_server.util.Lists;
import com.randioo.mahjong_public_server.util.key.Key;
import com.randioo.mahjong_public_server.util.vote.VoteBox;
import com.randioo.mahjong_public_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.log.HttpLogUtils;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Function;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private EventScheduler eventScheduler;

	@Autowired
	private LoginService loginService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private Dispatcher randomDispatcher;

	@Autowired
	private FlyChecker flyChecker;

	@Autowired
	private RoundOverCalculator roundOverCalculator;

	@Autowired
	private WaitOtherCallCardListChecker waitOtherCallCardListChecker;

	@Autowired
	private ZhuangJudger zhuangJudger;

	private Scanner in = new Scanner(System.in);

	@Override
	public void init() {

		// 确定百搭牌
		GameCache.getBaiDaCardNumSet().add(801);

		List<Class<? extends CardList>> lists = new ArrayList<>();
		lists.add(Gang.class);
		lists.add(Peng.class);
		lists.add(Chi.class);
		lists.add(Hu.class);

		Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();

		cardLists.put(Gang.class, ReflectUtils.newInstance(Gang.class));
		cardLists.put(Peng.class, ReflectUtils.newInstance(Peng.class));
		cardLists.put(Chi.class, ReflectUtils.newInstance(Chi.class));
		cardLists.put(Hu.class, ReflectUtils.newInstance(ZLPBaiDaHu.class));

		GameCache.getCheckCardListSequence().add(Hu.class);
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

		if (msg.equals(FightConstant.FIGHT_NOTICE_SEND_CARD)) {
			Game game = (Game) args[1];
			int seat = (int) args[2];
			this.ifAIAutoSendCard(game.getGameId(), seat);
		}

		if (msg.equals(FightConstant.FIGHT_GANG_PENG_HU)) {
			Game game = (Game) args[0];
			int gameId = game.getGameId();
			int seat = (int) args[1];
			SC sc = (SC) args[2];
			SCFightNoticeChooseCardList scFightNoticeChooseCardList = sc.getSCFightNoticeChooseCardList();
			this.ifAIAutoGangPengHu(gameId, seat, scFightNoticeChooseCardList);
		}

		if (FightConstant.FIGHT_GANG_MING.equals(msg)) {
			Game game = (Game) args[0];
			RoleGameInfo roleGameInfo = (RoleGameInfo) args[1];
			RoleGameInfo targetRoleGameInfo = (RoleGameInfo) args[2];

			this.calcMingGangScore(game, roleGameInfo, targetRoleGameInfo);
		}

		if (FightConstant.FIGHT_GANG_DARK.equals(msg)) {
			Game game = (Game) args[0];
			RoleGameInfo roleGameInfo = (RoleGameInfo) args[1];

			this.calcDarkGangScore(game, roleGameInfo);
		}

		if (FightConstant.FIGHT_GANG_ADD.equals(msg)) {
			Game game = (Game) args[0];
			RoleGameInfo roleGameInfo = (RoleGameInfo) args[1];

			this.calcAddGangScore(game, roleGameInfo);
		}

		if (FightConstant.FIGHT_APPLY_LEAVE.equals(msg)) {
			Game game = (Game) args[0];
			RoleGameInfo info = (RoleGameInfo) args[1];
			SC sc = (SC) args[2];

			// 如果是机器人,则直接设为同意
			if (info.roleId == 0) {
				this.voteApplyExit(game, info.gameRoleId, sc.getSCFightApplyExitGame().getApplyExitId(),
						FightVoteApplyExit.VOTE_AGREE);
			}

		}

	}

	@Override
	public void initService() {
		this.addObserver(this);
	}

	@Override
	public void readyGame(Role role) {
		loggerinfo(role, "readyGame " + role.getAccount());
		Game game = getGameById(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
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
		SessionUtils.sc(roleGameInfo.roleId,
				SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder()).build());

		// 清空上局缓存
		roleGameInfo.roundSCList.clear();

		// 游戏准备
		roleGameInfo.ready = true;
		SC scFightReady = SC.newBuilder()
				.setSCFightReady(SCFightReady.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId))).build();

		synchronized (game) {
			// 通知其他所有玩家，该玩家准备完毕
			this.sendAllSeatSC(game, scFightReady);
			notifyObservers(FightConstant.FIGHT_READY, scFightReady, game);
		}

		boolean matchAI = GlobleMap.Boolean(GlobleConstant.ARGS_MATCH_AI);
		// 检查是否全部都准备完毕,全部准备完毕
		if (matchAI ? this.checkAllReadyAI(game) : this.checkAllReady(game)) {
			loggerinfo("game=>" + game.getGameId() + "=>startGame");
			// 开始游戏
			if (matchAI)
				matchService.fillAI(game);

			this.gameStart(game);
		}
	}

	@Override
	public void queryGameConfig(Role role) {
		int gameId = role.getGameId();
		Game game = this.getGameById(gameId);
		if (game == null) {
			SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightQueryGameConfigResponse(
					FightQueryGameConfigResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build());
		}

		GameConfigData gameConfigData = game.getGameConfig();
		int currentRoundNumber = game.getFinishRoundCount();

		SessionUtils
				.sc(role.getRoleId(),
						SC.newBuilder()
								.setFightQueryGameConfigResponse(FightQueryGameConfigResponse.newBuilder()
										.setGameConfigData(gameConfigData).setCurrentRoundNumber(currentRoundNumber))
								.build());

	}

	/**
	 * 检查全部准备完毕
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月31日
	 */
	private boolean checkAllReady(Game game) {
		loggerinfo("game=>" + game.getGameId() + "=>checkAllReady");
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
		loggerinfo("checkAllReadyAI");
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready)
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.randioo.mahjong_public_server.module.fight.service.FightService#
	 * gameStart (com.randioo.mahjong_public_server.entity.bo.Game)
	 */
	@Override
	public void gameStart(Game game) {
		loggerinfo("gameStart=>game:" + game.getGameId());
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

		// 每个人的分数
		SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();
		// 设置每个人的座位和卡牌的数量
		SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
		for (int i = 0; i < gameConfigData.getMaxCount(); i++) {
			RoleGameInfo gameRoleInfo = game.getRoleIdMap().get(game.getRoleIdList().get(i));
			scFightStartBuilder.addPaiNum(PaiNum.newBuilder().setSeat(i).setNum(gameRoleInfo.cards.size()));

			// 准备一下所有人的分数
			GameOverResult gameOverResult = game.getStatisticResultMap().get(gameRoleInfo.gameRoleId);
			scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setScore(gameOverResult.score).setSeat(i));
		}

		scFightStartBuilder.setTimes(game.getMultiple());
		scFightStartBuilder.setRemainCardCount(game.getRemainCards().size());
		scFightStartBuilder.setZhuangSeat(game.getZhuangSeat());
		scFightStartBuilder.setMaxRound(gameConfigData.getRoundCount());
		scFightStartBuilder.setCurrentRoundNum(game.getFinishRoundCount());

		// 发送给每个玩家
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			SC sc = SC.newBuilder().setSCFightStart(scFightStartBuilder.clone().addAllPai(roleGameInfo.cards)).build();
			// 通知所有人游戏开始，并把自己的牌告诉场上的玩家
			SessionUtils.sc(roleGameInfo.roleId, sc);

			notifyObservers(FightConstant.FIGHT_START, sc, game, roleGameInfo);
		}

		// 通知一下所有人的分数
		SC fightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();
		this.sendAllSeatSC(game, fightScoreSC);
		this.notifyObservers(FightConstant.FIGHT_SCORE, fightScoreSC, game);

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
			// 杠标记附空
			info.isGang = false;
			// 清除手牌
			info.cards.clear();
			// 抢杠清空
			info.qiangGang = null;
			// 听牌清理
			info.tingCards.clear();
			// 清空已经组成的牌组
			info.showCardLists.clear();
			// 新拿的牌清空
			info.newCard = 0;
			// 回合结分重置
			// info.roundOverResult.score = 0;

			// 如果该玩家没有结果集,则创建结果集
			Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
			if (!resultMap.containsKey(info.gameRoleId)) {
				GameOverResult result = this.createRoleGameResult(info);
				resultMap.put(info.gameRoleId, result);
			}
		}

		// 剩余牌清空
		game.getRemainCards().clear();

		// 清空桌上的牌
		Map<Integer, List<Integer>> desktopCardMap = game.getDesktopCardMap();
		Map<Integer, List<Integer>> sendDesktopCardMap = game.getSendDesktopCardMap();
		for (int i = 0; i < config.getMaxCount(); i++) {
			if (!desktopCardMap.containsKey(i)) {
				List<Integer> list = new ArrayList<>();
				desktopCardMap.put(i, list);
			}
			if (!sendDesktopCardMap.containsKey(i)) {
				List<Integer> sendList = new ArrayList<>();
				sendDesktopCardMap.put(i, sendList);
			}

			desktopCardMap.get(i).clear();
			sendDesktopCardMap.get(i).clear();
		}

		// 临时列表清空
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

	}

	/**
	 * 创建游戏结果集
	 * 
	 * @param roleGameInfo
	 * @return
	 * @author wcy 2017年7月12日
	 */
	private GameOverResult createRoleGameResult(RoleGameInfo roleGameInfo) {
		GameOverResult gameOverResult = new GameOverResult();
		return gameOverResult;
	}

	/**
	 * 检查庄家是否存在，不存在就赋值
	 * 
	 * @param gameId
	 */
	private void checkZhuang(Game game) {
		if (GlobleMap.Boolean(GlobleConstant.ARGS_ZHUANG)) {
			game.setZhuangSeat(0);
		} else {
			int zhuangGameRoleId = game.getZhuangSeat();
			// 如果没有庄家，则随机一个
			if (zhuangGameRoleId == -1) {
				int index = RandomUtils.getRandomNum(game.getRoleIdMap().size());
				game.setZhuangSeat(index);
			}
		}

	}

	@Override
	public void dispatchCard(Game game) {
		// 赋值所有牌,然后随机一个个取
		List<Integer> remainCards = game.getRemainCards();
		Lists.fillList(remainCards, CardTools.CARDS);

		if (GlobleMap.Boolean(GlobleConstant.ARGS_DISPATCH)) {
			this.dispatchCardDebug(game);
		} else {
			this.dispatchCardRandom(game);
		}
		// 每个玩家卡牌排序
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			Collections.sort(roleGameInfo.cards);
			loggerinfo("game=>" + game.getGameId() + "=>" + roleGameInfo.gameRoleId + "," + roleGameInfo.cards);
		}
	}

	/**
	 * 随机发牌
	 * 
	 * @param gameId
	 */
	private void dispatchCardRandom(Game game) {
		List<Integer> remainCards = game.getRemainCards();

		List<CardPart> cardParts = randomDispatcher.dispatch(remainCards, game.getRoleIdMap().size(),
				FightConstant.EVERY_INIT_CARD_COUNT);
		int i = 0;
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			CardPart cardPart = cardParts.get(i);
			roleGameInfo.cards.addAll(cardPart);
			i++;
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
		// int[][] arrs = { { 11, 11, 16, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23
		// },
		// { 12, 13, 36, 37, 38, 37, 38, 39, 23, 24, 25, 22, 22 },
		// { 26, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 26, 29, 27, 28, 33, 34, 39 } };
		// 一炮多响
		int[][] arrs = { { 103, 103, 103, 306, 307, 308, 307, 308, 309, 204, 205, 202, 202 },
				{ 203, 207, 207, 307, 108, 201, 206, 209, 207, 208, 303, 304, 309 },
				{ 203, 203, 204, 801, 205, 301, 301, 301, 303, 303, 303, 305, 203 },
				{ 101, 101, 101, 102, 102, 102, 103, 104, 105, 201, 201, 201, 202 } };
		// // gangkai
		// int[][] arrs = { { 11, 11, 12, 12, 12, 13, 14, 15, 21, 21, 21, 23, 23
		// },
		// { 13, 13, 13, 36, 37, 38, 37, 38, 39, 14, 15, 16, 22 },
		// { 22, 25, 25, 17, 18, 26, 29, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 22, 29, 27, 28, 33, 34, 39 } };
		// hu
		// int[][] arrs = { { 101, 101, 102, 102, 102, 103, 104, 105, 201, 201,
		// 201, 203, 203 },
		// { 103, 103, 103, 306, 307, 308, 307, 308, 309, 104, 105, 106, 202,
		// 205 },
		// { 202, 205, 205, 107, 108, 206, 209, 204, 207, 304, 305, 306, 801 },
		// { 103, 207, 207, 307, 108, 201, 202, 209, 207, 208, 303, 304, 309 }
		// };
		// peng
		// int[][] arrs = { { 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 81
		// },
		// { 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 81 },
		// { 22, 25, 25, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
		// { 25, 27, 27, 37, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };

		// gang
		// int[][] arrs = { { 101, 101, 101, 101, 103, 103, 104, 104, 104, 104,
		// 106, 106, 801 },
		// { 102, 102, 102, 102, 103, 202, 103, 105, 105, 105, 105, 106, 801 },
		// { 202, 205, 205, 107, 108, 201, 203, 204, 207, 304, 305, 306, 801 },
		// { 205, 207, 207, 307, 108, 201, 202, 203, 207, 208, 303, 304, 309 }
		// };
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
		// remainCards.clear();
		// remainCards.add(203);

		Collections.sort(remainCards);
	}

	@Override
	public void exitGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build());
			return;
		}

		// // 游戏从来没有开始,则直接退出
		// if (!this.checkGameNeverStart(game)) {
		// SessionUtils.sc(role.getRoleId(),
		// SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build());
		// return;
		// }

		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build());

		// 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
		// 若是房主，则直接解散
		if (game.getMasterRoleId() == role.getRoleId()) {
			// 游戏从来没有开始,则直接退出
			if (this.checkGameNeverStart(game)) {
				// 退还钱款
				roleService.addRandiooMoney(role, game.getGameConfig().getRoundCount() / 3 * 10);
				roleService.initRoleDataFromHttp(role);
			}

			// 标记比赛结束
			game.setGameState(GameState.GAME_START_END);
			VideoCache.getVideoMap().remove(game.getGameId()); // 同时删除视频
			// 通知所有人比赛结束，并把游戏id标记变成0
			SC scClearRoomId = SC.newBuilder().setSCFightClearRoomId(SCFightClearRoomId.newBuilder()).build();
			// 清除房间号
			this.sendAllSeatSC(game, scClearRoomId);

			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				Role tempRole = (Role) RoleCache.getRoleById(info.roleId);
				if (tempRole != null) {
					tempRole.setGameId(0);
				}
				notifyObservers(FightConstant.FIGHT_DISMISS, scClearRoomId, game.getGameId(), info);
			}

			this.destroyGame(game);
		} else {
			String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
			// 该玩家直接退出
			// 清除房间号
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setSCFightClearRoomId(SCFightClearRoomId.newBuilder()).build());

			SC scExit = SC.newBuilder()
					.setSCFightExitGame(SCFightExitGame.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId)))
					.build();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				SessionUtils.sc(info.roleId, scExit);
				this.notifyObservers(FightConstant.FIGHT_GAME_EXIT, info, scExit);
			}
			game.getRoleIdMap().remove(gameRoleId);
			matchService.clearSeatByGameRoleId(game, gameRoleId);

			role.setGameId(0);
		}

	}

	/**
	 * 检查游戏是否从未开始过
	 * 
	 * @param game
	 * @return
	 * @author wcy 2017年6月29日
	 */
	private boolean checkGameNeverStart(Game game) {
		GameState gameState = game.getGameState();
		GameConfigData gameConfig = game.getGameConfig();
		int currentRound = game.getFinishRoundCount();
		int maxRound = gameConfig.getRoundCount();
		return gameState == GameState.GAME_STATE_PREPARE && currentRound == maxRound;
	}

	@Override
	public void agreeExit(Role role, FightVoteApplyExit vote, int voteId) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightAgreeExitGameResponse(
							FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}

		String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build());

		this.voteApplyExit(game, roleInfoStr, voteId, vote);
	}

	/**
	 * 
	 * @param game
	 * @param voteGameRoleId
	 * @param applyExitId
	 * @param vote
	 * @author wcy 2017年7月17日
	 */
	private void voteApplyExit(Game game, String voteGameRoleId, int applyExitId, FightVoteApplyExit vote) {
		VoteBox voteBox = game.getVoteBox();

		synchronized (voteBox) {
			if (voteBox.getVoteId() != applyExitId)
				return;
			if (vote == FightVoteApplyExit.VOTE_AGREE) {
				voteBox.vote(voteGameRoleId, true, applyExitId);
			} else if (vote == FightVoteApplyExit.VOTE_REJECT) {
				voteBox.vote(voteGameRoleId, false, applyExitId);
			}

			VoteResult voteResult = voteBox.getResult();
			System.out.println("voteBox=>" + voteResult);

			if (voteResult == VoteResult.PASS || voteResult == VoteResult.REJECT) {
				SCFightApplyExitResult.Builder builder = SCFightApplyExitResult.newBuilder();
				for (Map.Entry<String, Boolean> entrySet : voteBox.getVoteMap().entrySet()) {
					String key = entrySet.getKey();
					boolean value = entrySet.getValue();
					RoleGameInfo roleGameInfo = game.getRoleIdMap().get(key);
					Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
					String name = role == null ? ServiceConstant.ROBOT_PREFIX_NAME + game.getRoleIdList().indexOf(key)
							: role.getName();

					if (value)
						builder.addAgreeName(name);
					else
						builder.addRejectName(name);

				}

				if (voteResult == VoteResult.PASS) {
					// 游戏结束
					this.cancelGame(game);
				} else if (voteResult == VoteResult.REJECT) {
					RoleGameInfo applyerInfo = game.getRoleIdMap().get(voteBox.getApplyer());
					applyerInfo.lastRejectedExitTime = TimeUtils.getNowTime();
				}

				this.sendAllSeatSC(game, SC.newBuilder().setSCFightApplyExitResult(builder).build());

				voteBox.reset();
			}
		}
	}

	/**
	 * 取消游戏绿帽
	 * 
	 * @param game
	 * @author wcy 2017年6月29日
	 */
	private void cancelGame(Game game) {
		if (!game.isSaveData()) {
			Role masterRole = (Role) RoleCache.getRoleById(game.getMasterRoleId());
			roleService.addRandiooMoney(masterRole, game.getRandiooMoney());
		}

		if (game.getGameState() != GameState.GAME_START_END) {
			synchronized (game) {
				if (game.getGameState() == GameState.GAME_START_END)
					return;

				game.setGameState(GameState.GAME_START_END);

				this.roundOver2(game, false);
				this.gameOver(game);
			}
		}

		this.notifyObservers(FightConstant.FIGHT_CANCEL_GAME, game);

	}

	@Override
	public void applyExitGame(Role role) {
		Game game = this.getGameById(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse.newBuilder()).build());
			return;
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

		// 1.距离上次拒绝时间到现在的间隔时间内不能连续发起申请退出
		// 2.有人在申请退出时不能发布自己的申请退出
		int deltaTime = 30;
		int nowTime = TimeUtils.getNowTime();

		// 是否允许申请退出
		try {
			if (!isAllowApplyExit(nowTime, game, gameRoleId, deltaTime)) {
				SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightApplyExitGameResponse(
						FightApplyExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
						.build());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse.newBuilder()).build());

		VoteBox voteBox = game.getVoteBox();
		// 投票箱重置
		voteBox.reset();
		// 如果投票人是空的,则加入参与投票的人绿帽
		if (voteBox.getJoinVoteSet().size() == 0)
			voteBox.getJoinVoteSet().addAll(game.getRoleIdMap().keySet());

		// 设置申请退出的玩家id
		int voteId = voteBox.applyVote(gameRoleId);

		SC scApplyExit = SC.newBuilder().setSCFightApplyExitGame(SCFightApplyExitGame.newBuilder()
				.setName(role.getName()).setApplyExitId(voteId).setCountDown(FightConstant.COUNTDOWN)).build();

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			// 申请人跳过
			if (info.roleId == role.getRoleId())
				continue;

			SessionUtils.sc(info.roleId, scApplyExit);
			// 发送玩家申请退出的通知
			notifyObservers(FightConstant.FIGHT_APPLY_LEAVE, game, info, scApplyExit);
		}

		// 检查投票是否所有人都不在线
		if (this.checkVoteExitAllOffline(game, role.getRoleId())) {
			cancelGame(game);
		}
	}

	/**
	 * 检查退出所有人都不在线
	 * 
	 * @return
	 * @author wcy 2017年7月24日
	 */
	private boolean checkVoteExitAllOffline(Game game, int applyRoleId) {
		int agreeExitCount = 0;
		for (RoleGameInfo roleInfo : game.getRoleIdMap().values()) {
			// 如果是机器人
			if (roleInfo.roleId == 0) {
				agreeExitCount++;
				continue;
			}
			// 不是申请人
			if (roleInfo.roleId != applyRoleId) {
				IoSession session = SessionCache.getSessionById(roleInfo.roleId);
				String gameRoleId = matchService.getGameRoleId(game.getGameId(), roleInfo.roleId);
				VoteBox voteBox = game.getVoteBox();
				// 如果这个人没有连接并且还没有投票,则算是掉线
				Boolean result = voteBox.getVoteMap().get(gameRoleId);
				if (!session.isConnected() && result == null) {
					agreeExitCount++;
				}
			}
		}

		return agreeExitCount >= game.getRoleIdMap().size() - 1;
	}

	private boolean isAllowApplyExit(int nowTime, Game game, String applyExitRoleGameId, int deltaTime) {
		VoteBox voteBox = game.getVoteBox();
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(applyExitRoleGameId);
		int lastRejectExitTime = roleGameInfo.lastRejectedExitTime;
		Role role = loginService.getRoleById(roleGameInfo.roleId);
		// 有人在申请退出时，不能让另一个人申请退出
		// 现在的时间与上次被拒绝的时间差不能小于规定间隔绿帽

		if (game.getGameType() == GameType.GAME_TYPE_FRIEND) {
			return true;
		}
		loggerinfo(role, "applyExitGameRoleId->" + voteBox.getApplyer());
		if (StringUtils.isNullOrEmpty(voteBox.getApplyer())) {
			loggerinfo(role,
					"nowTime - lastRejectExitTime <= deltaTime" + nowTime + " " + lastRejectExitTime + " " + deltaTime);
			if (nowTime - lastRejectExitTime <= deltaTime) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public void touchCard(Game game) {
		int seat = game.getCurrentRoleIdIndex();
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);

		List<Integer> remainCards = game.getRemainCards();
		if (remainCards.size() > 0) {

			if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
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
				roleGameInfo.newCard = remainCards.remove(RandomUtils.getRandomNum(remainCards.size()));
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
			if (info.gameRoleId.equals(roleGameInfo.gameRoleId))
				touchCard = roleGameInfo.newCard;

			// FIXME
			SC sc = SC.newBuilder().setSCFightTouchCard(SCFightTouchCard.newBuilder().setSeat(seat)
					.setRemainCardCount(game.getRemainCards().size()).setTouchCard(touchCard)).build();

			SessionUtils.sc(info.roleId, sc);

			notifyObservers(FightConstant.FIGHT_TOUCH_CARD, sc, game, info);
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
		loggerinfo(game.toString());
		loggerinfo("gameId=>" + game.getGameId() + "=>gameRoleId:" + roleGameInfo.gameRoleId
				+ " please server touch a card to " + roleGameInfo.gameRoleId + ":1<int remainCard>");
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

		SC noticeSendCard = SC.newBuilder().setSCFightNoticeSendCard(SCFightNoticeSendCard.newBuilder().setSeat(index))
				.build();
		this.sendAllSeatSC(game, noticeSendCard);
		this.notifyObservers(FightConstant.FIGHT_NOTICE_SEND_CARD, noticeSendCard, game, index);
	}

	private void ifAIAutoSendCard(int gameId, int seat) {
		Game game = this.getGameById(gameId);
		RoleGameInfo nextRoleGameInfo = this.getCurrentRoleGameInfo(game);
		if (nextRoleGameInfo.roleId != 0) {
			return;
		}
		try {
			if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
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
						if (game == null)
							return;
						RoleGameInfo roleGameInfo = getCurrentRoleGameInfo(game);

						int cardIndex = RandomUtils.getRandomNum(roleGameInfo.cards.size());
						gameRoleIdSendCard(roleGameInfo.cards.get(cardIndex), game, roleGameInfo.gameRoleId, false);

						for (RoleGameInfo info : game.getRoleIdMap().values()) {
							loggerinfo("gameId=>" + game.getGameId() + "=>" + info);
						}
					}
				};
				evt.setEndTime(TimeUtils.getNowTime() + 1);
				evt.setGameId(game.getGameId());
				eventScheduler.addEvent(evt);
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	private void input_SendCard(Game game, RoleGameInfo nextRoleGameInfo) {
		boolean success = false;
		loggerinfo(game.toString());
		loggerinfo("gameRoleId:" + nextRoleGameInfo.gameRoleId
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
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightSendCardResponse(
							FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		if (roleGameInfo.roleId != role.getRoleId()) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightSendCardResponse(
							FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
							.build());
			return;
		}

		if (!roleGameInfo.cards.contains(card)) {
			if (roleGameInfo.newCard != card) {
				SessionUtils.sc(role.getRoleId(),
						SC.newBuilder().setFightSendCardResponse(
								FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_MORE_CARD.getNumber()))
								.build());
				return;
			}
		}

		// 发送卡牌
		loggerinfo(role, SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());

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
			if (cardList instanceof Hu)
				clazz = Hu.class;

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

			SC sc = SC.newBuilder().setSCFightNoticeChooseCardList(scFightNoticeChooseCardList).build(); // PengGangHu
																											// SC
			SessionUtils.sc(roleId, sc);

			if (game.getGameState() != GameState.GAME_START_START)
				break;
			this.notifyObservers(FightConstant.FIGHT_GANG_PENG_HU, game, sendSeat, sc,
					getRoleGameInfoBySeat(game, sendSeat));
		}

	}

	/**
	 * 取消选择杠碰吃胡
	 * 
	 * @param roleGameInfo
	 * @author wcy 2017年8月2日
	 */
	private void sendChooseCardListOver(Game game, RoleGameInfo roleGameInfo) {
		SC sc = SC.newBuilder().setSCFightChooseCardListOver(SCFightChooseCardListOver.newBuilder()).build();
		SessionUtils.sc(roleGameInfo.roleId, sc);
		this.notifyObservers(FightConstant.FIGHT_GANG_PENG_HU_OVER, sc, game, roleGameInfo);
	}

	/**
	 * 通知所有人都要消除杠碰吃胡
	 * 
	 * @param game
	 * @author wcy 2017年8月2日
	 */
	private void sendAllChooseCardListOver(Game game) {
		Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();
		for (RoleGameInfo roleGameInfo : roleGameInfoMap.values()) {
			this.sendChooseCardListOver(game, roleGameInfo);
		}
	}

	private void ifAIAutoGangPengHu(int gameId, int seat, SCFightNoticeChooseCardList scFightNoticeChooseCardList) {
		Game game = this.getGameById(gameId);
		RoleGameInfo tempRoleGameInfo = this.getRoleGameInfoBySeat(game, seat);
		int gameSendCount = scFightNoticeChooseCardList.getTempGameCount();

		if (tempRoleGameInfo.roleId != 0) {
			return;
		}
		if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
			final int finalSeat = seat;
			final Game finalGame = game;
			final RoleGameInfo finalRoleGameInfo = tempRoleGameInfo;
			final int finalGameSendCount = gameSendCount;

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
		loggerinfo(game.toString());
		loggerinfo("gameRoleId:" + tempRoleGameInfo.gameRoleId
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
				loggerinfo("callCardListId=>" + callCardListId + " chooes=>" + choose);
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
	 * @param allDelete
	 *            如果为true，则全部删除
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
			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder()
							.setFightPengResponse(
									FightPengResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount) {
				SessionUtils.sc(roleGameInfo.roleId,
						SC.newBuilder().setFightPengResponse(
								FightPengResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
								.build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setFightPengResponse(FightPengResponse.newBuilder()).build());

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seat, callCardListId);
			this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seat);

			// 标记为已经叫过了
			callCardList.call = true;

			// 取消选择杠碰吃胡
			this.sendChooseCardListOver(game, roleGameInfo);

			// if (!needWaitOtherCallCardListAction(game.getCallCardLists(),
			// seat)) {
			if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
				return;
			}

			// 所有人取消
			this.sendAllChooseCardListOver(game);

			// 检查是否可以胡,别人可以胡就不算杠
			CallCardList prevCallCardList = this.getPreviousCallCardList(game.getCallCardLists());
			if (prevCallCardList.cardList instanceof Hu) {
				this.huProgress(game, seat, roleGameInfo, prevCallCardList);
				return;
			}

			// 牌归自己
			Peng peng = (Peng) callCardList.cardList;

			roleGameInfo.cards.add(peng.card);

			// 移除手牌
			Lists.removeElementByList(roleGameInfo.cards, peng.getCards());

			// 显示到我方已碰的桌面上
			roleGameInfo.showCardLists.add(peng);

			CardListData pengData = this.parsePeng(peng);

			SC sc = SC.newBuilder()
					.setSCFightCardList(SCFightCardList.newBuilder().setCardListData(pengData).setSeat(seat)).build();
			// 通知其他玩家自己碰
			this.sendAllSeatSC(game, sc);
			// 发送通知
			this.notifyObservers(FightConstant.FIGHT_PENG, sc, game);

			// 跳转到当前碰的人
			this.jumpToIndex(game, seat);
			// 通知出牌
			this.noticeSendCard(game);
			// 倒计时
			this.noticeCountDown(game, 10);
			// 通知转向
			this.noticePointSeat(game, seat);
		}
		// }
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
			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder()
							.setFightGangResponse(
									FightGangResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount) {
				SessionUtils.sc(roleGameInfo.roleId,
						SC.newBuilder().setFightGangResponse(
								FightGangResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
								.build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setFightGangResponse(FightGangResponse.newBuilder()).build());

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seat, callCardListId);
			this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seat);

			// 标记为已经叫过了
			callCardList.call = true;

			this.sendChooseCardListOver(game, roleGameInfo);

			// if (needWaitOtherCallCardListAction(game.getCallCardLists(),
			// seat)) {
			// return;
			// }

			if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
				return;
			}

			this.sendAllChooseCardListOver(game);

			// 检查是否可以胡,别人可以胡就不算杠，这种情况是，自己可杠可胡，好死不死的选了个杠，结果就让别人胡了
			CallCardList prevCallCardList = this.getPreviousCallCardList(game.getCallCardLists());
			if (prevCallCardList.cardList instanceof Hu) {
				this.huProgress(game, seat, roleGameInfo, prevCallCardList);
				return;
			}

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

			this.accumlateSendCardCount(game);
			this.gangProcess2(game, seat, roleGameInfo, gang);
		}

	}

	private void gangProcess2(Game game, int seat, RoleGameInfo roleGameInfo, Gang gang) {
		roleGameInfo.showCardLists.add(gang);
		// 标记杠
		roleGameInfo.isGang = true;

		CardListData gangData = this.parseGang(gang);
		CardListType cardListType = gangData.getCardListType();
		// 明杠
		if (cardListType == CardListType.CARD_LIST_TYPE_GANG_LIGHT) {
			int targetSeat = gang.getTargetSeat();
			RoleGameInfo targetRoleGameInfo = this.getRoleGameInfoBySeat(game, targetSeat);

			this.notifyObservers(FightConstant.FIGHT_GANG_MING, game, roleGameInfo, targetRoleGameInfo);
		}
		// 补杠
		if (cardListType == CardListType.CARD_LIST_TYPE_GANG_ADD) {
			this.notifyObservers(FightConstant.FIGHT_GANG_ADD, game, roleGameInfo);
		}
		// 暗杠
		if (cardListType == CardListType.CARD_LIST_TYPE_GANG_DARK) {
			this.notifyObservers(FightConstant.FIGHT_GANG_DARK, game, roleGameInfo);
		}

		SC sc = SC.newBuilder().setSCFightCardList(SCFightCardList.newBuilder().setCardListData(gangData).setSeat(seat))
				.build();
		// 通知其他玩家自己杠
		this.sendAllSeatSC(game, sc);

		// 发送分数变化
		SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();
		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			RoleGameInfo info = this.getRoleGameInfoBySeat(game, i);
			int score = info.roundOverResult.score;
			scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setScore(score).setSeat(i));
		}
		SC scFightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();
		// 通知所有玩家
		this.sendAllSeatSC(game, scFightScoreSC);

		this.notifyObservers(FightConstant.FIGHT_GANG, sc, game);
		this.notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);

		// 跳转到当前杠的人
		this.jumpToIndex(game, seat);

		// 摸一张牌
		this.touchCard(game);
		// 通知转向
		this.noticePointSeat(game, seat);
	}

	/**
	 * 计算明牌分数
	 * 
	 * @param game
	 * @param roleGameInfo
	 * @param targetRoleGameInfo
	 * @author wcy 2017年7月12日
	 */
	private void calcMingGangScore(Game game, RoleGameInfo roleGameInfo, RoleGameInfo targetRoleGameInfo) {
		/** 猪卵泡突然发现别人的麻将杠分都是用游戏底分,觉得自己很不合群,所以让我改了 */
		int gangScore = game.getGameConfig().getMinStartScore();
		// int gangScore = game.getGameConfig().getGangScore();
		int score = gangScore * 3;

		roleGameInfo.roundOverResult.score += score;
		roleGameInfo.roundOverResult.mingGangScore += score;

		targetRoleGameInfo.roundOverResult.score -= score;

	}

	/**
	 * 计算补杠分数
	 * 
	 * @param game
	 * @param roleGameInfo
	 * @author wcy 2017年7月12日
	 */
	private void calcAddGangScore(Game game, RoleGameInfo roleGameInfo) {
		/** 猪卵泡突然发现别人的麻将杠分都是用游戏底分,觉得自己很不合群,所以让我改了 */
		int gangScore = game.getGameConfig().getMinStartScore();
		// int gangScore = game.getGameConfig().getGangScore();
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.gameRoleId.equals(roleGameInfo.gameRoleId)) {
				int addGangScore = gangScore * 3;
				info.roundOverResult.score += addGangScore;
				info.roundOverResult.addGangScore += addGangScore;
			} else {
				info.roundOverResult.score += -gangScore;
			}
		}
	}

	/**
	 * 计算暗杠分数
	 * 
	 * @param game
	 * @param roleGameInfo
	 * @author wcy 2017年7月12日
	 */
	private void calcDarkGangScore(Game game, RoleGameInfo roleGameInfo) {
		/** 猪卵泡突然发现别人的麻将杠分都是用游戏底分,觉得自己很不合群,所以让我改了 */
		int gangScore = game.getGameConfig().getMinStartScore();
		// int gangScore = game.getGameConfig().getGangScore();
		// TODO 从配置表中获取是否暗杠翻倍
		boolean darkGangDouble = true;
		gangScore = darkGangDouble ? gangScore * 2 : gangScore;

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.gameRoleId.equals(roleGameInfo.gameRoleId)) {
				int mingAddScore = gangScore * 6;
				info.roundOverResult.score += mingAddScore;
				info.roundOverResult.darkGangScore += mingAddScore;
			} else {
				info.roundOverResult.score += -gangScore * 2;
			}
		}
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
	 * 检查抢杠(杠冲)
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
			List<CallCardList> huCallCardList = game.getHuCallCardLists();
			for (CallCardList callCardList : huCallCardList) {
				Hu hu = (Hu) callCardList.cardList;
				hu.gangChong = true;
				hu.gangChongTargetSeat = seat;
			}
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
			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder()
							.setFightHuResponse(
									FightHuResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount || game.getGameState() != GameState.GAME_START_START) {
				SessionUtils.sc(roleGameInfo.roleId,
						SC.newBuilder().setFightHuResponse(
								FightHuResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
								.build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setFightHuResponse(FightHuResponse.newBuilder()).build());

			// 移除自己除了选定的牌型id之外的所有牌
			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seat, callCardListId);

			// 标记为已经叫过了
			callCardList.call = true;

			this.sendChooseCardListOver(game, roleGameInfo);

			// if (!needWaitOtherCallCardListAction(game.getCallCardLists(),
			// seat)) {
			// 检查是否要等待其他人做选择
			if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
				return;
			}

			// 通知其他玩家自己胡
			this.huProgress(game, seat, roleGameInfo, callCardList);
		}
	}

	/**
	 * 胡的流程
	 * 
	 * @param game
	 * @param seat
	 * @param roleGameInfo
	 * @param callCardList
	 * @author wcy 2017年8月3日
	 */
	private void huProgress(Game game, int seat, RoleGameInfo roleGameInfo, CallCardList callCardList) {
		Hu hu = (Hu) callCardList.cardList;
		// 如果前面玩家杠了又胡则为杠开
		hu.gangKai = roleGameInfo.isGang;

		this.accumlateSendCardCount(game);
		// 其他同样可以胡的人都胡
		List<CallCardList> huCallCardLists = game.getHuCallCardLists();
		for (CallCardList huCallCardList : huCallCardLists) {
			Hu everyHu = (Hu) huCallCardList.cardList;
			loggerinfo("gameId=>" + game.getGameId() + "=>" + hu.toString());
			int masterSeat = huCallCardList.masterSeat;
			RoundCardsData huData = this.parseHu(everyHu);
			RoleGameInfo huRoleGameInfo = getRoleGameInfoBySeat(game, masterSeat);
			huRoleGameInfo.roundCardsData = huData;

			SC sc = SC.newBuilder().setSCFightHu(SCFightHu.newBuilder().setSeat(masterSeat).setHuData(huData)).build();
			this.sendAllSeatSC(game, sc);
			this.notifyObservers(FightConstant.FIGHT_HU, sc, game);
		}
		// 如果胡的牌是抢杠, 杠的人要移除杠
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.qiangGang != null) {
				this.removeGangTargetCard(info, info.qiangGang);
			}
		}

		this.sendAllChooseCardListOver(game);
		// 通知转向
		this.noticePointSeat(game, seat);
		this.over(game, seat);
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
	 * @param seatIndex
	 *            发送过的人的座位号
	 * @param gameSendCount
	 *            有客户端传送过来进行验证的标记
	 * @author wcy 2017年6月17日
	 */
	private void guo(Game game, int seatIndex, int gameSendCount) {
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seatIndex);
		// 杠标记取消
		roleGameInfo.isGang = false;

		if (game.getSendCardCount() != gameSendCount) {
			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder()
							.setFightGuoResponse(
									FightGuoResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
							.build());
			return;
		}

		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount) {
				SessionUtils.sc(roleGameInfo.roleId,
						SC.newBuilder().setFightGuoResponse(
								FightGuoResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
								.build());
				return;
			}

			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setFightGuoResponse(FightGuoResponse.newBuilder()).build());

			// 喊过就删除自己所有的叫牌
			this.deleteAllCallCardListBySeat(game.getCallCardLists(), seatIndex);
			this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seatIndex);

			this.sendChooseCardListOver(game, roleGameInfo);

			// if (!needWaitOtherCallCardListAction(game.getCallCardLists(),
			// seatIndex)) {

			// 等待其他人选择
			if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seatIndex)) {
				return;
			}

			this.sendAllChooseCardListOver(game);

			int index = game.getCurrentRoleIdIndex();
			// 如果过的是自己，那要再出一张牌
			if (index == seatIndex) {
				this.noticeSendCard(game);
			} else {
				// 获得之前一个人的牌
				CallCardList preCallCardList = this.getPreviousCallCardList(game.getCallCardLists());

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
			// }
		}
	}

	@Override
	public CallCardList getPreviousCallCardList(List<CallCardList> callCardLists) {
		for (CallCardList callCardList : callCardLists) {
			if (callCardList.call) {
				return callCardList;
			}
			continue;
		}

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
		loggerinfo("game=>" + game.getGameId() + " over");

		game.setFinishRoundCount(game.getFinishRoundCount() + 1);

		this.roundOver2(game, true);

		boolean isGameOver = isGameOver(game);
		if (isGameOver) {

			if (game.getGameState() == GameState.GAME_START_END)
				return;
			synchronized (game) {
				if (game.getGameState() == GameState.GAME_START_END)
					return;

				game.setGameState(isGameOver ? GameState.GAME_START_END : GameState.GAME_STATE_PREPARE);
				this.gameOver(game);
			}
		} else {
			// 除npc外所有玩家重置准备
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				if (info.roleId == 0) {
					continue;
				}
				info.ready = false;
			}

		}
	}

	private boolean isGameOver(Game game) {
		GameConfigData gameConfigData = game.getGameConfig();
		GameOverMethod gameOverMethod = gameConfigData.getGameOverMethod();

		if (gameOverMethod == GameOverMethod.GAME_OVER_ROUND) {
			int roundCount = gameConfigData.getRoundCount();
			int finshRoundCount = game.getFinishRoundCount();

			return finshRoundCount >= roundCount;
		} else {
			String endTimeStr = gameConfigData.getEndTime();
			String nowTimeStr = TimeUtils.get_HHmmss_DateFormat().format(new Date());
			boolean isPassTime = false;
			try {
				isPassTime = TimeUtils.compareHHmmss(nowTimeStr, endTimeStr) >= 0;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return isPassTime;
		}
	}

	/**
	 * 回合结束
	 * 
	 * @param game
	 * @param seat
	 * @author wcy 2017年6月22日
	 */
	private void roundOver(Game game, boolean checkHu) {
		SCFightRoundOver.Builder scFightRoundOverBuilder = SCFightRoundOver.newBuilder();
		GameConfigData config = game.getGameConfig();
		// 剩余几局没有打,如果要检查胡,才有剩余次数
		int maxCount = config.getMaxCount();
		int finishRoundCount = game.getFinishRoundCount();
		scFightRoundOverBuilder.setMaxRoundCount(maxCount);
		scFightRoundOverBuilder.setFinishRoundCount(finishRoundCount);
		int minScore = config.getMinStartScore();

		List<RoleRoundOverInfoData.Builder> roleRoundOverInfoDataBuilderList = new ArrayList<>(
				game.getRoleIdList().size());

		FlyResult flyResult = new FlyResult();
		// 抓苍蝇
		if (checkHu) {
			flyResult = flyChecker.calculateFlys(game);
			scFightRoundOverBuilder.addAllFlyCards(flyResult.getTouchCards());
		}

		int flyScore = flyResult.getScore();
		List<Integer> resultFlys = flyResult.getResultFlys();

		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);

			GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
			RoundCardsData gameCardsData = this.parseRoundCardsData(game, roleGameInfo);
			boolean containsHu = false;
			GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);
			if (checkHu) {
				// 查胡
				for (CallCardList callCardList : game.getHuCallCardLists()) {
					if (callCardList.masterSeat != i)
						continue;

					containsHu = true;
					Hu hu = (Hu) callCardList.cardList;

					OverMethod overMethod = OverMethod.OVER_HU;
					gameOverResult.huCount++;

					if (hu.isMine) {// 自摸 的人底分x3,苍蝇x3，如果是杠开还要再乘2，每家都扣分
						overMethod = OverMethod.OVER_MO_HU;

						gameOverResult.moHuCount++;

						roleGameInfo.roundOverResult.score += minScore * (3 * (hu.gangKai ? 2 : 1)) + flyScore * 3;
						for (RoleGameInfo info : game.getRoleIdMap().values()) {
							if (info.gameRoleId.equals(roleGameInfo.gameRoleId))
								continue;

							info.roundOverResult.score += -(minScore * (hu.gangKai ? 2 : 1) + flyScore);
						}
					} else if (hu.gangChong) {// 杠冲底分x3,苍蝇x3,被杠冲的人扣相同分数
						roleGameInfo.roundOverResult.score += (minScore + flyScore) * 3;

						RoleGameInfo targetRoleGameInfo = this.getRoleGameInfoBySeat(game, hu.getTargetSeat());
						targetRoleGameInfo.roundOverResult.score += -(minScore + flyScore) * 3;
					} else {
						roleGameInfo.roundOverResult.score += minScore + flyScore;

						RoleGameInfo targetRoleGameInfo = this.getRoleGameInfoBySeat(game, hu.getTargetSeat());
						targetRoleGameInfo.roundOverResult.score += -(minScore + flyScore);
					}

					RoleRoundOverInfoData.Builder builder = RoleRoundOverInfoData.newBuilder()
							.setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData).setMinScore(minScore)
							.setGangKai(hu.gangKai).setOverMethod(overMethod);

					roleRoundOverInfoDataBuilderList.add(builder);
				}
			}

			// 没胡就是输，检查点冲
			if (!containsHu) {
				OverMethod overMethod = OverMethod.OVER_LOSS;
				if (checkHu) {
					// 检查是否被点冲
					for (CallCardList huCallCardList : game.getHuCallCardLists()) {
						Hu hu = (Hu) huCallCardList.cardList;
						if (hu.getTargetSeat() == i) {
							// 点冲
							overMethod = OverMethod.OVER_CHONG;
							break;
						}
					}
				}
				RoleRoundOverInfoData.Builder builder = RoleRoundOverInfoData.newBuilder().setGameRoleData(gameRoleData)
						.setRoundCardsData(gameCardsData).setOverMethod(overMethod).setMinScore(minScore);
				roleRoundOverInfoDataBuilderList.add(builder);
			}

		}

		// 注意上面循环的数序必须与接下来的循环顺序相同
		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
			// 设置局的分数
			RoleRoundOverInfoData.Builder builder = roleRoundOverInfoDataBuilderList.get(i)
					.setRoundScore(roleGameInfo.roundOverResult.score);
			scFightRoundOverBuilder.addRoleRoundOverInfoData(builder);

			// 将分数加入总结算分数中

			// TODO java.lang.NullPointerException
			// at
			// com.randioo.mahjong_public_server.module.fight.service.FightServiceImpl.roundOver(FightServiceImpl.java:1990)
			// at
			// com.randioo.mahjong_public_server.module.fight.service.FightServiceImpl.cancelGame(FightServiceImpl.java:766)
			// at
			// com.randioo.mahjong_public_server.module.fight.service.FightServiceImpl.applyExitGame(FightServiceImpl.java:840)
			// at
			// com.randioo.mahjong_public_server.module.fight.action.FightApplyExitAction.execute(FightApplyExitAction.java:24)

			Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
			if (!resultMap.containsKey(roleGameInfo.gameRoleId)) {
				GameOverResult result = this.createRoleGameResult(roleGameInfo);
				resultMap.put(roleGameInfo.gameRoleId, result);
			}
			GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);
			gameOverResult.score += roleGameInfo.roundOverResult.score;

		}

		SC scFightRoundOverSC = SC.newBuilder().setSCFightRoundOver(scFightRoundOverBuilder).build();

		// 所有人发结算通知
		this.sendAllSeatSC(game, scFightRoundOverSC);

		notifyObservers(FightConstant.ROUND_OVER, scFightRoundOverSC, game, checkHu);
	}

	/**
	 * 玩家是否胡
	 * 
	 * @param overMethod
	 * @return
	 * @author wcy 2017年8月2日
	 */
	private boolean overMethodIsHu(OverMethod overMethod) {
		return overMethod == OverMethod.OVER_HU || overMethod == OverMethod.OVER_MO_HU;
	}

	private void roundOver2(Game game, boolean checkHu) {
		SCFightRoundOver.Builder scFightRoundOverBuilder = SCFightRoundOver.newBuilder();
		GameConfigData config = game.getGameConfig();
		// 剩余几局没有打,如果要检查胡,才有剩余次数
		int maxCount = config.getMaxCount();
		int finishRoundCount = game.getFinishRoundCount();
		scFightRoundOverBuilder.setMaxRoundCount(maxCount);
		scFightRoundOverBuilder.setFinishRoundCount(finishRoundCount);

		// 房间号
		Key key = game.getLockKey();
		String roomId = matchService.getLockString(key);
		scFightRoundOverBuilder.setRoomId(roomId);

		int minScore = config.getMinStartScore();

		// 抓苍蝇
		FlyResult flyResult = checkHu ? flyChecker.calculateFlys(game) : new FlyResult();
		int flyScore = flyResult.getScore();
		List<Integer> resultFlys = flyResult.getResultFlys();

		scFightRoundOverBuilder.addAllFlyCards(flyResult.getTouchCards());

		// 初始化结分器
		RoundOverParameter roundOverParameter = new RoundOverParameter();
		roundOverParameter.setCheckHu(checkHu);
		roundOverParameter.setFlyScore(flyScore);
		roundOverParameter.setMinScore(minScore);
		roundOverParameter.getHuCallCardList().addAll(game.getHuCallCardLists());
		roundOverParameter.getRoleIdList().addAll(game.getRoleIdList());

		SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();

		Map<Integer, RoundOverResult> roundOverResultMap = roundOverCalculator.getRoundOverResults(roundOverParameter);
		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
			RoundOverResult roundOverResult = roundOverResultMap.get(i);

			GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
			RoundCardsData gameCardsData = this.parseRoundCardsData(game, roleGameInfo);

			RoleRoundOverInfoData.Builder roleRoundOverInfoBuilder = RoleRoundOverInfoData.newBuilder()
					.setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData).setMinScore(minScore)
					.setOverMethod(roundOverResult.overMethod).setMoScore(roundOverResult.moScore);

			// 获得总结分对象
			this.checkGameOverResult(game, roleGameInfo);
			GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);

			// 是否结局是胡
			if (this.overMethodIsHu(roundOverResult.overMethod)) {
				roleRoundOverInfoBuilder.setGangKai(roundOverResult.gangKai);
			}

			switch (roundOverResult.overMethod) {
			case OVER_CHONG:// 点冲
				gameOverResult.dianChong++;
				break;
			case OVER_LOSS:// 正常输
				break;
			case OVER_HU:// 胡
				gameOverResult.huCount++;
				break;
			case OVER_MO_HU: {// 摸胡
				gameOverResult.moHuCount++;
				gameOverResult.huCount++;
				break;
			}
			default:
				break;

			}

			// 玩家回合分数
			roleGameInfo.roundOverResult.score += roundOverResult.score;

			// 分数增量
			RoundOverResult gameRoundOverResult = roleGameInfo.roundOverResult;
			int deltaScore = gameRoundOverResult.score - gameOverResult.score;
			// 计算所有杠的次数和所获得的分数
			int mingGangDeltaScore = gameRoundOverResult.mingGangScore - gameOverResult.mingGangScore;
			int mingGangDeltaCount = gameRoundOverResult.mingGangCount - gameOverResult.mingGangCount;
			int darkGangDeltaScore = gameRoundOverResult.darkGangScore - gameOverResult.darkGangScore;
			int darkGangDeltaCount = gameRoundOverResult.dardGangCount - gameOverResult.dardGangCount;
			int addGangDeltaScore = gameRoundOverResult.addGangScore - gameOverResult.addGangScore;
			int addGangDeltaCount = gameRoundOverResult.addGangCount - gameOverResult.addGangCount;

			// 设置回合分数
			roleRoundOverInfoBuilder.setRoundScore(deltaScore).setDarkGangCount(darkGangDeltaCount)
					.setDarkGangScore(darkGangDeltaScore).setMingGangCount(addGangDeltaCount + mingGangDeltaCount)
					.setMingGangScore(mingGangDeltaScore + addGangDeltaScore);

			scFightRoundOverBuilder.addRoleRoundOverInfoData(roleRoundOverInfoBuilder);

			// 二次结分汇总
			gameOverResult.score = roleGameInfo.roundOverResult.score;
			// 杠的汇总
			gameOverResult.addGangCount = gameRoundOverResult.addGangCount;
			gameOverResult.dardGangCount = gameRoundOverResult.dardGangCount;
			gameOverResult.mingGangCount = gameRoundOverResult.mingGangCount;
			gameOverResult.addGangScore = gameRoundOverResult.addGangScore;
			gameOverResult.darkGangScore = gameRoundOverResult.darkGangScore;
			gameOverResult.mingGangScore = gameRoundOverResult.mingGangScore;

			scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setSeat(i).setScore(gameOverResult.score));
		}

		SC scFightRoundOverSC = SC.newBuilder().setSCFightRoundOver(scFightRoundOverBuilder).build();
		SC scFightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();

		// 所有人发结算通知
		this.sendAllSeatSC(game, scFightRoundOverSC);

		// 所有人发送分数通知
		this.sendAllSeatSC(game, scFightScoreSC);

		this.notifyObservers(FightConstant.ROUND_OVER, scFightRoundOverSC, game, checkHu);
		this.notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);

		// 庄判断器
		int nextRoundZhuangSeat = zhuangJudger.getZhuangSeat(roundOverResultMap);
		// 返回-1表示沿用这一局的庄家
		if (nextRoundZhuangSeat != -1)
			game.setZhuangSeat(nextRoundZhuangSeat);
	}

	private void checkGameOverResult(Game game, RoleGameInfo roleGameInfo) {
		Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
		if (!resultMap.containsKey(roleGameInfo.gameRoleId)) {
			GameOverResult result = this.createRoleGameResult(roleGameInfo);
			resultMap.put(roleGameInfo.gameRoleId, result);
		}
	}

	/**
	 * 游戏结束
	 * 
	 * @param game
	 * @author wcy 2017年6月22日
	 */
	private void gameOver(Game game) {
		// 一定要发这一句，让前端把钥匙清空
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightClearRoomId(SCFightClearRoomId.newBuilder()).build());

		SCFightGameOver scFightGameOver = this.parseGameOverData(game);

		this.sendAllSeatSC(game, SC.newBuilder().setSCFightGameOver(scFightGameOver).build());
		// this.notifyObservers(FightConstant.FIGHT_GAME_OVER, scFightGameOver,
		// game);
		this.notifyObservers(FightConstant.FIGHT_GAME_OVER, SC.newBuilder().setSCFightGameOver(scFightGameOver).build(),
				game);

		this.destroyGame(game);
	}

	/**
	 * 
	 * @param game
	 * @return
	 * @author wcy 2017年7月14日
	 */
	private SCFightGameOver parseGameOverData(Game game) {
		SCFightGameOver.Builder fightGameOverBuilder = SCFightGameOver.newBuilder();
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
			GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);

			int huCount = gameOverResult.huCount;
			int moHuCount = gameOverResult.moHuCount;
			int dianChong = gameOverResult.dianChong;
			int zhuaHuCount = huCount - moHuCount;
			int totalGameScore = gameOverResult.score;

			RoleGameOverInfoData roleGameOverInfoData = RoleGameOverInfoData.newBuilder().setGameRoleData(gameRoleData)
					.setHuCount(huCount).setZhuaHuCount(zhuaHuCount).setMoHuCount(moHuCount)
					.setDianChongCount(dianChong).setGameScore(totalGameScore).build();

			fightGameOverBuilder.addRoleGameOverInfoData(roleGameOverInfoData);
		}
		// 通过钥匙获得房间号
		Key lockKey = game.getLockKey();
		String roomId = matchService.getLockString(lockKey);

		fightGameOverBuilder.setRoomId(roomId);
		int maxRoundCount = game.getGameConfig().getRoundCount();
		fightGameOverBuilder.setMaxRoundCount(maxRoundCount);
		fightGameOverBuilder.setFinishRoundCount(game.getFinishRoundCount());

		// 所有人发结算通知绿
		SCFightGameOver fightGameOver = fightGameOverBuilder.build();
		return fightGameOver;
	}

	/**
	 * 销毁游戏
	 * 
	 * @param game
	 * @author wcy 2017年7月13日
	 */
	private void destroyGame(Game game) {

		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
			if (role != null) {
				role.setGameId(0);

			}
		}

		Key key = game.getLockKey();
		if (key != null) {
			String lockString = matchService.getLockString(key);
			GameCache.getGameLockStringMap().remove(lockString);
			key.recycle();
		}

		// 将游戏从缓存池中移除
		GameCache.getGameMap().remove(game.getGameId());
	}

	private RoundCardsData parseRoundCardsData(Game game, RoleGameInfo roleGameInfo) {
		List<CardList> cardLists = roleGameInfo.showCardLists;
		RoundCardsData.Builder gameCardsDataBuilder = RoundCardsData.newBuilder().setHuCard(
				roleGameInfo.roundCardsData == null ? roleGameInfo.newCard : roleGameInfo.roundCardsData.getHuCard())
				.addAllHandCards(roleGameInfo.cards);
		for (CardList cardList : cardLists) {
			Function function = GameCache.getParseCardListToProtoFunctionMap().get(cardList.getClass());
			CardListData cardListData = (CardListData) function.apply(cardList);
			gameCardsDataBuilder.addCardListData(cardListData);
		}

		return gameCardsDataBuilder.build();
	}

	/**
	 * 某玩家出牌绿
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
				game, SC
						.newBuilder().setSCFightSendCard(SCFightSendCard.newBuilder()
								.setSeat(game.getCurrentRoleIdIndex()).setCard(card).setIsTouchCard(isSendTouchCard))
						.build());
		this.notifyObservers(
				FightConstant.FIGHT_SEND_CARD, SC
						.newBuilder().setSCFightSendCard(SCFightSendCard.newBuilder()
								.setSeat(game.getCurrentRoleIdIndex()).setCard(card).setIsTouchCard(isSendTouchCard))
						.build(),
				game);
		// 如果有摸得牌还在要加入到手牌
		if (!isSendTouchCard) {
			this.newCardAdd2Cards(roleGameInfo);
		}

		// 清空临时列表
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

		// 不是白搭牌才能杠碰胡
		boolean isBaiDaCard = GameCache.getBaiDaCardNumSet().contains(card);
		if (!isBaiDaCard) {
			// 保存场上除了本人的杠碰胡
			for (int index = 0; index < game.getRoleIdList().size(); index++) {
				// 自己不能碰自己
				if (index == game.getCurrentRoleIdIndex())
					continue;

				this.checkOtherCallCardList(game, index, card, GameCache.getCheckCardListSequence());
			}
		}

		// 其他人杠碰胡过或下一个人
		this.otherRoleGangPengHuOrNextOne(game);

	}

	/**
	 * 其他人杠碰胡过或下一个人绿
	 * 
	 * @param game
	 * @author wcy 2017年7月10日
	 */
	private void otherRoleGangPengHuOrNextOne(Game game) {
		// 如果没有可以杠碰胡则通知下一个人，如果有则发送通知并等待反馈
		if (game.getCallCardLists().size() == 0) {
			// 下一个人
			this.nextIndex(game);
			// 摸牌
			this.touchCard(game);

		} else {
			loggerinfo("gameId=>" + game.getGameId() + "=>sendGangPengHuMsg2Role");
			this.noticeCountDown(game, 10);
			this.sendGangPengHuMsg2Role(game);
		}
	}

	/**
	 * 检查听牌绿
	 * 
	 * @param game
	 * @param gameRoleId
	 * @return
	 * @author wcy 2017年7月10日
	 */
	private boolean checkTing(Game game, String gameRoleId) {
		Class<? extends CardList> clazz = GameCache.getCheckGangCardList().get(0);
		Hu hu = (Hu) GameCache.getCardLists().get(clazz);

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		roleGameInfo.tingCards.clear();

		List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
		CardSort cardSort = new CardSort(4);

		cardSort.fillCardSort(cards);

		hu.checkTing(cardSort, roleGameInfo.tingCards, game.getGameConfig());
		boolean containsWaitCards = roleGameInfo.tingCards.size() > 0;

		return containsWaitCards;
	}

	/**
	 * 新牌加绿入到手牌
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
	 * 发送倒计绿时
	 * 
	 * @param gameId
	 * @param countdown
	 * @author wcy 2017年6月17日
	 */
	private void noticeCountDown(Game game, int countdown) {
		// 发送倒计时
		this.sendAllSeatSC(game,
				SC.newBuilder().setSCFightCountdown(SCFightCountdown.newBuilder().setCountdown(10)).build());
		this.notifyObservers(FightConstant.FIGHT_COUNT_DOWN,
				SC.newBuilder().setSCFightCountdown(SCFightCountdown.newBuilder().setCountdown(10)).build(), game);
	}

	/**
	 * 座位指绿针
	 * 
	 * @param game
	 * @param seat
	 * @author wcy 2017年6月21日
	 */
	private void noticePointSeat(Game game, int seat) {
		SC sc = SC.newBuilder().setSCFightPointSeat(
				SCFightPointSeat.newBuilder().setSeat(seat).setTempGameCount(game.getSendCardCount())).build();
		this.sendAllSeatSC(game, sc);
		this.notifyObservers(FightConstant.FIGHT_POINT_SEAT, sc, game);
	}

	/**
	 * 检查叫碰杠胡的绿动作
	 * 
	 * @param game
	 * @param hasGangPengHuSeatedIndex
	 * @param card
	 * @param list
	 *            需要获得的牌绿型
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
			templateCardList.check(game.getGameConfig(), cardLists, cardSort, card, roleGameInfo.showCardLists, true);

			for (CardList cardList : cardLists) {
				cardList.setTargetSeat(currentRoleIdSeat);

				CallCardList callCardList = new CallCardList();
				callCardList.cardListId = callCardLists.size() + 1;
				callCardList.masterSeat = hasGangPengHuSeatedIndex;
				callCardList.cardList = cardList;

				callCardLists.add(callCardList);
				// 如果是胡放到另一个数组中
				if (cardList instanceof Hu)
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
	 * @param list
	 *            需要获得的绿牌型
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
			templateCardList.check(game.getGameConfig(), cardLists, cardSort, card, roleGameInfo.showCardLists, false);

			for (CardList cardList : cardLists) {
				cardList.setTargetSeat(currentRoleIdSeat);

				CallCardList callCardList = new CallCardList();
				callCardList.cardListId = callCardLists.size() + 1;
				callCardList.masterSeat = hasGangPengHuSeatedIndex;
				callCardList.cardList = cardList;

				callCardLists.add(callCardList);
				// 如果是胡放到另一个绿数组中
				if (cardList instanceof Hu)
					huCallCardLists.add(callCardList);
			}

			cardLists.clear();
		}
	}

	/*
	 * scType 为通知类型
	 */
	private void sendAllSeatSC(Game game, SC sc) {
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			SessionUtils.sc(roleGameInfo.roleId, sc);
		}
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
	@Override
	public Game getGameById(int gameId) {
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
		gangDataBuilder.setCardListType(gang.dark ? CardListType.CARD_LIST_TYPE_GANG_DARK
				: gang.peng == null ? CardListType.CARD_LIST_TYPE_GANG_LIGHT : CardListType.CARD_LIST_TYPE_GANG_ADD);

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

	@Override
	public void disconnect(Role role) {
		int gameId = role.getGameId();
		if (role.getGameId() <= 0)
			return;

		Game game = this.getGameById(gameId);
		if (game == null) {
			return;
		}
		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

		SC sc = SC.newBuilder()
				.setSCFightDisconnect(SCFightDisconnect.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId)))
				.build();
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			if (roleGameInfo.roleId == role.getRoleId())
				continue;

			SessionUtils.sc(roleGameInfo.roleId, sc);
		}
	}

	// 重连
	// @Override
	// public void rejoin(Role role) {
	// int gameId = role.getGameId();
	// /* System.out.println("@@@@"+gameId); */
	// Game game = GameCache.getGameMap().get(gameId);
	// if (game == null) {
	// SessionUtils
	// .sc(role.getRoleId(),
	// SC.newBuilder()
	// .setFightRejoinResponse(
	// FightRejoinResponse.newBuilder().setErrorCode(
	// ErrorCode.GAME_NOT_EXIST.getNumber())).build());
	// return;
	// }
	// RoleGameInfo myInfo = null;
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// if (info.roleId == role.getRoleId()) {
	// myInfo = info;
	// break;
	// }
	// }
	// if (myInfo == null) {
	// SessionUtils.sc(
	// role.getRoleId(),
	// SC.newBuilder()
	// .setFightRejoinResponse(
	// FightRejoinResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber()))
	// .build());
	// return;
	// }
	// List<ByteString> scList = new ArrayList<>();

	// 重连后逐条发通知

	// 进入游戏玩家的sc
	// FIXME
	// VideoData videoData = myInfo.videoData;
	// List<List<SC>> scs = videoData.getScList();
	// if (scs != null) {
	// for (SC sc : scs.get(0)) {
	// scList.add(sc.toByteString());
	// }
	// }
	//
	// // 单局内的sc
	// for (SC sc : myInfo.roundSCList) {
	// scList.add(sc.toByteString());
	// }
	// System.out.println(scList);
	// System.out.println(scList.size());
	// RoundVideoData roundVideoData =
	// RoundVideoData.newBuilder().addAllSc(scList).build();
	// SessionUtils.sc(
	// role.getRoleId(),
	// SC.newBuilder()
	// .setFightRejoinResponse(
	// FightRejoinResponse.newBuilder().setRoundVideoData(roundVideoData)
	// .setLockString(matchService.getLockString(game.getLockKey()))).build());
	// // myInfo.online = true;
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {// 通知其他玩家我已经重上好了
	// SessionUtils.sc(
	// info.roleId,
	// SC.newBuilder()
	// .setSCFightRejoin(
	// SCFightRejoin.newBuilder().setSeated(
	// game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
	//
	// }
	//
	// // if (myInfo.auto >= 2) {// 重连后解除托管状态
	// // this.auto(role);
	// // }
	// }

	public static void main(String[] args) {

		GlobleXmlLoader.init("./server.xml");

		GlobleMap.putParam(GlobleConstant.ARGS_PORT, Integer.parseInt(args[0]));
		GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, false);

		String projectName = GlobleMap.String(GlobleConstant.ARGS_PROJECT_NAME)
				+ GlobleMap.Int(GlobleConstant.ARGS_PORT);
		HttpLogUtils.setProjectName(projectName);

		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		GameServerInit gameServerInit = ((GameServerInit) SpringContext.getBean("gameServerInit"));

		gameServerInit.start();

		Game game = new Game();
		game.setGameId(1);
		GameCache.getGameMap().put(1, game);
		GameConfigData config = GameConfigData.newBuilder().setEndTime("4:00:00").setMaxCount(4).build();
		game.setGameConfig(config);
		//
		// MatchServiceImpl matchService = new MatchServiceImpl();
		// VideoServiceImpl videoService = new VideoServiceImpl();

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

		// FightServiceImpl fightService = new FightServiceImpl();
		// fightService.matchService = matchService;
		// fightService.videoService = videoService;
		//
		// fightService.init();
		// fightService.initService();
		FightService fightService = SpringContext.getBean("fightService");

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
