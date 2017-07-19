package com.randioo.mahjong_public_server.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.springframework.stereotype.Component;

import com.randioo.randioo_server_base.utils.TimeUtils;

@Component
public class HeartTimeOutHandler implements KeepAliveRequestTimeoutHandler {

	@Override
	public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession arg1) throws Exception {
		System.out.println(TimeUtils.getDetailTimeStr() + " keepAliveRequestTimedOut");
	}

}
