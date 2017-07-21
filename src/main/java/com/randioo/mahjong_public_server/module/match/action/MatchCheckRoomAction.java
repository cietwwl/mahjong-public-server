package com.randioo.mahjong_public_server.module.match.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.Match.MatchCheckRoomRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(MatchCheckRoomRequest.class)
public class MatchCheckRoomAction implements IActionSupport {
	@Autowired
	private MatchService matchService;

	@Override
	public void execute(Object data, IoSession session) {
		// TODO Auto-generated method stub
		MatchCheckRoomRequest request = (MatchCheckRoomRequest) data;

		matchService.checkRoom(request.getRoomId(), session);
	}

}
