package com.randioo.mahjong_public_server.module.race.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.mahjong_public_server.module.race.service.RaceService;
import com.randioo.mahjong_public_server.protocol.Race.RaceJoinRaceRequest;
import com.randioo.mahjong_public_server.protocol.Race.RaceJoinRaceResponse;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTAnnotation(RaceJoinRaceRequest.class)
public class RaceJoinAction implements IActionSupport {

	@Autowired
	private RaceService raceService;

	@Override
	public void execute(Object data, IoSession session) {
		RaceJoinRaceRequest message = (RaceJoinRaceRequest) data;

		RaceJoinRaceRequest request = (RaceJoinRaceRequest)data;
//		message = raceService.joinRace(role, request.getRace());
		
	}

}
