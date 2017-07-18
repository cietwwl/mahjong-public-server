package com.randioo.mahjong_public_server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

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
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.log.HttpLogUtils;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * Hello world!
 *
 */
public class mahjong_public_serverApp {
	public static void main(String[] args) {
		
		// CS cs =
		// CS.newBuilder().setHeartRequest(HeartRequest.newBuilder()).build();
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// try {
		// cs.writeDelimitedTo(out);
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// byte[] bytes = out.toByteArray();
		//
		// IoBuffer buffer = IoBuffer.allocate(bytes.length);
		// buffer.order(ByteOrder.LITTLE_ENDIAN);
		// buffer.put(bytes);
		// byte[] b = buffer.array();
		// for(byte bs :b){
		// System.out.print(bs +" ");
		// }System.out.println();
		
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
		gameServerInit.setKeepAliveFilter(new KeepAliveFilter(new KeepAliveMessageFactory() {

			@Override
			public boolean isResponse(IoSession iosession, Object obj) {
				if (obj instanceof SC) {
					SC sc = (SC) obj;
					System.out.println(sc);
					if (sc.toString().contains(HeartResponse.class.getSimpleName()))
						return true;
				} else {
					InputStream input = (InputStream) obj;
					input.mark(0);
					
					try {
						SC sc = SC.parseDelimitedFrom(input);
						if (sc.toString().contains(HeartResponse.class.getSimpleName()))
							return true;

					} catch (Exception e) {

					} finally {
						try {
							if (input != null)
								input.reset();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return false;
			}

			@Override
			public boolean isRequest(IoSession iosession, Object obj) {
				if (obj instanceof SC) {
					System.out.println(((SC) obj));
					if (((SC) obj).toString().contains(SCHeart.class.getSimpleName()))
						return true;
				} else {
					InputStream input = (InputStream) obj;
					input.mark(0);

					try {
						CS cs = CS.parseDelimitedFrom(input);
						if (cs.toString().contains(HeartRequest.class.getSimpleName()))
							return true;

					} catch (Exception e) {

					} finally {
						try {
							if (input != null)
								input.reset();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return false;
			}

			@Override
			public Object getResponse(IoSession iosession, Object obj) {
				return SC.newBuilder().setHeartResponse(HeartResponse.newBuilder()).build();
			}

			@Override
			public Object getRequest(IoSession iosession) {
				return SC.newBuilder().setSCHeart(SCHeart.newBuilder()).build();
			}
		}, IdleStatus.READER_IDLE, new KeepAliveRequestTimeoutHandler() {

			@Override
			public void keepAliveRequestTimedOut(KeepAliveFilter keepalivefilter, IoSession iosession) throws Exception {
				System.out.println(TimeUtils.getDetailTimeStr() + " keepAliveRequestTimedOut");
			}

		}, 10, 5));
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
