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

	/**
	 * 增加燃点币
	 * 
	 * @param role
	 * @param money
	 * @return
	 * @author wcy 2017年7月14日
	 */
	boolean addRandiooMoney(Role role, int money);

	/**
	 * 
	 * @param role
	 * @author wcy 2017年7月14日
	 */
	void initRoleDataFromHttp(Role role);

}
