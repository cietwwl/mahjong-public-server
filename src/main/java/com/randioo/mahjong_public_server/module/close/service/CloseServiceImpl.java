package com.randioo.mahjong_public_server.module.close.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.service.BaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("closeService")
public class CloseServiceImpl extends BaseService implements CloseService {

	@Autowired
	private LoginService loginService;

	@Autowired
	private GameDB gameDB;

	@Override
	public void asynManipulate(Role role) {
		logger.info("[account:" + role.getAccount() + ",name:" + role.getName() + "] manipulate");

		SessionCache.removeSessionById(role.getRoleId());
		role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());

		if (!gameDB.isUpdatePoolClose()) {
			gameDB.getUpdatePool().submit(new EntityRunnable<Role>(role) {
				@Override
				public void run(Role role) {
					roleDataCache2DB(role, true);
				}
			});
		}
	}

	@Override
	public void roleDataCache2DB(Role role, boolean mustSave) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
					+ "] save error");
		}

	}

}
