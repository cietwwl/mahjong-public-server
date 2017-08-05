package com.randioo.mahjong_public_server.module.fight.service;

import java.util.List;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface FightService extends ObserveBaseServiceInterface {
    public void readyGame(Role role);

    /**
     * 退出游戏
     * 
     * @param role
     * @author wcy 2017年7月14日
     */
    void exitGame(Role role);

    /**
     * 申请退出游戏
     * 
     * @param role
     * @author wcy 2017年7月14日
     */
    void applyExitGame(Role role);

    /**
     * 同意退出
     * 
     * @param role
     * @param vote
     * @param voteId
     * @author wcy 2017年7月14日
     */
    void agreeExit(Role role, FightVoteApplyExit vote, int voteId);

    /**
     * 真实玩家出牌
     * 
     * @param role
     * @param paiList
     */
    void sendCard(Role role, int card, boolean isTouchCard);

    /**
     * 分牌
     * 
     * @param gameId
     * @author wcy 2017年5月31日
     */
    void dispatchCard(Game game);

    /**
     * 游戏开始
     * 
     * @param gameId
     */
    void gameStart(Game game);

    /**
     * 摸牌
     * 
     * @param gameRoleId
     * @param gameId
     */
    void touchCard(Game game);

    /**
     * 碰
     * 
     * @param role
     * @param gameSendCount
     * @param callCardListId
     * @author wcy 2017年7月14日
     */
    void peng(Role role, int gameSendCount, int callCardListId);

    /**
     * 杠
     * 
     * @param role
     * @param gameSendCount
     * @param callCardListId
     * @author wcy 2017年7月14日
     */
    void gang(Role role, int gameSendCount, int callCardListId);

    /**
     * 胡
     * 
     * @param role
     * @param gameSendCount
     * @param callCardListId
     * @author wcy 2017年7月14日
     */
    void hu(Role role, int gameSendCount, int callCardListId);

    /**
     * 过
     * 
     * @param role
     * @param gameSendCount
     * @author wcy 2017年7月14日
     */
    void guo(Role role, int gameSendCount);

    /**
     * 获得之前一个人的卡组
     * 
     * @param callCardLists
     * @return
     * @author wcy 2017年8月3日
     */
    CallCardList getPreviousCallCardList(List<CallCardList> callCardLists);

    /**
     * 获得游戏
     * 
     * @param gameId
     * @return
     */
    Game getGameById(int gameId);

    /**
     * 战斗断开
     * 
     * @param role
     * @author wcy 2017年7月14日
     */
    void disconnect(Role role);

    /**
     * 查询游戏配置
     * 
     * @param role
     * @author wcy 2017年8月4日
     */
    void queryGameConfig(Role role);

    /**
     * 重连
     * 
     * @param role
     * @author wcy 2017年7月14日
     */
    // void rejoin(Role role);

}
