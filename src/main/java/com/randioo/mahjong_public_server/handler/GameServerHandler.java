package com.randioo.mahjong_public_server.handler;

import java.io.InputStream;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.close.service.CloseService;
import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.navigation.Navigation;
import com.randioo.randioo_server_base.net.GameServerHandlerAdapter;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.TimeUtils;

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
			if (role != null)
				closeService.asynManipulate(role);

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

	}

	@Override
	public void messageReceived(IoSession session, Object messageObj) throws Exception {

		InputStream input = (InputStream) messageObj;

		try {
			CS message = CS.parseDelimitedFrom(input);
			actionDispatcher(message, session);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (input != null) {
				input.close();
			}
		}

	}

	/**
	 * 消息事件分发
	 * 
	 * @param message
	 * @param session
	 * @author wcy 2017年1月3日
	 */
	private void actionDispatcher(GeneratedMessage message, IoSession session) {
		Map<FieldDescriptor, Object> allFields = message.getAllFields();
		for (Map.Entry<FieldDescriptor, Object> entrySet : allFields.entrySet()) {

			String name = entrySet.getKey().getName();
			IActionSupport action = Navigation.getAction(name);
			try {
				action.execute(entrySet.getValue(), session);
			} catch (Exception e) {
				logger.error("Fake protocol：" + name, e);
				session.close(true);
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.info(getMessage(message, session));
	}

	private String getMessage(Object message, IoSession session) {
		Integer roleId = (Integer) session.getAttribute("roleId");
		String roleAccount = null;
		String roleName = null;
		if (roleId != null) {
			Role role = (Role) RoleCache.getRoleById(roleId);
			if (role != null) {
				roleAccount = role.getAccount();
				roleName = role.getName();
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(TimeUtils.getDetailTimeStr()).append(" [roleId:").append(roleId).append(",account:")
				.append(roleAccount).append(",name:").append(roleName).append("] ").append(message);
		String output = sb.toString();
		if (output.length() < 120) {
			output = output.replaceAll("\n", " ").replace("\t", " ").replace("  ", "");
		}

		return output;
	}

}
