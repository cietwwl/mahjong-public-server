package com.randioo.mahjong_public_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;

@MyBatisGameDaoAnnotation
public interface RoleDao extends BaseDao<Role> {
	Role get(@Param("roleId") int id);

	Role getRoleByAccount(@Param("account") String account);

	List<String> getAllAccounts();

	List<String> getAllNames();

	public Integer getMaxRoleId();

	void updateLimit(@Param("moneyExchangeNum") int moneyExchangeNum,
			@Param("moneyExchangeTimeStr") String moneyExchangeTimeStr, @Param("roleId") int id);

	void update(Role role);
}
