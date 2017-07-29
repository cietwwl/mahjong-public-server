package com.randioo.mahjong_public_server.handler;

import java.io.InputStream;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.close.service.CloseService;
import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.Heart.SCHeart;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.error.ActionSupportFakeException;
import com.randioo.randioo_server_base.handler.GameServerHandlerAdapter;
import com.randioo.randioo_server_base.log.HttpLogUtils;

@Component
public class GameServerHandler extends GameServerHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private CloseService closeService;

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("roleId:" + session.getAttribute("roleId") + " sessionCreated");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("roleId:" + session.getAttribute("roleId") + " sessionOpened");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("roleId:" + session.getAttribute("roleId") + " sessionClosed");

		Role role = (Role) RoleCache.getRoleBySession(session);
		try {
			if (role != null) {
				IoSession currentSession = SessionCache.getSessionById(role.getRoleId());
				if (session != currentSession)
					return;

				closeService.asynManipulate(role);
			}

		} catch (Exception e) {
			logger.error("sessionClosed error:", e);
		} finally {
			session.close(true);
		}

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable e) throws Exception {
		logger.error("", e);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object messageObj) throws Exception {
		InputStream input = (InputStream) messageObj;

		CS message = null;
		try {
			message = CS.parseDelimitedFrom(input);
			System.out.println(message);
			logger.warn(getMessage(message, session));
			try{
			    actionDispatcher(message, session);			    
			}catch(ActionSupportFakeException e){
			    e.printStackTrace();
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (message.toString().contains(SCHeart.class.getSimpleName()))
			return;
		logger.warn(getMessage(message, session));
	}

	private String getMessage(Object message, IoSession session) {
		Integer roleId = (Integer) session.getAttribute("roleId");

		Role role = null;
		if (roleId != null) {
			role = (Role) RoleCache.getRoleById(roleId);
		}
		return HttpLogUtils.role(role, message);

	}

}
