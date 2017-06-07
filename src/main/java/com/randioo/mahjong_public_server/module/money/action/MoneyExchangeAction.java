package com.randioo.mahjong_public_server.module.money.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.money.service.MoneyExchangeService;
import com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTAnnotation(MoneyExchangeRequest.class)
public class MoneyExchangeAction implements IActionSupport {

	@Autowired
	private MoneyExchangeService moneyExchangeService;

	@Override
	public void execute(Object data, IoSession session) {
		MoneyExchangeRequest request = (MoneyExchangeRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage sc = moneyExchangeService.moneyExchange(role, request.getAdd(), request.getNum());
		SessionUtils.sc(session, sc);
	}

}
