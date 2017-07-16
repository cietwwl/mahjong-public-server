package com.randioo.mahjong_public_server.util.vote;

import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.template.Function;

public class AllVoteExceptApplyerStrategy<T> implements VoteStrategy<T> {

	@Override
	public void vote(Map<T, Boolean> voteMap, int totalCount, VoteListener listener, Function generateFunction,
			T applyer) {
		boolean hasResult = false;
		if (voteMap.size() == totalCount - 1) {
			generateFunction.apply();
			if (checkResult(voteMap))
				listener.pass();
			else
				listener.fail();

		} else {
			// WAIT_VOTE: {
			// for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			// // 申请人就跳过
			// if (roleGameInfo.gameRoleId.equals(applyExitGameRoleId)) {
			// continue;
			// }
			// // 投票中没有此人就检查连接,没断就返回
			// if (!game.getVoteMap().containsKey(roleGameInfo.gameRoleId)) {
			// IoSession session =
			// SessionCache.getSessionById(roleGameInfo.roleId);
			// if (session == null || !session.isConnected()) {
			// voteMap.put(roleGameInfo.gameRoleId,
			// FightVoteApplyExit.VOTE_AGREE);
			// } else {
			// break WAIT_VOTE;
			// }
			// }
			// }
			// voteResult = this.checkVoteContinueGame(game) ?
			// FightConstant.GAME_CONTINUE : FightConstant.GAME_OVER;
			// this.generateApplyExitId(game);
			// }
			
			

//			WAIT_VOTE: {
//				for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
//					// 申请人就跳过
//					if (roleGameInfo.gameRoleId.equals(applyExitGameRoleId)) {
//						continue;
//					}
//					// 投票中没有此人就检查连接,没断就返回
//					if (!game.getVoteMap().containsKey(roleGameInfo.gameRoleId)) {
//						IoSession session = SessionCache.getSessionById(roleGameInfo.roleId);
//						if (session == null || !session.isConnected()) {
//							voteMap.put(roleGameInfo.gameRoleId, FightVoteApplyExit.VOTE_AGREE);
//						} else {
//							break WAIT_VOTE;
//						}
//					}
//				}
//				voteResult = this.checkVoteContinueGame(game) ? FightConstant.GAME_CONTINUE : FightConstant.GAME_OVER;
//				generateFunction.apply();
//			}
		}
	}

	private boolean checkResult(Map<T, Boolean> voteMap) {
		boolean checkResult = false;
		for (boolean result : voteMap.values()) {
			checkResult &= result;
		}
		return checkResult;
	}

}
