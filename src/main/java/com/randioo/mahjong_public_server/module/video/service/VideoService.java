package com.randioo.mahjong_public_server.module.video.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface VideoService extends ObserveBaseServiceInterface{

	GeneratedMessage videoGet(Role role);

	GeneratedMessage videoGetById(int id);

	GeneratedMessage videoGetByRound(int id, int round);
}
