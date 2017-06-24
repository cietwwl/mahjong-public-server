package com.randioo.mahjong_public_server.module.race.service;

import java.util.List;
import java.util.concurrent.locks.Lock;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.cache.local.RaceCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.mahjong_public_server.entity.po.Race;
import com.randioo.mahjong_public_server.entity.po.RaceRole;
import com.randioo.mahjong_public_server.entity.po.RaceStateInfo;
import com.randioo.mahjong_public_server.entity.po.Rank;
import com.randioo.mahjong_public_server.entity.po.RoleRaceInfo;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.module.race.RaceConstant;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.randioo_race_sdk.RaceExistResponse;
import com.randioo.mahjong_public_server.randioo_race_sdk.RandiooRaceWebSdk;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.lock.CacheLockUtil;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;

@Service("raceService")
public class RaceServiceImpl extends ObserveBaseService implements RaceService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private LoginService loginService;

	private RandiooRaceWebSdk randiooRaceWebSdk;

	@Override
	public void raceInit(Role role) {
		RoleRaceInfo roleRaceInfo = new RoleRaceInfo();
		roleRaceInfo.roleId = role.getRoleId();

		role.setRoleRaceInfo(roleRaceInfo);
	}

	@Override
	public void initService() {
		randiooRaceWebSdk = new RandiooRaceWebSdk();
		randiooRaceWebSdk.init();
		randiooRaceWebSdk.debug(GlobleConfig.Boolean("racedebug"));

		addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		super.update(observer, msg, args);

		if (msg.equals(FightConstant.ROUND_OVER)) {
			@SuppressWarnings("unchecked")
			List<Rank> roleList = (List<Rank>) args[0];
			JSONObject json = new JSONObject(roleList);

			// List<Map.Entry<Integer,Integer>> list = new
			// ArrayList<Map.Entry<Integer,Integer>>(resultMap.entrySet());
			// Collections.sort(list,new
			// Comparator<Map.Entry<Integer,Integer>>() {
			// //升序排序
			// public int compare(Entry<Integer, Integer> o1,
			// Entry<Integer, Integer> o2) {
			// return o2.getValue()-o1.getValue();
			// }
			//
			// });

		}

		if (msg.equals(RaceConstant.RACE_CREATE)) {
			Race race = (Race) args[0];
			Role role = (Role) args[1];

			this.createGame(race, role);
		}

		if (msg.equals(RaceConstant.RACE_JOIN_GAME)) {
			Race race = (Race) args[0];

			this.joinGame(race);
		}
	}

	@Override
	public Message createRace(MahjongRaceConfigure configure) {

		GameConfigData gameConfigData = this.parseGameConfig(configure);

		Game game = matchService.createGameByGameConfig(gameConfigData);

		// 新建一场比赛
		Race race = createRace(game.getGameId(), configure);

		Message message = new Message();
		message.setType((short) 1);
		message.putString(configure.account);
		message.putInt(1);// success
		message.putInt(race.getGameId());

		return message;
	}

	@Override
	public void raceEnd(int raceId) {
		RaceCache.getRaceMap().remove(raceId);
	}

	private GameConfigData parseGameConfig(MahjongRaceConfigure configure) {
		GameConfigData gameConfig = GameConfigData.newBuilder().setZhuahu(configure.zhuahu)
				.setRaceType(configure.raceType).setMinStartScore(configure.minStartScore)
				.setMaxCount(configure.maxCount).setGangScore(configure.gangScore).setGangkai(configure.gangkai)
				.setCatchScore(configure.catchScore).setEndCatchCount(configure.endCatchCount)
				.setEndTime(configure.endTime).build();

		return gameConfig;
	}

	private MahjongRaceConfigure parseMahjongRaceConfigure(RaceExistResponse raceExistResponse) {
		return new MahjongRaceConfigure();
	}

	private Race createRace(int gameId, MahjongRaceConfigure configure) {
		// 新建一场比赛
		Race race = new Race();
		race.setRaceName(configure.raceName);
		race.setEndTime(configure.endTime);
		race.setGameId(gameId);
		RaceCache.getRaceMap().put(race.getGameId(), race);

		return race;
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
		Lock lock = CacheLockUtil.getLock(Race.class, raceId);
		try {
			lock.lock();
			// 检查比赛网站是否有这个比赛
			RaceExistResponse raceExistResponse = randiooRaceWebSdk.exist(raceId);
			if (raceExistResponse == null) {
				// 比赛不存在
			} else {
				// 比赛存在则创建比赛
				Race race = RaceCache.getRaceMap().get(raceId);
				if (race == null) {
					race = getRaceById(raceId);
					MahjongRaceConfigure config = this.parseMahjongRaceConfigure(raceExistResponse);
					// 比赛配置赋值
					race.config = config;
					GameConfigData gameConfigData = this.parseGameConfig(config);
					Game game = matchService.createGameByGameConfig(gameConfigData);
					this.bindGame2Race(race, game);
					// 比赛创建
					notifyObservers(RaceConstant.RACE_CREATE, race, role);
				}

				// 游戏已经存在
				Game game = GameCache.getGameMap().get(race.getGameId());

				// 如果比赛的人小于最大人数并且队伍长度不是0
				if (game.getRoleIdMap().size() < race.config.maxCount && race.getRoleIdQueue().size() == 0) {
					matchService.joinGame(role, race.getGameId());
				} else {
					race.getRoleIdQueue().add(role.getRoleId());
				}
				notifyObservers(RaceConstant.RACE_JOIN_GAME, race);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		// Game game = GameCache.getGameMap().get(gameId);
		// synchronized (game) {
		// if (game.getRoleIdMap().keySet().size() < 4) {
		// matchService.joinGame(role, gameId);
		// } else {
		// List<Integer> waitList =
		// RaceCache.getRaceMap().get(raceId).getRoleIdQueue();
		// if (game.getRoleIdMap().keySet().size() + waitList.size() <
		// game.getGameConfig().getMaxCount()) {
		// waitList.add(role.getRoleId());
		// }
		// }
		// }

	}

	private void bindGame2Race(Race race, Game game) {
		race.setGameId(game.getGameId());
	}

	private void createGame(Race race, Role role) {
		int raceId = race.getRaceId();
		String account = role.getAccount();
		randiooRaceWebSdk.create(raceId, account);
	}

	private void joinGame(Race race) {
		RaceStateInfo raceStateInfo = new RaceStateInfo();
		for (int roleId : race.getRoleIdQueue()) {
			Role role = (Role) loginService.getRoleById(roleId);
			RaceRole raceRoleQueue = new RaceRole();
			raceRoleQueue.account = role.getAccount();
			raceRoleQueue.score = role.getRoleRaceInfo().totalRoundScore;
			raceStateInfo.queueAccount.add(raceRoleQueue);
		}
		
		Game game = GameCache.getGameMap().get(race.getGameId());
		
	}

}
