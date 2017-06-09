package com.randioo.mahjong_public_server.module.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.comparator.HexCardComparator;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.SendCardTimeEvent;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfig;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.PaiNum;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightReadyResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightSendCardResponse;
import com.randioo.mahjong_public_server.protocol.Fight.SCAgreeExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCallLandLord;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightStart;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.CardTools;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private HexCardComparator hexCardComparator;

	@Autowired
	private EventScheduler eventScheduler;

	@Override
	public void init() {

	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(FightConstant.APPLY_LEAVE)) {
			RoleGameInfo info = (RoleGameInfo) args[1];
			System.out.println("@@@" + info.roleId + info.gameRoleId);
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
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightReadyResponse(
									FightReadyResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}

		GameConfig gameConfig = game.getGameConfig();

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		// 游戏准备
		SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder())
				.build());

		roleGameInfo.ready = true;
		SC scFightReady = SC
				.newBuilder()
				.setSCFightReady(
						SCFightReady.newBuilder().setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)))
				.build();
		for (RoleGameInfo info : game.getRoleIdMap().values())
			SessionUtils.sc(info.roleId, scFightReady);

		// 检查是否全部都准备完毕,全部准备完毕开始游戏
		if (this.checkAllReady(role.getGameId())) {
			game.setGameState(GameState.GAME_START_START);
			// 游戏初始化
			this.gameInit(game.getGameId());
			SCFightStart.Builder scFightBuilder = SCFightStart.newBuilder();
			for (int i = 0; i < gameConfig.getMaxCount(); i++) {
				scFightBuilder.addPaiNum(PaiNum.newBuilder().setSeated(i)
						.setNum(game.getRoleIdMap().get(game.getRoleIdList().get(i)).cards.size()));
			}

			scFightBuilder.setTimes(game.getMultiple());
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				logger.debug("~~~" + info.roleId + game.getMultiple());
				SessionUtils.sc(info.roleId, SC.newBuilder().setSCFightStart(scFightBuilder.clone().addAllPai(info.cards))
						.build());
				SessionUtils.sc(
						info.roleId,
						SC.newBuilder()
								.setSCFightCallLandLord(
										SCFightCallLandLord.newBuilder().setCurrentFen(0)
												.setSeated(game.getCurrentRoleIdIndex())
												.setCountdown(FightConstant.COUNTDOWN)).build());
			}
			this.notifyObservers(FightConstant.NEXT_ROLE_TO_CALL_LANDLORD, game.getGameId());
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
		Game game = GameCache.getGameMap().get(gameId);
		GameConfig gameConfig = game.getGameConfig();
		if (game.getRoleIdMap().size() < gameConfig.getMaxCount())
			return false;

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			if (!info.ready)
				return false;
		}
		return true;
	}

	/**
	 * 游戏初始化
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	private void gameInit(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		game.setMultiple(1);

		// 卡牌初始化
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			info.cards.clear();
		}

		dispatchCard(game.getGameId());

	}

	@Override
	public GeneratedMessage exitGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			return SC
					.newBuilder()
					.setFightExitGameResponse(
							FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
					.build();
		}

		String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

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
				SC scExit = SC.newBuilder().setSCFightExitGame(SCFightExitGame.newBuilder().setGameRoleId(gameRoleId))
						.build();
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
		Game game = GameCache.getGameMap().get(role.getGameId());
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
		Game game = GameCache.getGameMap().get(gameId);
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
	public void dispatchCard(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		GameConfig gameConfig = game.getGameConfig();
		for (RoleGameInfo info : game.getRoleIdMap().values())
			info.cards.clear();

		int maxCount = gameConfig.getMaxCount();
		int needCard = 1;
		int totalCardCount = (CardTools.CARDS.length - maxCount) / maxCount;
		int landlordCardBoxIndex = RandomUtils.getRandomNum(needCard);
		int landlordCardIndex = RandomUtils.getRandomNum(FightConstant.CARDS.length);
		for (int j = 0; j < needCard; j++) {
			// 先添加所有的牌,然后逐一随机拿走
			List<Integer> list = new ArrayList<>(CardTools.CARDS.length);
			for (int i = 0; i < CardTools.CARDS.length; i++)
				list.add(CardTools.CARDS[i]);

			for (int i = 0; i < totalCardCount; i++) {
				for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
					int index = RandomUtils.getRandomNum(list.size());
					int value = list.get(index);
					list.remove(index);

					// 如果符合条件,就从这个人开始叫地主
					if (landlordCardBoxIndex == j && landlordCardIndex == index) {
						// 如果明牌是大小王，则要翻倍

						game.setMultiple(game.getMultiple()
								+ (value == CardTools.C_KING || value == CardTools.C_QUEUE ? 1 : 0));

						// 设置开始叫地主的人的索引
						game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId));
					}

					roleGameInfo.cards.add(value);
				}
			}

			// 对所有玩家的卡牌进行排序
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
				Collections.sort(roleGameInfo.cards, hexCardComparator);

		}
	}


	@Override
	public void sendCard(Role role, List<Integer> paiList) {
		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		if (game == null) {
			SessionUtils.sc(
					role.getRoleId(),
					SC.newBuilder()
							.setFightSendCardResponse(
									FightSendCardResponse.newBuilder().setErrorCode(
											ErrorCode.GAME_NOT_EXIST.getNumber())).build());
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
		}
		// 自动出牌解除
		roleGameInfo.auto = 0;
		this.gameRoleIdSendCard(paiList, gameId, gameRoleId);
	}

	private void gameRoleIdSendCard(List<Integer> paiList, int gameId, String gameRoleId) {
//		Game game = GameCache.getGameMap().get(gameId);
//		game.getGameConfig();
//		int roleId = game.getRoleIdMap().get(gameRoleId).roleId;
//
//		CardList lastCardList = game.getLastCardList();
//		CardList sendCardList = null;
//		// 如果长度是0，说明不出牌，则要检查是否允许不出牌
//		if (paiList.size() == 0) {
//			if (game.getPassCount() >= (game.getMaxRoleCount() - 1)) {
//				// 不允许不出牌
//				SessionUtils.sc(
//						roleId,
//						SC.newBuilder()
//								.setFightSendCardResponse(
//										FightSendCardResponse.newBuilder().setErrorCode(
//												ErrorCode.NULL_REJECT.getNumber())).build());
//			} else {
//				// 允许不出牌
//				game.setPassCount(game.getPassCount() + 1);
//			}
//		} else {
//			// 结构化卡牌，用于卡牌识别
//			CardSort cardSort = new CardSort();
//			CardTools.fillCardSort(cardSort, paiList);
//
//			// 如果没有先前的牌型，则为主动出牌,直接判断牌型<br>否则通过使用上一轮牌型判断
//			sendCardList = lastCardList == null ? initiativeSend(cardSort, paiList) : passiveSend(
//					lastCardList.getClass(), cardSort, paiList);
//
//			// 匹配牌型失败
//			if (sendCardList == null)
//				SessionUtils.sc(
//						roleId,
//						SC.newBuilder()
//								.setFightSendCardResponse(
//										FightSendCardResponse.newBuilder().setErrorCode(
//												ErrorCode.NOT_SAME_TYPE.getNumber())).build());
//
//			// 比较大小
//			if (lastCardList != null) {
//				try {
//					boolean bigger = sendCardListBiggerThanLastCardList(sendCardList, lastCardList);
//					if (!bigger)
//						SessionUtils.sc(
//								roleId,
//								SC.newBuilder()
//										.setFightSendCardResponse(
//												FightSendCardResponse.newBuilder().setErrorCode(
//														ErrorCode.SMALLER.getNumber())).build());
//				} catch (CardTypeComparableException e) {
//					SessionUtils.sc(
//							roleId,
//							SC.newBuilder()
//									.setFightSendCardResponse(
//											FightSendCardResponse.newBuilder().setErrorCode(
//													ErrorCode.NOT_SAME_TYPE.getNumber())).build());
//				}
//			}
//		}
//		// 设置最后一个人的牌型
//		game.setLastCardList(sendCardList);
//
//		// 如果出牌了，则放弃出牌的计数器重置
//		if (sendCardList != null)
//			game.setPassCount(0);
//
//		// 从手牌中移除该牌
//		RoleGameInfo info = game.getRoleIdMap().get(gameRoleId);
//		for (int pai : paiList) {
//			int index = info.cards.indexOf(pai);
//			info.cards.remove(index);
//		}
//		SessionUtils.sc(roleId, SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
//		/**
//		 * 通知已经出牌
//		 */
//		for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
//			SessionUtils.sc(
//					tInfo.roleId,
//					SC.newBuilder()
//							.setSCFightPutOutPai(
//									SCFightPutOutPai.newBuilder().addAllPutOutPai(paiList)
//											.setSeated(game.getCurrentRoleIdIndex())).build());
//			if (game.isMingPaiState()) {
//				RoleGameInfo LandLord = game.getRoleIdMap().get(game.getLandlordGameRoleId());
//				SessionUtils.sc(
//						tInfo.roleId,
//						SC.newBuilder()
//								.setSCFightMingPai(
//										SCFightMingPai.newBuilder().addAllPai(LandLord.cards)
//												.setSeated(game.getRoleIdList().indexOf(LandLord))).build());
//			}
//		}
//		notifyObservers(FightConstant.SEND_CARD, gameId, gameRoleId, sendCardList);
//
//		// 检查游戏是否结束
//		if (!this.checkGameOver(gameId)) {
//			// 如果没有结束，则请求下一个人出牌
//			// 将索引换到下一个人
//			getNextIndex(gameId);
//
//			game.setSendCardCount(game.getSendCardCount() + 1);
//			game.setSendCardTime(TimeUtils.getNowTime());
//			for (RoleGameInfo tInfo : game.getRoleIdMap().values()) {
//				SessionUtils.sc(
//						tInfo.roleId,
//						SC.newBuilder()
//								.setSCFightPutOut(
//										SCFightPutOut.newBuilder().setCountdown(FightConstant.COUNTDOWN)
//												.setSeated(game.getCurrentRoleIdIndex())).build());
//			}
//
//			notifyObservers(FightConstant.NEXT_GAME_ROLE_SEND_CARD, gameId);
//		}
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
		this.gameRoleIdSendCard(/* paiList */t, gameId, gameRoleId);

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

	private int getNextIndex(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		game.setCurrentRoleIdIndex((index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
		return game.getCurrentRoleIdIndex();
	}

	/**
	 * 主动出牌
	 * 
	 * @param cardSort
	 * @param paiList
	 * @return
	 */
//	private CardList initiativeSend(CardSort cardSort, List<Integer> paiList) {
//		for (CardList cardList : GameCache.getCardLists().values()) {
//			CardList sendCardList = checkCardList(cardList, cardSort, paiList);
//			if (sendCardList != null)
//				return sendCardList;
//		}
//		return null;
//	}

	/**
	 * 被动出牌
	 * 
	 * @param clazz
	 * @param cardSort
	 * @param paiList
	 * @return
	 */
//	private CardList passiveSend(Class<?> clazz, CardSort cardSort, List<Integer> paiList) {
//		CardList cardList = GameCache.getCardLists().get(clazz);
//		CardList sendCardList = checkCardList(cardList, cardSort, paiList);
//
//		if (sendCardList == null) {
//			// 没有匹配成功，再查炸弹
//			for (CardList checkCardList : GameCache.getSendCardSeqCheckerList()) {
//				if (sendCardList == null) {
//					sendCardList = checkCardList(checkCardList, cardSort, paiList);
//					if (sendCardList != null)
//						break;
//				}
//			}
//		}
//		return sendCardList;
//	}

	/**
	 * 发送的牌是否比上一次的牌大
	 * 
	 * @param sendCardList
	 * @param lastCardList
	 * @return
	 */
//	private boolean sendCardListBiggerThanLastCardList(CardList sendCardList, CardList lastCardList) {
//		int compare = sendCardList.compareTo(lastCardList);
//		if (compare <= 0)
//			return false;
//
//		return true;
//	}

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

	private void allScore(RoleGameInfo info) {
		int gameId = Integer.parseInt(info.gameRoleId.split("_")[0]);
		Game game = GameCache.getGameMap().get(gameId);
		
	}

	/**
	 * 根据原有牌型查牌型
	 * 
	 * @param targetCardList
	 * @param cardSort
	 * @param paiList
	 * @return
	 */
//	public CardList checkCardList(CardList targetCardList, CardSort cardSort, List<Integer> paiList) {
//		try {
//			return targetCardList.pattern(cardSort, paiList);
//		} catch (CardListPatternException e) {
//		}
//		return null;
//	}

	/**
	 * 获得当前玩家的信息
	 * 
	 * @param gameId
	 * @return
	 * @author wcy 2017年6月2日
	 */
	private RoleGameInfo getCurrentRoleGameInfo(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		String gameRoleId = game.getRoleIdList().get(index);
		return game.getRoleIdMap().get(gameRoleId);
	}

	public static void main(String[] args) {
		// test_dispatchCard();
		// test_linkedMap();
		// test_call_landlord();
	}
	//
	// private static void test_linkedMap() {
	// Map<Integer, Integer> map = new LinkedHashMap<>();
	// map.put(1, 1);
	// map.put(2, 2);
	// map.put(4, 4);
	// map.put(5, 5);
	// map.put(3, 3);
	// map.put(6, 6);
	// map.put(8, 8);
	// map.put(7, 7);
	// map.put(9, 9);
	// map.put(10, 10);
	// map.put(11, 11);
	// map.put(12, 12);
	// System.out.println(map.keySet());
	// }
	//
	// private static void test_dispatchCard() {
	// FightServiceImpl impl = new FightServiceImpl();
	// Game game = new Game();
	// game.setGameId(1);
	// game.setMaxRoleCount(3);
	// game.setMasterRoleId(1);
	//
	// GameCache.getGameMap().put(game.getGameId(), game);
	//
	// for (int i = 0; i < 3; i++) {
	// RoleGameInfo info = new RoleGameInfo();
	// info.gameRoleId = i + "";
	// info.roleId = i;
	// game.getRoleIdMap().put(info.gameRoleId, info);
	// }
	//
	// impl.dispatchCard(1);
	//
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// System.out.print(info.gameRoleId + "=");
	// for (int i : info.cards)
	// System.out.print(CardTools.toNum(i) + ",");
	// System.out.println();
	// }
	// for (int card : game.getLandlordCards())
	// System.out.print(CardTools.toNum(card) + ",");
	//
	// }
	//
	// private static void test_map() {
	// Map<Integer, Integer> map = new HashMap<>();
	// map.put(1, 1);
	// map.put(2, 2);
	// map.put(3, 3);
	// Set<Integer> set = map.keySet();
	// Set<Integer> set2 = map.keySet();
	// map.remove(1);
	// System.out.println(set);
	// }
	//
	// private static void test_call_landlord() {
	// FightServiceImpl impl = new FightServiceImpl();
	// Game game = new Game();
	// game.setGameId(1);
	// game.setMaxRoleCount(3);
	// game.setMasterRoleId(1);
	//
	// GameCache.getGameMap().put(game.getGameId(), game);
	//
	// for (int i = 0; i < 3; i++) {
	// RoleGameInfo info = new RoleGameInfo();
	// info.gameRoleId = i + "";
	// info.roleId = i;
	// game.getRoleIdMap().put(info.gameRoleId, info);
	// game.getRoleIdList().add(info.gameRoleId);
	// }
	//
	// impl.gameInit(1);
	// impl.dispatchCard(1);
	//
	// for (RoleGameInfo info : game.getRoleIdMap().values()) {
	// System.out.print(info.gameRoleId + "=");
	// for (int i : info.cards)
	// System.out.print(CardTools.toNum(i) + ",");
	// System.out.println();
	// }
	// for (int card : game.getLandlordCards())
	// System.out.print(CardTools.toNum(card) + ",");
	//
	// game.setCurrentRoleIdIndex(0);
	// System.out.println();
	//
	// impl.callLandlord(1, 0 + "", 0);
	// System.out.println("mul=" + game.getMultiple() + " score=" +
	// game.getCallLandlordScore());
	// impl.callLandlord(1, 1 + "", 0);
	// System.out.println("mul=" + game.getMultiple() + " score=" +
	// game.getCallLandlordScore());
	// impl.callLandlord(1, 2 + "", 0);
	// System.out.println("mul=" + game.getMultiple() + " score=" +
	// game.getCallLandlordScore());
	// game.setCurrentRoleIdIndex(0);
	// impl.callLandlord(1, 0 + "", 1);
	// System.out.println("mul=" + game.getMultiple() + " score=" +
	// game.getCallLandlordScore());
	// impl.callLandlord(1, 1 + "", 3);
	// System.out.println("mul=" + game.getMultiple() + " score=" +
	// game.getCallLandlordScore());
	// impl.callLandlord(1, 2 + "", 2);
	// System.out.println("mul=" + game.getMultiple() + " score=" +
	// game.getCallLandlordScore());
	//
	// }
	//
	// private static void test_send_card_clock() {
	//
	// }

}
