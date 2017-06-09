package com.randioo.randioo_server_base.net;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.navigation.Navigation;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.template.IActionSupport;

public class BackgroundServerHandlerAdapter extends IoHandlerAdapter {
	private static final String PREFIX = "server_";

	protected void actionDispatcher(Object messageObj, IoSession session) {
		Message message = (Message) messageObj;
		String protocolKey = PREFIX + message.getType();
		IActionSupport action = Navigation.getAction(protocolKey);
		try {
			action.execute(message, session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
