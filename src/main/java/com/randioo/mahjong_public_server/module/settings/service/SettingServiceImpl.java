package com.randioo.mahjong_public_server.module.settings.service;

import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.protocol.Settings.SettingsResponse;
import com.randioo.mahjong_public_server.protocol.Settings.SettingsShowResponse;
import com.randioo.randioo_server_base.service.ObserveBaseService;

@Service("settingService")
public class SettingServiceImpl extends ObserveBaseService implements SettingService {

	@Override
	public GeneratedMessage saveSettings(Role role, int volume, int musicVolume) {
		role.setVolume(volume);
		role.setMusicVolume(musicVolume);
		return SC.newBuilder().setSettingsResponse(SettingsResponse.newBuilder()).build();
	}

	@Override
	public GeneratedMessage getSettings(Role role) {
		if (role == null) {
			return SC
					.newBuilder()
					.setSettingsShowResponse(
							SettingsShowResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber())).build();
		}
		return SC
				.newBuilder()
				.setSettingsShowResponse(
						SettingsShowResponse.newBuilder().setMusicVolume(role.getMusicVolume())
								.setVolume(role.getVolume())).build();
	}

}
