package com.randioo.mahjong_public_server;

import java.io.IOException;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.randioo.mahjong_public_server.handler.HeartTimeOutHandler;
import com.randioo.mahjong_public_server.httpserver.LiteHttpServer;
import com.randioo.mahjong_public_server.httpserver.LiteServlet;
import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.servlet.StartServlet;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.config.GlobleArgsLoader;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.heart.ProtoHeartFactory;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.log.HttpLogUtils;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * Hello world!
 *
 */
public class mahjong_public_serverApp {

    public static void main(String[] args) {

        // SC sc =
        // SC.newBuilder().setExtension(ServerMessageExtension.newProtocol,
        // 2005).build();
        //
        // ByteString byteString = sc.toByteString();
        // try {
        // SC sc2 = SC.parseFrom(byteString);
        // System.out.println(sc2.getAllFields());
        //
        // ExtensionRegistry registry = ExtensionRegistry.newInstance();
        // ServerMessageExtension.registerAllExtensions(registry);
        // SC sc3 = SC.parseFrom(byteString,registry);
        //
        // Map<FieldDescriptor, Object> map = sc3.getAllFields();
        // for(Map.Entry<FieldDescriptor, Object> entrySet:map.entrySet()){
        // entrySet.getKey();
        // Object obj = entrySet.getValue();
        // System.out.println(obj);
        // }
        //
        //
        // } catch (Exception e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // System.exit(0);

        GlobleXmlLoader.init("./server.xml");
        GlobleArgsLoader.init(args);

        String projectName = GlobleMap.String(GlobleConstant.ARGS_PROJECT_NAME)
                + GlobleMap.Int(GlobleConstant.ARGS_PORT);
        HttpLogUtils.setProjectName(projectName);

        Logger logger = LoggerFactory.getLogger(mahjong_public_serverApp.class.getSimpleName());
        logger.info(HttpLogUtils.sys(GlobleMap.print()));

        SensitiveWordDictionary.readAll("./sensitive.txt");

        SpringContext.initSpringCtx("ApplicationContext.xml");

        // 平台接口初始化
        RandiooPlatformSdk randiooPlatformSdk = SpringContext.getBean(RandiooPlatformSdk.class);
        randiooPlatformSdk.setDebug(GlobleMap.Boolean(GlobleConstant.ARGS_PLATFORM));
        randiooPlatformSdk.setActiveProjectName(GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME));

        GameServerInit gameServerInit = ((GameServerInit) SpringContext.getBean(GameServerInit.class));

        HeartTimeOutHandler heartTimeOutHandler = SpringContext.getBean(HeartTimeOutHandler.class);
        gameServerInit.setKeepAliveFilter(new KeepAliveFilter(new ProtoHeartFactory(CS.class, SC.class),
                IdleStatus.READER_IDLE, heartTimeOutHandler, 3, 5));
        gameServerInit.start();

        LiteHttpServer server = new LiteHttpServer();
        server.setPort(GlobleMap.Int(GlobleConstant.ARGS_PORT) + 10000);
        server.setRootPath("/majiang");
        server.addLiteServlet("/kickRace", (LiteServlet) SpringContext.getBean(StartServlet.class));
        try {
            server.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, true);

    }
}
