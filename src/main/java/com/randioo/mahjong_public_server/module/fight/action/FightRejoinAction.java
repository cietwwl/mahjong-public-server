package com.randioo.mahjong_public_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.mahjong_public_server.protocol.Fight.FightRejoinRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightRejoinRequest.class)
public class FightRejoinAction implements IActionSupport {

	@Autowired
	private FightService fightService;
	
	@Override
	public void execute(Object data, IoSession session) {
		Role role = (Role) RoleCache.getRoleBySession(session);
		fightService.rejoin(role);
	}

}