package com.randioo.mahjong_public_server.module.match.service;

import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.util.key.Key;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface MatchService extends ObserveBaseServiceInterface {
    /**
     * 创建游戏
     * 
     * @param role
     * @return
     * @author wcy 2017年5月25日
     */
    public void createRoom(Role role, GameConfigData gameConfigData);

    /**
     * 预加入
     * 
     * @param role
     * @param lockString
     * @author wcy 2017年8月10日
     */
    void preJoinGame(Role role, String lockString);

    /**
     * 加入游戏
     * 
     * @param role
     * @param lockString
     * @return
     * @author wcy 2017年5月25日
     */
    public void joinGame(Role role, String lockString);

    /**
     * 获得游戏玩家标识符
     * 
     * @param gameId
     * @param roleId
     * @return
     * @author wcy 2017年5月25日
     */
    String getGameRoleId(int gameId, int roleId);

    GeneratedMessage match(Role role);

    Game createGame(int roleId, GameConfigData gameConfigData);

    Game createGameByGameConfig(GameConfigData gameConfigData);

    void joinGameProcess1(Role role, int gameId);

    void joinGameProcess2(Role role, RoleGameInfo roleGameInfo, Game game);

    GameRoleData parseGameRoleData(RoleGameInfo info, Game game);

    void fillAI(Game game);

    /**
     * 获得钥匙的房间字符串
     * 
     * @param key
     * @return
     * @author wcy 2017年7月13日
     */
    String getLockString(Key key);

    /**
     * 取消匹配
     * 
     * @param role
     * @author wcy 2017年7月14日
     */
    void cancelMatch(Role role);

    /**
     * 取消匹配服务接口
     * 
     * @param role
     * @author wcy 2017年7月14日
     */
    void serviceCancelMatch(Role role);

    void checkRoom(String roomId, IoSession session);

    /**
     * 清空某玩家的座位
     * 
     * @param game
     * @param seat
     * @author wcy 2017年7月28日
     */
    void clearSeatByGameRoleId(Game game, String gameRoleId);

    /**
     * 获得重连录像
     * 
     * @param game
     * @param gameRoleId
     * @return
     * @author wcy 2017年8月16日
     */
    List<ByteString> getRejoinSCList(Game game, String gameRoleId);

}
