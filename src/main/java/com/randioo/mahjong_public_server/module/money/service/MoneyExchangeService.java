package com.randioo.mahjong_public_server.module.money.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface MoneyExchangeService extends ObserveBaseServiceInterface {

	GeneratedMessage moneyExchange(Role role, boolean add, int num);
	
	public  boolean exchangeMoney(Role role, int money, boolean add);

	void newRoleInit(Role role);

}
