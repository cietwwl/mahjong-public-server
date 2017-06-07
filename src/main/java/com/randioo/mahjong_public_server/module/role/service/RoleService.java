package com.randioo.mahjong_public_server.module.role.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface RoleService extends ObserveBaseServiceInterface {

	void newRoleInit(Role role);

	public void roleInit(Role role);

	GeneratedMessage rename(Role role, String name);

	public void setHeadimgUrl(Role role, String headimgUrl);

	public void setRandiooMoney(Role role, int randiooMoney);
	
	GeneratedMessage getRoleData(String account);

}
