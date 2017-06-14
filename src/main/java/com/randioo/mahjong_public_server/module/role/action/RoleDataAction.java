package com.randioo.mahjong_public_server.module.role.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.role.service.RoleService;
import com.randioo.mahjong_public_server.protocol.Role.RoleGetRoleDataRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTAnnotation(RoleGetRoleDataRequest.class)
public class RoleDataAction implements IActionSupport {

	@Autowired
	private RoleService roleService;

	@Override
	public void execute(Object data, IoSession session) {
		RoleGetRoleDataRequest request = (RoleGetRoleDataRequest) data;
		String account = ((Role) RoleCache.getRoleBySession(session)).getAccount();
		GeneratedMessage sc = roleService.getRoleData(account);
		SessionUtils.sc(session, sc);
	}

}
