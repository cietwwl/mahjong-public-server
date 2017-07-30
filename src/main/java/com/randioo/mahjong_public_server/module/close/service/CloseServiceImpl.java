package com.randioo.mahjong_public_server.module.close.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.dao.RoleDao;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.service.BaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.utils.SaveUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("closeService")
public class CloseServiceImpl extends BaseService implements CloseService {

	@Autowired
	private LoginService loginService;

	@Autowired
	private FightService fightService;

	@Autowired
	private MatchService matchService;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private RoleDao roleDao;

	@Override
	public void asynManipulate(Role role) {
		if (role == null)
			return;

		loggerinfo(role, "[account:" + role.getAccount() + ",name:" + role.getName() + "] manipulate");

		role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());
		matchService.serviceCancelMatch(role);
		fightService.disconnect(role);

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
			if (SaveUtils.needSave(role, mustSave)) {
				roleDao.update(role);
			}
		} catch (Exception e) {
			e.printStackTrace();
			loggererror(role, "id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
					+ "] save error", e);
		}
	}

}
