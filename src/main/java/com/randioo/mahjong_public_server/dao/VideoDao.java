package com.randioo.mahjong_public_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.bo.VideoData;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;

@MyBatisGameDaoAnnotation
public interface VideoDao extends BaseDao<Role> {

	List<VideoData> get(@Param("roleId") int id);
	
	void insert(VideoData v);
	
	VideoData getById(@Param("ID") int id);
}
