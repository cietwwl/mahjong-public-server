package com.randioo.mahjong_public_server.module.race.service;

import java.util.Date;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.GlobleConstant;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.cache.local.RaceCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.mahjong_public_server.entity.po.Race;
import com.randioo.mahjong_public_server.entity.po.RaceRole;
import com.randioo.mahjong_public_server.entity.po.RaceStateInfo;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.RoleRaceInfo;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.module.race.RaceConstant;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchMineInfo;
import com.randioo.mahjong_public_server.protocol.Race.RaceJoinRaceResponse;
import com.randioo.mahjong_public_server.protocol.Race.SCRaceJoinRace;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.randioo_race_sdk.RaceExistResponse;
import com.randioo.mahjong_public_server.randioo_race_sdk.RandiooRaceWebSdk;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.lock.CacheLockUtil;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("raceService")
public class RaceServiceImpl extends ObserveBaseService implements RaceService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private LoginService loginService;

	private RandiooRaceWebSdk randiooRaceWebSdk;

	@Override
	public void raceInit(Role role) {
		createRaceInit(role);
	}

	@Override
	public void newRaceInit(Role role) {
		createRaceInit(role);
	}

	private void createRaceInit(Role role) {
		RoleRaceInfo roleRaceInfo = new RoleRaceInfo();
		roleRaceInfo.roleId = role.getRoleId();

		role.setRoleRaceInfo(roleRaceInfo);
	}

	@Override
	public void initService() {
		randiooRaceWebSdk = new RandiooRaceWebSdk();
		randiooRaceWebSdk.init();
		randiooRaceWebSdk.debug(GlobleMap.Boolean(GlobleConstant.ARGS_RACE_DEBUG));

		addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(RaceConstant.RACE_CREATE)) {
			Race race = (Race) args[0];
			Role role = (Role) args[1];

			this.createGame(race, role);
		}

		if (msg.equals(FightConstant.FIGHT_GAME_OVER)) {
			Game game = (Game) args[0];
			this.noticeRaceState(game, true);
		}

		if (msg.equals(FightConstant.ROUND_OVER)) {
			Game game = (Game) args[0];
			this.noticeRaceState(game, false);
		}

		if (msg.equals(RaceConstant.RACE_JOIN_GAME)) {
			Game game = (Game) args[0];
			this.noticeRaceState(game, false);
		}

	}

	private GameConfigData parseGameConfig(MahjongRaceConfigure configure) {
		GameConfigData gameConfig = GameConfigData.newBuilder().setRaceType(configure.raceType)
				.setMinStartScore(configure.minStartScore).setMaxCount(configure.maxCount)
				.setGangScore(configure.gangScore).setFlyScore(configure.catchScore)
				.setFlyCount(configure.endCatchCount).setEndTime(configure.endTime).build();

		return gameConfig;
	}

	private MahjongRaceConfigure parseMahjongRaceConfigure(RaceExistResponse raceExistResponse) {
		MahjongRaceConfigure c = new MahjongRaceConfigure();
		c.account = raceExistResponse.nickname;
		c.catchScore = raceExistResponse.flyScore;
		c.endCatchCount = raceExistResponse.fly;
		c.endTime = TimeUtils.get_HHmmss_DateFormat().format(new Date(raceExistResponse.endTime));
		c.minStartScore = raceExistResponse.difen;
		c.raceId = raceExistResponse.raceId;
		c.maxCount = 4;
		c.onlineCount = raceExistResponse.onlineCount;
		return c;
	}

	/**
	 * 通过id拿比赛
	 * 
	 * @param raceId
	 * @return
	 * @author wcy 2017年6月23日
	 */
	private Race getRaceById(int raceId) {
		Race race = RaceCache.getRaceMap().get(raceId);
		if (race == null) {
			race = new Race();
			race.setRaceId(raceId);
			RaceCache.getRaceMap().put(raceId, race);
		}
		return race;
	}

	@Override
	public void joinRace(Role role, int raceId) {
		loggerinfo(role, "race join " + raceId);
		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder().setRaceJoinRaceResponse(RaceJoinRaceResponse.newBuilder()).build());

		SCRaceJoinRace.Builder scRaceJoinRaceBuilder = SCRaceJoinRace.newBuilder();
		Lock lock = CacheLockUtil.getLock(Race.class, raceId);
		try {
			lock.lock();
			// 检查比赛网站是否有这个比赛
			RaceExistResponse raceExistResponse = randiooRaceWebSdk.exist(raceId);
			if (raceExistResponse == null) {
				// 比赛不存在
				scRaceJoinRaceBuilder.setErrorCode(ErrorCode.RACE_JOIN_FAILED.getNumber());
			} else {
				// 比赛存在则创建比赛
				Race race = RaceCache.getRaceMap().get(raceId);
				if (race == null) {
					MahjongRaceConfigure config = this.parseMahjongRaceConfigure(raceExistResponse);
					race = getRaceById(config.raceId);
					// 比赛配置赋值
					race.setConfig(config);
					GameConfigData gameConfigData = this.parseGameConfig(config);
					Game game = matchService.createGameByGameConfig(gameConfigData);
					this.bindGame2Race(race, game);
					// 比赛创建
					notifyObservers(RaceConstant.RACE_CREATE, race, role);
				}

				// 游戏已经存在
				Game game = GameCache.getGameMap().get(race.getGameId());
				// 先加入到队伍在加入到房间
				race.getRoleIdQueue().add(role.getRoleId());
				if (game.getRoleIdMap().size() < race.getConfig().maxCount) {
					int firstRoleId = race.getRoleIdQueue().remove(0);
					Role firstRole = loginService.getRoleById(firstRoleId);

					matchService.joinGame(firstRole, race.getGameId());
					String gameRoleId = matchService.getGameRoleId(game.getGameId(), firstRoleId);
					RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
					GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
					SessionUtils.sc(role.getRoleId(), SC.newBuilder()
							.setSCMatchMineInfo(SCMatchMineInfo.newBuilder().setGameRoleData(gameRoleData)).build());
					SessionUtils.sc(role.getRoleId(),
							SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build());
				}

				notifyObservers(RaceConstant.RACE_JOIN_GAME, game);
			}

		} catch (Exception e) {
			scRaceJoinRaceBuilder.setErrorCode(ErrorCode.RACE_JOIN_FAILED.getNumber());
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		SessionUtils.sc(role.getRoleId(), SC.newBuilder().setSCRaceJoinRace(scRaceJoinRaceBuilder).build());
	}

	private void bindGame2Race(Race race, Game game) {
		race.setGameId(game.getGameId());
		RaceCache.getGameRaceMap().put(race.getGameId(), race);
	}

	private void createGame(Race race, Role role) {
		int raceId = race.getRaceId();
		String account = role.getAccount();
		randiooRaceWebSdk.create(raceId, account);
	}

	private void noticeRaceState(Game game, boolean isFinal) {
		Race race = RaceCache.getGameRaceMap().get(game.getGameId());
		if (race == null)
			return;
		RaceStateInfo raceStateInfo = new RaceStateInfo();
		raceStateInfo.raceId = race.getRaceId();
		raceStateInfo.isFinal = isFinal;
		for (int roleId : race.getRoleIdQueue()) {
			Role role = (Role) loginService.getRoleById(roleId);
			if (role == null)
				continue;
			RaceRole raceRoleQueue = new RaceRole();
			raceRoleQueue.account = role.getAccount();
			raceRoleQueue.score = role.getRoleRaceInfo().totalRoundScore;
			raceStateInfo.queueAccount.add(raceRoleQueue);
		}

		for (String gameRoleId : game.getRoleIdList()) {
			RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
			Role roleSeat = loginService.getRoleById(roleGameInfo.roleId);

			// TODO
			RaceRole raceRole = new RaceRole();
			RoleRaceInfo roleRaceInfo = roleSeat.getRoleRaceInfo();
			raceRole.account = roleSeat.getAccount();
			raceRole.score = roleRaceInfo.totalRoundScore;
			raceStateInfo.accounts.add(raceRole);
		}

		randiooRaceWebSdk.state(raceStateInfo);
	}

	@Override
	public void kickRace(int raceId, String account) {
		Race race = RaceCache.getRaceMap().get(raceId);
		Role role = (Role) loginService.getRoleByAccount(account);

		if (race == null) {
			return;
		}

		Lock lock = CacheLockUtil.getLock(Race.class, raceId);
		try {
			lock.lock();
			System.out.println("kick");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

}
