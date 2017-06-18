package com.randioo.mahjong_public_server.module.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.AIChooseCallCardListTimeEvent;
import com.randioo.mahjong_public_server.entity.po.AISendCardTimeEvent;
import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.SendCardTimeEvent;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.entity.po.cardlist.Chi;
import com.randioo.mahjong_public_server.entity.po.cardlist.Gang;
import com.randioo.mahjong_public_server.entity.po.cardlist.Hu;
import com.randioo.mahjong_public_server.entity.po.cardlist.Peng;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.module.match.service.MatchServiceImpl;
import com.randioo.mahjong_public_server.protocol.Entity.CallGangData;
import com.randioo.mahjong_public_server.protocol.Entity.CallHuData;
import com.randioo.mahjong_public_server.protocol.Entity.CallPengData;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfig;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GangData;
import com.randioo.mahjong_public_server.protocol.Entity.HuData;
import com.randioo.mahjong_public_server.protocol.Entity.PaiNum;
import com.randioo.mahjong_public_server.protocol.Entity.PengData;
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
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCountdown;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList.Builder;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightPeng;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightStart;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTouchCard;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.CardTools;
import com.randioo.mahjong_public_server.util.Lists;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.config.GlobleConfig.GlobleEnum;
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
		// GameCache.getCheckCardListSequence().add(Gang.class);
		GameCache.getCheckCardListSequence().add(Peng.class);

		// GameCache.getCheckSelfCardList().add(Hu.class);
		// GameCache.getCheckSelfCardList().add(Gang.class);

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
		GameCache.getAddProtoFunctionMap().put(Peng.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				PengData pengData = (PengData) params[2];
				builder.setCallPengData(CallPengData.newBuilder().setCallId(callId).setPengData(pengData));
				return null;
			}
		});
		GameCache.getAddProtoFunctionMap().put(Gang.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				GangData gangData = (GangData) params[2];
				builder.setCallGangData(CallGangData.newBuilder().setGangData(gangData).setCallId(callId));
				return null;
			}
		});
		GameCache.getAddProtoFunctionMap().put(Hu.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				int callId = (Integer) params[1];
				HuData huData = (HuData) params[2];
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
		if (msg.equals(FightConstant.NEXT_GAME_ROLE_SEND_CARD)) {
			int gameId = (int) args[0];
			this.checkAutoAI(gameId);
		}
	}

	@Override
	public void initService() {
		this.addObserver(this);
	}

	@Override
	public void readyGame(Role role) {
		logger.trace("readyGame" + role.getAccount());
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

		roleGameInfo.ready = true;
		SC scFightReady = SC
				.newBuilder()
				.setSCFightReady(
						SCFightReady.newBuilder().setSeat(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)))
				.build();
		// 通知其他所有玩家，该玩家准备完毕
		for (RoleGameInfo info : game.getRoleIdMap().values())
			SessionUtils.sc(info.roleId, scFightReady);

		// 检查是否全部都准备完毕,全部准备完毕
		if (this.checkAllReady(role.getGameId())) {
			// 开始游戏
			logger.trace("startGame " + game.getGameId());
			this.gameStart(game.getGameId());
		}
	}

	/**
	 * 检查全部准备完毕
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年5月31日
	 */
	private boolean checkAllReady(int gameId) {
		logger.trace("checkAllReady");
		Game game = this.getGameById(gameId);
		GameConfig gameConfig = game.getGameConfig();
		if (game.getRoleIdMap().size() < gameConfig.getMaxCount())
			return false;

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready)
				return false;
		}
		return true;
	}

	@Override
	public void gameStart(int gameId) {
		logger.trace("gameStart");
		Game game = this.getGameById(gameId);
		GameConfig gameConfig = game.getGameConfig();

		game.setGameState(GameState.GAME_START_START);
		// 游戏初始化
		this.gameInit(game.getGameId());
		// 检查庄家
		this.checkZhuang(game.getGameId());
		// 发牌
		this.dispatchCard(game.getGameId());
		// 设置出牌玩家索引
		game.setCurrentRoleIdIndex(game.getZhuangSeat());

		// 设置每个人的座位和卡牌的数量
		SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
		for (int i = 0; i < gameConfig.getMaxCount(); i++) {
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
		this.touchCard(gameId);

		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			logger.trace(roleGameInfo.toString());
		}
		logger.trace(game.getRemainCards() + "");

		this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, gameId);
	}

	/**
	 * 游戏初始化
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	private void gameInit(int gameId) {
		Game game = this.getGameById(gameId);
		GameConfig config = game.getGameConfig();
		game.setMultiple(1);

		// 卡牌初始化
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			info.cards.clear();
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
	private void checkZhuang(int gameId) {
		boolean debug = true;
		Game game = this.getGameById(gameId);
		int zhuangGameRoleId = game.getZhuangSeat();
		// 如果没有庄家，则随机一个
		if (zhuangGameRoleId == -1) {
			int index = debug ? 0 : RandomUtils.getRandomNum(game.getRoleIdMap().size());
			game.setZhuangSeat(index);
		}

	}

	@Override
	public void dispatchCard(int gameId) {
		Game game = this.getGameById(gameId);

		// 赋值所有牌,然后随机一个个取
		List<Integer> remainCards = game.getRemainCards();
		Lists.fillList(remainCards, CardTools.CARDS);
		if (GlobleConfig.Boolean(GlobleEnum.DEBUG)) {

			int[][] arrs = { { 13, 15, 22, 22, 24, 25, 26, 29, 32, 35, 37, 37, 81 },
					{ 14, 21, 25, 25, 26, 29, 29, 31, 33, 36, 38, 81, 38 },
					{ 12, 12, 14, 17, 18, 21, 23, 24, 27, 34, 35, 36, 81 },
					{ 12, 16, 16, 16, 18, 21, 22, 23, 27, 28, 33, 34, 39 } };
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

			for (int card : removeList) {
				for (int i = remainCards.size() - 1; i >= 0; i--) {
					if (card == remainCards.get(i)) {
						remainCards.remove(i);
						break;
					}
				}
			}

		} else {
			for (int i = 0; i < 13; i++) {
				// 每个人先发出13张牌
				for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
					int index = RandomUtils.getRandomNum(remainCards.size());
					roleGameInfo.cards.add(remainCards.get(index));
					remainCards.remove(index);
				}
			}
		}
		// 每个玩家卡牌排序
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			Collections.sort(roleGameInfo.cards);
			logger.trace(roleGameInfo.gameRoleId + "," + roleGameInfo.cards);
		}
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
	public void touchCard(int gameId) {
		Game game = this.getGameById(gameId);
		int seat = game.getCurrentRoleIdIndex();
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(gameId, seat);

		List<Integer> remainCards = game.getRemainCards();
		if (remainCards.size() > 0) {
			List<Integer> list = cheatTouchCards.get(seat);
			if (list.size() != 0) {
				for (int i = game.getRemainCards().size() - 1; i >= 0; i--) {
					if (game.getRemainCards().get(i) == list.get(0)) {
						roleGameInfo.newCard = list.get(0);
						game.getRemainCards().remove(i);
						list.remove(0);
						break;
					}
				}
			} else {
				roleGameInfo.newCard = remainCards.remove(0);
			}

		} else {// 牌出完了，则游戏技术
			this.gameOver(gameId, seat);
			return;
		}

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
		this.checkCallCardList(gameId, game.getCurrentRoleIdIndex(), roleGameInfo.newCard,
				GameCache.getCheckSelfCardList());

		// 检查有没有可以杠碰胡的牌
		if (game.getCallCardLists().size() > 0) {
			this.sendGangPengHuMsg2Role(gameId);
		}

		// 通知出牌
		this.noticeSendCard(gameId);

		this.noticeCountDown(gameId, 10);
	}

	/**
	 * 通知出牌
	 * 
	 * @param gameId
	 * @author wcy 2017年6月16日
	 */
	private void noticeSendCard(int gameId) {
		Game game = this.getGameById(gameId);
		RoleGameInfo roleGameInfo = this.getCurrentRoleGameInfo(gameId);
		int index = game.getRoleIdList().indexOf(roleGameInfo.gameRoleId);

		this.sendAllSeatSC(game,
				SC.newBuilder().setSCFightNoticeSendCard(SCFightNoticeSendCard.newBuilder().setSeat(index)).build());

		RoleGameInfo nextRoleGameInfo = this.getCurrentRoleGameInfo(gameId);
		if (nextRoleGameInfo.roleId == 0) {
			AISendCardTimeEvent evt = new AISendCardTimeEvent() {

				@Override
				public void update(TimeEvent timeEvent) {
					Game game = getGameById(gameId);
					RoleGameInfo roleGameInfo = getCurrentRoleGameInfo(gameId);
					int cardIndex = RandomUtils.getRandomNum(roleGameInfo.cards.size());

					int index = game.getRoleIdList().indexOf(roleGameInfo.gameRoleId);
					if (autoCards.get(index).size() > 0) {
						int value = autoCards.get(index).remove(0);
						gameRoleIdSendCard(value, gameId, roleGameInfo.gameRoleId, false);
					} else if (roleGameInfo.cards.size() > 0) {
						gameRoleIdSendCard(roleGameInfo.cards.get(cardIndex), gameId, roleGameInfo.gameRoleId, false);
					}
					for (RoleGameInfo info : game.getRoleIdMap().values()) {
						logger.debug(info + "");
					}
				}
			};
			evt.setEndTime(TimeUtils.getNowTime() + 1);
			evt.setGameId(gameId);
			eventScheduler.addEvent(evt);
		}
		this.notifyObservers(FightConstant.FIGHT_NOTICE_SEND_CARD, gameId);
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
		String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
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

		if (!roleGameInfo.cards.contains(card) && roleGameInfo.newCard != card) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightSendCardResponse(
									FightSendCardResponse.newBuilder().setErrorCode(
											ErrorCode.FIGHT_MORE_CARD.getNumber())).build());
			return;
		}

		// 发送卡牌
		logger.trace("" + SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder())
				.build());

		// 自动出牌解除
		roleGameInfo.auto = 0;

		// 该玩家出牌
		this.gameRoleIdSendCard(card, gameId, gameRoleId, isTouchCard);

	}

	/**
	 * 卡牌指针移动
	 * 
	 * @param gameId
	 * @author wcy 2017年6月16日
	 */
	private void jumpCardSeat(int gameId) {
		Game game = this.getGameById(gameId);
		game.setCurrentCardSeatIndex(game.getCurrentRoleIdIndex());
	}

	/***
	 * 发送杠碰胡的消息给玩家
	 * 
	 * @param gameId
	 * @author wcy 2017年6月14日
	 */
	private void sendGangPengHuMsg2Role(int gameId) {
		Game game = this.getGameById(gameId);
		int currentRoleIndex = game.getCurrentRoleIdIndex();
		// 有杠碰胡的要发送给相应的玩家
		for (int index = 0; index < game.getRoleIdList().size(); index++) {
			// 如果是自己直接跳过
			if (currentRoleIndex == index)
				continue;

			List<CallCardList> callCardLists = game.getCallCardLists();
			// 分开卡牌发送给每个人
			SCFightNoticeChooseCardList.Builder builder = SCFightNoticeChooseCardList.newBuilder();
			int count = 0;
			for (CallCardList callCardList : callCardLists) {
				if (callCardList.masterSeat != index)
					continue;
				count++;
				Class<? extends CardList> clazz = callCardList.cardList.getClass();

				Function parseCardListToProtoFunction = GameCache.getParseCardListToProtoFunctionMap().get(clazz);
				Function addProtoFunction = GameCache.getAddProtoFunctionMap().get(clazz);

				Object cardListProtoData = parseCardListToProtoFunction.apply(callCardList.cardList);
				addProtoFunction.apply(builder, callCardList.cardListId, cardListProtoData);
			}

			// 有杠碰胡则需要通知
			if (count > 0) {
				String gameRoleIdString = game.getRoleIdList().get(index);
				RoleGameInfo tempRoleGameInfo = game.getRoleIdMap().get(gameRoleIdString);
				SCFightNoticeChooseCardList scFightNoticeChooseCardList = builder.setTempGameCount(
						game.getSendCardCount()).build();

				SessionUtils.sc(tempRoleGameInfo.roleId,
						SC.newBuilder().setSCFightNoticeChooseCardList(scFightNoticeChooseCardList).build());

				// 如果是机器人，则自动出牌
				if (tempRoleGameInfo.roleId == 0) {
					// 机器人处理杠碰胡
					AIChooseCallCardListTimeEvent chooseTimeEvent = new AIChooseCallCardListTimeEvent() {

						@Override
						public void update(TimeEvent timeEvent) {
							SCFightNoticeChooseCardList sc = (SCFightNoticeChooseCardList) message;
							guo(gameId, AISeat, sc.getTempGameCount());
						}
					};
					chooseTimeEvent.setEndTime(TimeUtils.getNowTime() + 1);
					chooseTimeEvent.setMessage(scFightNoticeChooseCardList);
					chooseTimeEvent.setGameId(gameId);
					chooseTimeEvent.setAISeat(index);
					eventScheduler.addEvent(chooseTimeEvent);
				}

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

		this.peng(gameId, seatIndex, gameSendCount, cardListId);
	}

	/**
	 * 碰
	 * 
	 * @param seat
	 * @param gameSendCount
	 * @author wcy 2017年6月17日
	 */
	private void peng(int gameId, int seat, int gameSendCount, int callCardListId) {
		Game game = this.getGameById(gameId);
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(gameId, seat);
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
				CardList cardList = callCardList.cardList;

				// 移除手牌
				roleGameInfo.cards.removeAll(cardList.getCards());

				// 显示到我方已碰的桌面上
				roleGameInfo.showCardLists.add(cardList);

				PengData pengData = this.parsePeng((Peng) cardList);

				// 通知其他玩家自己碰
				this.sendAllSeatSC(game,
						SC.newBuilder().setSCFightPeng(SCFightPeng.newBuilder().setPengData(pengData).setSeat(seat))
								.build());
				// 跳转到当前碰的人
				this.jumpToIndex(gameId, seat);
				// 摸牌
				this.touchCard(gameId);

			}
		}
	}

	@Override
	public void gang(Role role, int gameSendCount, int callCardListId) {

		// 收到杠的信息
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightGangResponse(FightGangResponse.newBuilder()).build());

		int gameId = role.getGameId();
		Game game = this.getGameById(gameId);

		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		if (game.getSendCardCount() != gameSendCount)
			return;
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount)
				return;

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seatIndex, callCardListId);

			if (!needWaitOtherCallCardListAction(game.getCallCardLists(), seatIndex)) {
				// 跳转到当前杠的人
				this.jumpToIndex(gameId, seatIndex);

				// TODO 牌归自己

				// TODO 显示到桌面上

				// TODO 通知其他玩家本操作

				// TODO 拿一张牌
				// this.touchCard(gameRoleId, gameId);

				SessionUtils.sc(role.getRoleId(),
						SC.newBuilder().setSCFightNoticeSendCard(SCFightNoticeSendCard.newBuilder()).build());

			}
		}
	}

	@Override
	public void hu(Role role, int gameSendCount, int callCardListId) {

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightHuResponse(FightHuResponse.newBuilder()).build());

		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);

		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);
		if (game.getSendCardCount() != gameSendCount)
			return;
		synchronized (game.getCallCardLists()) {
			if (game.getSendCardCount() != gameSendCount)
				return;

			CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
					seatIndex, callCardListId);

			// 可能有好几个人都胡了，所以要等待
			if (!this.needWaitOtherCallCardListAction(game.getCallCardLists(), seatIndex)) {

				this.gameOver(gameId, seatIndex);
			}
		}
	}

	@Override
	public void guo(Role role, int gameSendCount) {
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightGuoResponse(FightGuoResponse.newBuilder()).build());

		int gameId = role.getGameId();
		Game game = getGameById(gameId);
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.guo(gameId, seatIndex, gameSendCount);

		// this.getNextIndex(gameId);
		//
		// RoleGameInfo roleGameInfo =
		// this.getCurrentRoleGameInfo(gameId);
		//
		// touchCard(roleGameInfo.gameRoleId, gameId);
		//
		// SessionUtils.sc(roleGameInfo.roleId,
		//
		// SC.newBuilder().setSCFightNoticeSendCard(SCFightNoticeSendCard.newBuilder()).build());
		// }

	}

	/**
	 * 
	 * @param gameId
	 * @param seatIndex 发送过的人的座位号
	 * @param gameSendCount 有客户端传送过来进行验证的标记
	 * @author wcy 2017年6月17日
	 */
	private void guo(int gameId, int seatIndex, int gameSendCount) {

		Game game = this.getGameById(gameId);
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(gameId, seatIndex);

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

			this.deleteAllCallCardListBySeat(game.getCallCardLists(), seatIndex);

			if (!needWaitOtherCallCardListAction(game.getCallCardLists(), seatIndex)) {

				// 获得之前一个人的牌
				CallCardList preCallCardList = this.getPreviousCallCardList(gameId);

				if (preCallCardList == null) {
					// 如果没有其他人有选择权,则直接顺序下一个人
					this.nextIndex(gameId);
					this.touchCard(gameId);
				} else {
					// 如果做过判断了，则就是这个人的选择了
					if (preCallCardList.call) {
						this.jumpToIndex(gameId, preCallCardList.masterSeat);
						this.touchCard(gameId);
					}
				}
			}
		}
	}

	@Override
	public CallCardList getPreviousCallCardList(int gameId) {
		Game game = this.getGameById(gameId);
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

	private void gameOver(int gameId, int seat) {
		Game game = this.getGameById(gameId);
		Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();
		Map<Integer, Integer> gameResultMap = new HashMap<>();
		for (RoleGameInfo roleGameInfo : roleGameInfoMap.values()) {
			// TODO
			gameResultMap.put(roleGameInfo.roleId, 0);
		}

		// 所有人发结算通知
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightGameOver(SCFightGameOver.newBuilder()).build());
		// 所有人发准备通知
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build());

		notifyObservers(FightConstant.GAME_OVER, gameResultMap);
	}

	/**
	 * 某玩家出牌
	 * 
	 * @param card
	 * @param gameId
	 * @param gameRoleId
	 */
	private void gameRoleIdSendCard(int card, int gameId, String gameRoleId, boolean isSendTouchCard) {
		Game game = this.getGameById(gameId);

		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		// 设置当前的牌
		List<Integer> sendDesktopCards = game.getSendDesktopCardMap().get(game.getCurrentRoleIdIndex());
		sendDesktopCards.add(card);

		// 从手上减掉牌
		if (isSendTouchCard) {
			roleGameInfo.newCard = 0;
		} else {
			for (int i = roleGameInfo.cards.size() - 1; i >= 0; i--) {
				int tempCard = roleGameInfo.cards.get(i);
				if (card == tempCard) {
					roleGameInfo.cards.remove(i);
					break;
				}
			}
		}

		// 设置当前的出牌
		this.jumpCardSeat(gameId);

		// 通知所有人,此人出的牌
		this.sendAllSeatSC(
				game,
				SC.newBuilder()
						.setSCFightSendCard(
								SCFightSendCard.newBuilder().setSeat(game.getCurrentRoleIdIndex()).setCard(card)
										.setIsTouchCard(isSendTouchCard)).build());

		// 如果有摸得牌还在要加入到手牌
		if (isSendTouchCard) {
			roleGameInfo.cards.add(roleGameInfo.newCard);
			roleGameInfo.newCard = 0;
			Collections.sort(roleGameInfo.cards);
		}

		// 清空临时列表
		game.getCallCardLists().clear();
		game.getHuCallCardLists().clear();

		// 保存场上除了本人的杠碰胡
		for (int index = 0; index < game.getRoleIdList().size(); index++) {
			// 自己不能碰自己
			if (index == game.getCurrentRoleIdIndex())
				continue;

			this.checkCallCardList(gameId, index, card, GameCache.getCheckCardListSequence());
		}

		// 如果没有可以杠碰胡则通知下一个人，如果有则发送通知并等待反馈
		if (game.getCallCardLists().size() == 0) {
			// 下一个人
			this.nextIndex(gameId);
			// 摸牌
			this.touchCard(gameId);

		} else {
			logger.info("sendGangPengHuMsg2Role ");
			this.sendGangPengHuMsg2Role(gameId);
			this.noticeCountDown(gameId, 10);
		}

	}

	/**
	 * 发送倒计时
	 * 
	 * @param gameId
	 * @param countdown
	 * @author wcy 2017年6月17日
	 */
	private void noticeCountDown(int gameId, int countdown) {
		Game game = this.getGameById(gameId);
		// 发送倒计时
		this.sendAllSeatSC(game, SC.newBuilder().setSCFightCountdown(SCFightCountdown.newBuilder().setCountdown(10))
				.build());
	}

	/**
	 * 检查叫碰杠胡的动作
	 * 
	 * @param gameId
	 * @param hasGangPengHuSeatedIndex
	 * @param card
	 * @param list
	 * @author wcy 2017年6月14日
	 */
	private void checkCallCardList(int gameId, int hasGangPengHuSeatedIndex, int card,
			List<Class<? extends CardList>> list) {
		Game game = this.getGameById(gameId);
		String gameRoleId = game.getRoleIdList().get(hasGangPengHuSeatedIndex);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		CardSort cardSort = new CardSort(4);
		List<CardList> cardLists = new ArrayList<>();
		cardSort.fillCardSort(roleGameInfo.cards);

		List<CallCardList> callCardLists = game.getCallCardLists();
		List<CallCardList> huCallCardLists = game.getHuCallCardLists();

		for (Class<? extends CardList> clazz : list) {
			CardList templateCardList = GameCache.getCardLists().get(clazz);
			templateCardList.check(cardLists, cardSort, card);

			for (CardList cardList : cardLists) {
				CallCardList callCardList = new CallCardList();
				callCardList.cardListId = callCardLists.size() + 1;
				callCardList.cardList = cardList;
				callCardList.masterSeat = hasGangPengHuSeatedIndex;
				callCardList.cardList.setTargetSeat(game.getCurrentCardSeatIndex());

				callCardLists.add(callCardList);
				// 如果是胡放到另一个数组中
				if (cardList.getClass() == Hu.class)
					huCallCardLists.add(callCardList);
			}

			cardLists.clear();
		}
	}

	private void sendAllSeatSC(Game game, SC sc) {
		List<String> roleIdList = game.getRoleIdList();
		for (String gameRoleId : roleIdList) {
			RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
			SessionUtils.sc(info.roleId, sc);
		}

	}

	private void checkAutoAI(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		// 发送等待消息
		RoleGameInfo info = getCurrentRoleGameInfo(gameId);
		if (info.auto >= 2) {
			autoSendCard(gameId, info.gameRoleId);
			return;
		}

		SendCardTimeEvent sendCardTimeEvent = new SendCardTimeEvent() {

			@Override
			public void update(TimeEvent timeEvent) {
				timeUp((SendCardTimeEvent) timeEvent);
			}
		};

		sendCardTimeEvent.setSendCardCount(game.getSendCardCount());
		sendCardTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.SEND_CARD_WAIT_TIME);
		sendCardTimeEvent.setGameId(gameId);

		eventScheduler.addEvent(sendCardTimeEvent);
	}

	private void timeUp(SendCardTimeEvent event) {
		int gameId = event.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		// 如果出牌数已经改变,或者游戏已经结束,则直接返回
		if (game.getSendCardCount() != event.getSendCardCount())
			return;

		String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());

		this.autoSendCard(gameId, gameRoleId);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		roleGameInfo.auto++;

		System.out.println("time up");
	}

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
	private void nextIndex(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		jumpToIndex(gameId, (index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
	}

	/**
	 * 跳转到固定的某个人
	 * 
	 * @param gameId
	 * @param seatedIndex
	 * @return
	 * @author wcy 2017年6月14日
	 */
	private void jumpToIndex(int gameId, int seatedIndex) {
		Game game = this.getGameById(gameId);
		game.setCurrentRoleIdIndex(seatedIndex);
		// 出牌次数加1
		game.setSendCardCount(game.getSendCardCount() + 1);
	}

	/**
	 * 检查游戏是否结束
	 * 
	 * @param gameId
	 */
	public boolean checkGameOver(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (info.cards.size() == 0) {
				// 该玩家是赢家
				return true;
			}
		}

		return false;
	}

	/**
	 * 获得当前玩家的信息
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private RoleGameInfo getCurrentRoleGameInfo(int gameId) {
		Game game = this.getGameById(gameId);
		int index = game.getCurrentRoleIdIndex();
		RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(gameId, index);
		return roleGameInfo;
	}

	private RoleGameInfo getRoleGameInfoBySeat(int gameId, int seat) {
		Game game = this.getGameById(gameId);
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

	private GangData parseGang(Gang gang) {
		GangData.Builder gangDataBuilder = GangData.newBuilder();
		for (int i = 0; i < 4; i++) {
			gangDataBuilder.addCard(gang.card);
		}
		return gangDataBuilder.build();
	}

	private PengData parsePeng(Peng peng) {
		PengData.Builder pengDataBuilder = PengData.newBuilder();
		pengDataBuilder.setTargetSeat(peng.getTargetSeat());
		pengDataBuilder.setCard(peng.card);

		return pengDataBuilder.build();
	}

	private HuData parseHu(Hu hu) {
		return null;
	}

	/**
	 * 获得当前牌
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月12日
	 */
	private int getCurrentTargetCard(int gameId) {
		Game game = this.getGameById(gameId);
		int currentIndex = game.getCurrentRoleIdIndex();
		List<Integer> cards = game.getSendDesktopCardMap().get(currentIndex);
		return cards.get(cards.size() - 1);
	}

	public static void main(String[] args) {

		GlobleConfig.init("10006", "debug");
		Game game = new Game();
		game.setGameId(1);
		GameCache.getGameMap().put(1, game);
		game.setGameConfig(GameConfig.newBuilder().setMaxCount(4).build());

		MatchServiceImpl matchService = new MatchServiceImpl();

		RoleGameInfo r1 = new RoleGameInfo();
		r1.roleId = 1;
		RoleGameInfo r2 = new RoleGameInfo();
		RoleGameInfo r3 = new RoleGameInfo();
		RoleGameInfo r4 = new RoleGameInfo();

		r1.gameRoleId = matchService.getGameRoleId(1, 1);
		r2.gameRoleId = "1_0_0";
		r3.gameRoleId = "1_0_1";
		r4.gameRoleId = "1_0_2";

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

		fightService.gameStart(1);

		fightService.gameRoleIdSendCard(81, 1, matchService.getGameRoleId(1, 1), false);
		fightService.gameRoleIdSendCard(12, 1, "1_0_0", false);
		fightService.gameRoleIdSendCard(14, 1, "1_0_1", false);
		fightService.gameRoleIdSendCard(14, 1, "1_0_2", false);

	}

	private static Map<Integer, List<Integer>> autoCards = new HashMap<>();
	private static Map<Integer, List<Integer>> cheatTouchCards = new HashMap<>();
	static {
		autoCards.put(1, new ArrayList<Integer>());
		autoCards.put(2, new ArrayList<Integer>());
		autoCards.put(3, new ArrayList<Integer>());

		autoCards.get(1).add(37);

		cheatTouchCards.put(1, new ArrayList<Integer>());
		cheatTouchCards.put(2, new ArrayList<Integer>());
		cheatTouchCards.put(3, new ArrayList<Integer>());

		cheatTouchCards.get(1).add(37);
	}

}
