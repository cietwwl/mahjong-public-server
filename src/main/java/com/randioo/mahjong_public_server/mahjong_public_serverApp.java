package com.randioo.mahjong_public_server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.randioo.mahjong_public_server.handler.BackgroundServerHandler;
import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.Race.RaceJoinRaceRequest;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.config.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.entity.GlobalConfigFunction;
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
		GlobleConfig.initParam(new GlobalConfigFunction() {

			@Override
			public void init(Map<String, Object> map, List<String> list) {
				{
					int index = list.indexOf("artifical");
					if (index != -1) {
						boolean ai = Boolean.parseBoolean(list.get(index + 1));
						map.put("artifical", ai);
					}
				}
				{
					int index = list.indexOf("dispatch");
					if (index != -1) {
						boolean dispatch = Boolean.parseBoolean(list.get(index + 1));
						map.put("dispatch", dispatch);
					}
				}
				{
					int index = list.indexOf("racedebug");
					if (index != -1) {
						boolean dispatch = Boolean.parseBoolean(list.get(index + 1));
						map.put("racedebug", dispatch);
					}
				}

			}
		});
		GlobleConfig.init(args);
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		 try {
//			CS.newBuilder().setRaceJoinRaceRequest(RaceJoinRaceRequest.newBuilder().setRace("123").setRace2("101")).build().writeDelimitedTo(output);
//			byte[] bytes = output.toByteArray();
//			System.out.println(bytes);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		};
		 

		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		((GameServerInit) SpringContext.getBean("gameServerInit")).start();

		BackgroundServerHandler handler = SpringContext.getBean("backgroundServerHandler");
		WanServer.startServer(new ProtocolCodecFilter(new MessageCodecFactory()), handler, new InetSocketAddress(
				GlobleConfig.Int(GlobleEnum.PORT) + 10000));

		GlobleConfig.set(GlobleEnum.LOGIN, true);

		// ((Test)SpringContext.getBean("test")).fuck();

	}

}
