package com.randioo.mahjong_public_server;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.keepalive.KeepAliveFilter;

import com.randioo.mahjong_public_server.handler.HeartTimeOutHandler;
import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.Heart.CSHeart;
import com.randioo.mahjong_public_server.protocol.Heart.HeartRequest;
import com.randioo.mahjong_public_server.protocol.Heart.HeartResponse;
import com.randioo.mahjong_public_server.protocol.Heart.SCHeart;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.config.ConfigLoader;
import com.randioo.randioo_server_base.config.GlobleArgsLoader;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.heart.ProtoHeartFactory;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.init.LogSystem;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * Hello world!
 *
 */
public class mahjong_public_serverApp {

    /**
     * @param args
     * @author wcy 2017年8月17日
     */
    public static void main(String[] args) {

        GlobleXmlLoader.init("./server.xml");
        GlobleArgsLoader.init(args);

        LogSystem.init(mahjong_public_serverApp.class);

        ConfigLoader.loadConfig("com.randioo.mahjong_public_server.entity.file", "./config.zip");
        
        SensitiveWordDictionary.readAll("./sensitive.txt");

        SpringContext.initSpringCtx("ApplicationContext.xml");

        // 平台接口初始化
        RandiooPlatformSdk randiooPlatformSdk = SpringContext.getBean(RandiooPlatformSdk.class);
        randiooPlatformSdk.setDebug(GlobleMap.Boolean(GlobleConstant.ARGS_PLATFORM));
        randiooPlatformSdk.setActiveProjectName(GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME));

        GameServerInit gameServerInit = ((GameServerInit) SpringContext.getBean(GameServerInit.class));
        // 设置CS
        gameServerInit.setMessageLite(CS.getDefaultInstance());
        
        // 心跳工厂
        ProtoHeartFactory protoHeartFactory = new ProtoHeartFactory();
        protoHeartFactory.setHeartRequest(CS.newBuilder().setHeartRequest(HeartRequest.newBuilder()).build());
        protoHeartFactory.setHeartResponse(SC.newBuilder().setHeartResponse(HeartResponse.newBuilder()).build());
        protoHeartFactory.setScHeart(SC.newBuilder().setSCHeart(SCHeart.newBuilder()).build());
        protoHeartFactory.setCsHeart(CS.newBuilder().setCSHeart(CSHeart.newBuilder()).build());

        HeartTimeOutHandler heartTimeOutHandler = SpringContext.getBean(HeartTimeOutHandler.class);
        gameServerInit.setKeepAliveFilter(new KeepAliveFilter(protoHeartFactory, IdleStatus.READER_IDLE,
                heartTimeOutHandler, 5, 3));
        gameServerInit.start();

        // LiteHttpServer server = new LiteHttpServer();
        // server.setPort(GlobleMap.Int(GlobleConstant.ARGS_PORT) + 10000);
        // server.setRootPath("/mahjong_public");
        // server.addLiteServlet("/kickRace", (LiteServlet)
        // SpringContext.getBean(StartServlet.class));
        // // server.addLiteServlet("/giveCard", (LiteServlet)
        // // SpringContext.getBean(StartServlet.class));
        // try {
        // server.init();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, true);

    }
}
