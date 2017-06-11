package com.randioo.mahjong_public_server.module.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.comparator.HexCardComparator;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.SendCardTimeEvent;
import com.randioo.mahjong_public_server.entity.po.TargetCard;
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
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameDismiss;
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

		// 当前目标牌重置
		TargetCard targetCard = game.getTargetCard();
		targetCard.roleIdIndex = -1;
		targetCard.card = 0;
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
		// 检查场上除了本人有没有杠碰胡
		for (int i = 0; i < game.getRoleIdList().size(); i++) {
			String tempGameRoleId = game.getRoleIdList().get(i);
			// 自己不能碰自己
			if (tempGameRoleId.equals(gameRoleId))
				continue;

			this.checkGangPengHuByTargetCard(gameId, card, i);
		}
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
		TargetCard targetCard = game.getTargetCard();
		targetCard.roleIdIndex = game.getRoleIdList().indexOf(gameRoleId);
		targetCard.card = card;

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
	private void checkGangPengHuByTargetCard(int gameId, int card, int seatedIndex) {
		Game game = this.getGameById(gameId);
		String gameRoleId = game.getRoleIdList().get(seatedIndex);
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

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

	public static void main(String[] args) {

	}

}
