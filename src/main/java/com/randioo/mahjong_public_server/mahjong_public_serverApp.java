package com.randioo.mahjong_public_server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.keepalive.KeepAliveFilter;

import com.randioo.mahjong_public_server.handler.HeartTimeOutHandler;
import com.randioo.mahjong_public_server.httpserver.LiteHttpServer;
import com.randioo.mahjong_public_server.httpserver.LiteServlet;
import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.Heart.HeartRequest;
import com.randioo.mahjong_public_server.protocol.Heart.HeartResponse;
import com.randioo.mahjong_public_server.protocol.Heart.SCHeart;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.config.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.entity.GlobalConfigFunction;
import com.randioo.randioo_server_base.heart.ProtoHeartFactory;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.log.HttpLogUtils;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;

/**
 * Hello world!
 *
 */
public class mahjong_public_serverApp {
	public static void main(String[] args) {

		StringUtils.printArgs(args);

		GlobleConfig.initParam(new GlobalConfigFunction() {

			@Override
			public void init(Map<String, Object> map, List<String> list) {
				String[] params = { "artifical", "dispatch", "racedebug", "matchai" };
				for (String param : params) {
					GlobleConfig.initBooleanValue(param, list);
				}
			}
		});
		GlobleConfig.init(args);
		HttpLogUtils.setProjectName("public_majiang" + GlobleConfig.Int(GlobleEnum.PORT));

		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		GameServerInit gameServerInit = ((GameServerInit) SpringContext.getBean("gameServerInit"));

		gameServerInit.setKeepAliveFilter(new KeepAliveFilter(new ProtoHeartFactory(CS.class, SC.class),
				IdleStatus.READER_IDLE, (HeartTimeOutHandler) SpringContext.getBean("heartTimeOutHandler"), 3, 5));
		gameServerInit.start();

		LiteHttpServer server = new LiteHttpServer();
		server.setPort(GlobleConfig.Int(GlobleEnum.PORT) + 10000);
		server.setRootPath("/majiang");
		server.addLiteServlet("/kickRace", (LiteServlet) SpringContext.getBean("startServlet"));
		try {
			server.init();
		} catch (IOException e) {
			e.printStackTrace();
		}

		GlobleConfig.set(GlobleEnum.LOGIN, true);

		// ((Test)SpringContext.getBean("test")).fuck();

	}
}
