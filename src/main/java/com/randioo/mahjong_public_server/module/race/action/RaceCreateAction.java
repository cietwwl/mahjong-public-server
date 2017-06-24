package com.randioo.mahjong_public_server.module.race.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.mahjong_public_server.module.race.service.RaceService;
import com.randioo.randioo_server_base.annotation.PTStringAnnotation;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTStringAnnotation("server_1")
public class RaceCreateAction implements IActionSupport {

	@Autowired
	private RaceService raceService;

	@Override
	public void execute(Object data, IoSession session) {
		Message message = (Message) data;

		MahjongRaceConfigure configure = new MahjongRaceConfigure();
		configure.raceType = message.getInt();
//		configure.raceName = message.getString();
		configure.maxCount = message.getInt();
		configure.endTime = message.getString();
		configure.gangScore = message.getInt();
		configure.zhuahu = message.getBoolean();
		configure.endCatchCount = message.getInt();
		configure.catchScore = message.getInt();
		configure.gangkai = message.getBoolean();
		configure.minStartScore = message.getInt();
        configure.account = message.getString();
		message = raceService.createRace(configure);
		
		SessionUtils.sc(session, message);
	}

}
