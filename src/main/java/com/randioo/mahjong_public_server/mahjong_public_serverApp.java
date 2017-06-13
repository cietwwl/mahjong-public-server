package com.randioo.mahjong_public_server;

import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.handler.BackgroundServerHandler;
import com.randioo.mahjong_public_server.test.Test;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.config.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.net.WanServer;
import com.randioo.randioo_server_base.protocol.randioo.MessageCodecFactory;
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
		GlobleConfig.init(args);

		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		((GameServerInit) SpringContext.getBean("gameServerInit")).start();

		BackgroundServerHandler handler = SpringContext.getBean("backgroundServerHandler");
		WanServer.startServer(new ProtocolCodecFilter(new MessageCodecFactory()), handler, new InetSocketAddress(
				GlobleConfig.Int(GlobleEnum.PORT) + 10000));

		GlobleConfig.set(GlobleEnum.LOGIN, true);

		((Test)SpringContext.getBean("test")).fuck();

	}

}
