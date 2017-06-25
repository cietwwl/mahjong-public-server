package com.randioo.mahjong_public_server.module.race.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.race.service.RaceService;
import com.randioo.mahjong_public_server.protocol.Race.RaceJoinRaceRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(RaceJoinRaceRequest.class)
public class RaceJoinAction implements IActionSupport {

	@Autowired
	private RaceService raceService;

	@Override
	public void execute(Object data, IoSession session) {
		RaceJoinRaceRequest request = (RaceJoinRaceRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		int raceId = request.getRaceId();
		
		raceService.joinRace(role, raceId);
	}

}
