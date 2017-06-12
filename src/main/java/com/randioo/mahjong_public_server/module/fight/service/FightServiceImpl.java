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
import com.randioo.mahjong_public_server.comparator.HexCardComparator;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.SendCardTimeEvent;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.entity.po.cardlist.Gang;
import com.randioo.mahjong_public_server.entity.po.cardlist.Hu;
import com.randioo.mahjong_public_server.entity.po.cardlist.Kan;
import com.randioo.mahjong_public_server.entity.po.cardlist.Shun;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
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
import com.randioo.mahjong_public_server.protocol.Fight.FightHuResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightPengResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightReadyResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightSendCardResponse;
import com.randioo.mahjong_public_server.protocol.Fight.SCAgreeExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameDismiss;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList.Builder;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightStart;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTouchCard;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.CardTools;
import com.randioo.mahjong_public_server.util.Lists;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
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
	private HexCardComparator hexCardComparator;

	@Autowired
	private EventScheduler eventScheduler;

	@Override
	public void init() {
		List<Class<? extends CardList>> lists = new ArrayList<>();
		lists.add(Gang.class);
		lists.add(Kan.class);
		lists.add(Shun.class);
		lists.add(Hu.class);

		Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();
		for (Class<? extends CardList> clazz : lists)
			cardLists.put(clazz, ReflectUtils.newInstance(clazz));

		List<Class<? extends CardList>> checkCardListSeq = GameCache.getCheckCardListSequence();
		checkCardListSeq.add(Kan.class);
		checkCardListSeq.add(Gang.class);
		checkCardListSeq.add(Hu.class);

		// 各种转换方法
		GameCache.getParseCardListToProtoFunctionMap().put(Kan.class, new Function() {
			@Override
			public Object apply(Object... params) {
				return parsePeng((Kan) params[0]);
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
		GameCache.getAddProtoFunctionMap().put(Kan.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				PengData pengData = (PengData) params[1];
				builder.addPengData(pengData);
				return null;
			}
		});
		GameCache.getAddProtoFunctionMap().put(Gang.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				GangData gangData = (GangData) params[1];
				builder.addGangData(gangData);
				return null;
			}
		});
		GameCache.getAddProtoFunctionMap().put(Hu.class, new Function() {
			@Override
			public Object apply(Object... params) {
				SCFightNoticeChooseCardList.Builder builder = (Builder) params[0];
				HuData huData = (HuData) params[1];
				builder.addHuData(huData);
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

		roleGameInfo.ready = true;
		SC scFightReady = SC.newBuilder()
				.setSCFightReady(
						SCFightReady.newBuilder().setSeated(game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)))
				.build();
		// 通知其他所有玩家，该玩家准备完毕
		for (RoleGameInfo info : game.getRoleIdMap().values())
			SessionUtils.sc(info.roleId, scFightReady);

		// 检查是否全部都准备完毕,全部准备完毕
		if (this.checkAllReady(role.getGameId())) {
			// 开始游戏
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
		game.setCurrentRoleIdIndex(game.getRoleIdList().indexOf(game.getZhuangGameRoleId()));

		// 设置每个人的座位和卡牌的数量
		SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
		for (int i = 0; i < gameConfig.getMaxCount(); i++) {
			RoleGameInfo gameRoleInfo = game.getRoleIdMap().get(game.getRoleIdList().get(i));
			scFightStartBuilder.addPaiNum(PaiNum.newBuilder().setSeated(i).setNum(gameRoleInfo.cards.size()));
		}

		scFightStartBuilder.setTimes(game.getMultiple());
		// 发送给每个玩家
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			// 通知所有人游戏开始，并把自己的牌告诉场上的玩家
			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setSCFightStart(scFightStartBuilder.clone().addAllPai(roleGameInfo.cards)).build());
		}

		// 庄家发一张牌
		this.touchCard(game.getZhuangGameRoleId(), gameId);

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
		for (int i = 0; i < config.getMaxCount(); i++) {
			List<Integer> list = desktopCardMap.get(i);
			if (list == null) {
				list = new ArrayList<>();
				desktopCardMap.put(i, list);
			}
			list.clear();
		}

		// 临时列表清空
		for (int i = 0; i < 4; i++) {
			// 出的牌清空
			List<Integer> sendDesktopCardList = game.getSendDesktopCardMap().get(i);
			if (sendDesktopCardList == null) {
				sendDesktopCardList = new ArrayList<>();
				game.getSendDesktopCardMap().put(i, sendDesktopCardList);
			}
			sendDesktopCardList.clear();
		}

		// 临时列表清空
		List<Class<? extends CardList>> seq = GameCache.getCheckCardListSequence();
		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			Map<Class<? extends CardList>, List<CallCardList>> allCallCardList = game.getTempCardListMap().get(i);
			if (allCallCardList == null) {
				allCallCardList = new HashMap<Class<? extends CardList>, List<CallCardList>>();
				game.getTempCardListMap().put(i, allCallCardList);
			}

			for (Class<? extends CardList> clazz : seq) {
				List<CallCardList> list = allCallCardList.get(clazz);
				if (list == null) {
					list = new ArrayList<>();
					allCallCardList.put(clazz, list);
				}
				list.clear();
			}
		}

	}

	/**
	 * 检查庄家是否存在，不存在就赋值
	 * 
	 * @param gameId
	 */
	private void checkZhuang(int gameId) {
		Game game = this.getGameById(gameId);
		String zhuangGameRoleId = game.getZhuangGameRoleId();
		// 如果没有庄家，则随机一个
		if (zhuangGameRoleId == null) {
			int index = RandomUtils.getRandomNum(game.getRoleIdMap().size());
			String gameRoleId = game.getRoleIdList().get(index);
			game.setZhuangGameRoleId(gameRoleId);
		}

	}

	@Override
	public void dispatchCard(int gameId) {
		Game game = this.getGameById(gameId);

		// 赋值所有牌,然后随机一个个取
		List<Integer> copyCards = Lists.fillList(game.getRemainCards(), CardTools.CARDS);

		for (int i = 0; i < 13; i++) {
			// 每个人先发出13张牌
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				int index = RandomUtils.getRandomNum(copyCards.size());
				roleGameInfo.cards.add(copyCards.get(index));
				copyCards.remove(index);
			}
		}

		// 庄加一张牌
		RoleGameInfo zhuangRoleGameInfo = game.getRoleIdMap().get(game.getZhuangGameRoleId());
		zhuangRoleGameInfo.cards.add(copyCards.remove(0));

		// 每个玩家卡牌排序
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values())
			Collections.sort(roleGameInfo.cards, hexCardComparator);
	}

	@Override
	public GeneratedMessage exitGame(Role role) {
		Game game = this.getGameById(role.getGameId());
		if (game == null) {
			return SC.newBuilder()
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
				return SC.newBuilder()
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
			return SC.newBuilder()
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
					SessionUtils.sc(roleGameInfo.roleId,
							SC.newBuilder().setSCAgreeExitGame(SCAgreeExitGame.newBuilder().setName(info.roleId == 0
									? "ROBOT" + info.gameRoleId : RoleCache.getRoleById(info.roleId).getName())));
				}
				return SC.newBuilder().setFightAgreeExitGameResponse(
						FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.APPLY_REJECT.getNumber()))
						.build();
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
	public void touchCard(String gameRoleId, int gameId) {
		Game game = this.getGameById(gameId);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

		List<Integer> remainCards = game.getRemainCards();
		if (remainCards.size() > 0) {
			roleGameInfo.newCard = remainCards.remove(0);
		}

		// 通知该玩家摸到的是什么牌
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			int touchCard = 0;
			// 如果是玩家自己,则把牌赋值
			if (info.gameRoleId.equals(gameRoleId))
				touchCard = roleGameInfo.newCard;

			SessionUtils.sc(roleGameInfo.roleId,
					SC.newBuilder().setSCFightTouchCard(
							SCFightTouchCard.newBuilder().setGameRoleId(info.gameRoleId).setTouchCard(touchCard))
							.build());
		}
	}

	@Override
	public void sendCard(Role role, int card) {
		int gameId = role.getGameId();
		Game game = this.getGameById(gameId);
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightSendCardResponse(
							FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
		}
		String gameRoleId = game.getRoleIdList().get(game.getCurrentRoleIdIndex());
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		if (roleGameInfo.roleId != role.getRoleId()) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightSendCardResponse(
							FightSendCardResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
							.build());
		}

		// 自动出牌解除
		roleGameInfo.auto = 0;
		// 该玩家出牌
		this.gameRoleIdSendCard(card, gameId, gameRoleId);

		boolean hasGangPengHu = false;
		// 保存场上除了本人的杠碰胡
		for (int index = 0; index < game.getRoleIdList().size(); index++) {
			String tempGameRoleId = game.getRoleIdList().get(index);
			// 自己不能碰自己
			if (tempGameRoleId.equals(gameRoleId))
				continue;

			if (this.saveGangPengHu(gameId, index))
				hasGangPengHu = true;

		}

		// 如果没有可以杠碰胡则通知下一个人，如果有则发送通知并等待反馈
		if (!hasGangPengHu) {
			this.getNextIndex(gameId);
		} else {
			for (int i = 0; i < game.getRoleIdList().size(); i++) {
				SCFightNoticeChooseCardList.Builder scBuilder = SCFightNoticeChooseCardList.newBuilder();
				String tempGameRoleId = game.getRoleIdList().get(i);
				RoleGameInfo tempRoleGameInfo = game.getRoleIdMap().get(tempGameRoleId);

				// 自己不能碰杠胡自己
				if (tempGameRoleId.equals(gameRoleId))
					continue;

				for (Class<? extends CardList> clazz : GameCache.getCheckCardListSequence()) {
					List<CallCardList> list = game.getTempCardListMap().get(i).get(clazz);
					if (list.size() > 0) {
						Function func = GameCache.getParseCardListToProtoFunctionMap().get(clazz);
						Function func2 = GameCache.getAddProtoFunctionMap().get(clazz);
						for (CallCardList callCardList : list) {
							GeneratedMessage cardListProtoData = (GeneratedMessage) func.apply(callCardList.cardList);
							func.apply(scBuilder, cardListProtoData);
						}
					}
				}
				SessionUtils.sc(tempRoleGameInfo.roleId,
						SC.newBuilder().setSCFightNoticeChooseCardList(scBuilder).build());
			}
		}
	}

	@Override
	public void peng(Role role, int card) {

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightPengResponse(FightPengResponse.newBuilder()).build());

		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.peng(gameId, seatIndex);
	}

	@Override
	public void gang(Role role, int card) {

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightGangResponse(FightGangResponse.newBuilder()).build());

		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.gang(gameId, seatIndex);
	}

	@Override
	public void hu(Role role) {

		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightHuResponse(FightHuResponse.newBuilder()).build());

		int gameId = role.getGameId();
		Game game = GameCache.getGameMap().get(gameId);
		String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
		int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

		this.hu(gameId, seatIndex);
	}

	private void peng(int gameId, int seatIndex) {
		Game game = this.getGameById(gameId);
		int currentCard = this.getCurrentTargetCard(gameId);

	}

	private void gang(int gameId, int seatIndex) {
		Game game = this.getGameById(gameId);

	}

	private void hu(int gameId, int seatIndex) {
		Game game = this.getGameById(gameId);

	}

	private void gameOver(int gameId, String gameRoleId) {

	}

	/**
	 * 某玩家出牌
	 * 
	 * @param card
	 * @param gameId
	 * @param gameRoleId
	 */
	private void gameRoleIdSendCard(int card, int gameId, String gameRoleId) {
		Game game = this.getGameById(gameId);

		// 设置当前的牌
		List<Integer> sendDesktopCards = game.getSendDesktopCardMap().get(game.getCurrentRoleIdIndex());
		sendDesktopCards.add(card);

		// 通知所有人,此人出的牌
		this.sendAllGameRoleSC(game.getRoleIdMap(),
				SC.newBuilder().setSCFightSendCard(SCFightSendCard.newBuilder().setCard(card)).build());
	}

	/**
	 * 检查某玩家的杠碰胡
	 * 
	 * @param gameId
	 * @param currentCard
	 */
	private boolean saveGangPengHu(int gameId, int seatedIndex) {
		Game game = this.getGameById(gameId);
		String gameRoleId = game.getRoleIdList().get(seatedIndex);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
		int card = this.getCurrentTargetCard(gameId);

		CardSort cardSort = new CardSort(4);
		List<CardList> cardLists = new ArrayList<>();
		cardSort.fillCardSort(roleGameInfo.cards);

		// 清空所有临时数据
		this.clearAllTempMap(gameId);

		boolean hasGangPengHu = false;
		for (Class<? extends CardList> clazz : GameCache.getCheckCardListSequence()) {
			List<CallCardList> callCardLists = game.getTempCardListMap().get(seatedIndex).get(clazz);
			callCardLists.clear();
			CardList cardList = GameCache.getCardLists().get(clazz);
			cardList.check(cardLists, cardSort, card);
			for (CardList _cardList : cardLists) {
				CallCardList callCardList = new CallCardList();
				callCardList.call = false;
				callCardList.cardList = _cardList;

				callCardLists.add(callCardList);
				hasGangPengHu = true;
			}

			cardLists.clear();
		}

		return hasGangPengHu;
	}

	private void sendAllGameRoleSC(Map<String, RoleGameInfo> map, SC sc) {
		for (RoleGameInfo info : map.values()) {
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

	private int getNextIndex(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		game.setCurrentRoleIdIndex((index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
		return game.getCurrentRoleIdIndex();
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
		Game game = GameCache.getGameMap().get(gameId);
		int index = game.getCurrentRoleIdIndex();
		String gameRoleId = game.getRoleIdList().get(index);
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
			gangDataBuilder.addGang(gang.card);
		}
		return gangDataBuilder.build();
	}

	private PengData parsePeng(Kan kan) {
		PengData.Builder pengDataBuilder = PengData.newBuilder();
		for (int i = 0; i < 3; i++) {
			pengDataBuilder.addPeng(kan.card);
		}
		return pengDataBuilder.build();
	}

	private HuData parseHu(Hu hu) {
		return null;
	}

	private void clearAllTempMap(int gameId) {
		Game game = this.getGameById(gameId);

		for (Class<? extends CardList> clazz : GameCache.getCheckCardListSequence()) {
			for (int i = 0; i < game.getRoleIdList().size(); i++) {
				game.getTempCardListMap().get(clazz).get(i).clear();
			}
		}
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

	}

}
