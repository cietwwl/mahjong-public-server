package com.randioo.mahjong_public_server.util;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.bo.VideoData;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.protocol.Entity.GameVideoData;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.cache.RoleCache;

public class VideoUtils {
	// public static void parseVideo(VideoData data){
	// try {
	// return video.parseFrom(data.getVideo());
	// } catch (InvalidProtocolBufferException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	//
	// }
	public static void parseVideoData(VideoData data) {
		try {
			if (data.getScList() == null) {
				List<List<SC>> scList = new ArrayList<>();
				data.setScList(scList);
			}
			GameVideoData gameVideoData = GameVideoData.parseFrom(data
					.getData());
			for (int i = 0; i < gameVideoData.getRoundVideoDataCount(); i++) {
				List<SC> list = new ArrayList<>();
				for (ByteString byteString : gameVideoData.getRoundVideoData(i)
						.getScList()) {
					list.add(SC.parseFrom(byteString));
				}
				data.getScList().add(list);
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void toVideoData(RoleGameInfo info, byte[] data) {
		VideoData videoData = info.videoData;
		videoData.setRoleId(info.roleId);
		Role role = null;
		if (info.roleId != 0) {
			role = (Role) RoleCache.getRoleMap().get(info.roleId);
			videoData.setGameId(role.getGameId());
		}
		videoData.setData(data);
	}
}
