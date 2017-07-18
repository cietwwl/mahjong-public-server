package com.randioo.randioo_server_base.heart;

import java.io.InputStream;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.google.protobuf.GeneratedMessage;

public abstract class HeartMessageFactory implements KeepAliveMessageFactory {

	public abstract GeneratedMessage getResponseMessage(IoSession session);

	public abstract GeneratedMessage getHeartSCMessage(IoSession session);

	public abstract String getHeartRequestName();

	public abstract String getHeartResponseName();

	@Override
	public boolean isRequest(IoSession arg0, Object arg1) {
		InputStream input = (InputStream)arg1;
		
		GeneratedMessage message = (GeneratedMessage) arg1;
		return message.toString().contains(getHeartRequestName());
	}

	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		GeneratedMessage message = (GeneratedMessage) arg1;
		return message.toString().contains(getHeartResponseName());
	}

	@Override
	public Object getRequest(IoSession arg0) {
		return getHeartSCMessage(arg0);
	}

	@Override
	public Object getResponse(IoSession arg0, Object arg1) {
		return getResponseMessage(arg0);
	}

}
