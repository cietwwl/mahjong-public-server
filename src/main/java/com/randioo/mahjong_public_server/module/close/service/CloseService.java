package com.randioo.mahjong_public_server.module.close.service;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.BaseServiceInterface;

public interface CloseService extends BaseServiceInterface {
	public void asynManipulate(Role role);

	void roleDataCache2DB(Role role, boolean mustSave);
}
