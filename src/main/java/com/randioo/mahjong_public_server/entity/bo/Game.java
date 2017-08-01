package com.randioo.mahjong_public_server.entity.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.po.CallCardList;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.module.fight.component.score.round.GameOverResult;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.mahjong_public_server.util.key.Key;
import com.randioo.mahjong_public_server.util.vote.VoteBox;

public class Game {
    private int gameId;
    /** 玩家id集合 */
    private Map<String, RoleGameInfo> roleIdMap = new LinkedHashMap<>();
    /** 房主id */
    private int masterRoleId;
    /** 房间锁 */
    private Key lockKey;
    /** 游戏开始 */
    private GameState gameState;
    /** 游戏类型 */
    private GameType gameType;
    /** 游戏配置 */
    private GameConfigData gameConfigData;
    /** 在线玩家数量 */
    private int onlineRoleCount;
    /** 玩家id列表，用于换人 */
    private List<String> roleIdList = new ArrayList<>();
    /** 当前玩家id */
    private int currentRoleIdIndex;
    /** 当前指针指向的目标牌 */
    private int currentCardSeatIndex;
    /** 游戏倍数 */
    private int multiple;
    /** 出牌计数 */
    private int sendCardCount;
    /** 出牌的时间戳 */
    private int sendCardTime;
    /** 庄家的玩家id */
    private int zhuangSeat = -1;
    /** 以及打完的回合数 */
    private int finishRoundCount;
    /** 剩余的牌 */
    private List<Integer> remainCards = new ArrayList<>();
    /** 桌上的牌<索引id,出牌的列表> */
    private Map<Integer, List<Integer>> desktopCardMap = new HashMap<>();
    /** 每个人每次叫牌的临时存储 */
    private List<CallCardList> callCardLists = new ArrayList<>();
    /** 每个人胡的牌要另外再存一份 */
    private List<CallCardList> huCallCardLists = new ArrayList<>();
    /** 出牌放在桌上的表 */
    private Map<Integer, List<Integer>> sendDesktopCardMap = new HashMap<>();
    /** 玩家结果统计 */
    private Map<String, GameOverResult> statisticResultMap = new HashMap<>();
    /** 是否结算 */
    private boolean isSaveData = false;
    /** 燃点币 */
    private int randiooMoney;
    /** 投票箱 */
    private VoteBox voteBox = new VoteBox();

    public VoteBox getVoteBox() {
        return voteBox;
    }

    public int getFinishRoundCount() {
        return finishRoundCount;
    }

    public void setFinishRoundCount(int finishRoundCount) {
        this.finishRoundCount = finishRoundCount;
    }

    public int getOnlineRoleCount() {
        return onlineRoleCount;
    }

    public void setOnlineRoleCount(int onlineRoleCount) {
        this.onlineRoleCount = onlineRoleCount;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public Map<String, RoleGameInfo> getRoleIdMap() {
        return roleIdMap;
    }

    public int getMasterRoleId() {
        return masterRoleId;
    }

    public void setMasterRoleId(int masterRoleId) {
        this.masterRoleId = masterRoleId;
    }

    public Key getLockKey() {
        return lockKey;
    }

    public void setLockKey(Key lockKey) {
        this.lockKey = lockKey;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameConfigData getGameConfig() {
        return gameConfigData;
    }

    public void setGameConfig(GameConfigData gameConfigData) {
        this.gameConfigData = gameConfigData;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public int getCurrentRoleIdIndex() {
        return currentRoleIdIndex;
    }

    public void setCurrentRoleIdIndex(int currentRoleIdIndex) {
        this.currentRoleIdIndex = currentRoleIdIndex;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public int getSendCardCount() {
        return sendCardCount;
    }

    public void setSendCardCount(int sendCardCount) {
        this.sendCardCount = sendCardCount;
    }

    public int getSendCardTime() {
        return sendCardTime;
    }

    public void setSendCardTime(int sendCardTime) {
        this.sendCardTime = sendCardTime;
    }

    public int getZhuangSeat() {
        return zhuangSeat;
    }

    public void setZhuangSeat(int zhuangSeat) {
        this.zhuangSeat = zhuangSeat;
    }

    /**
     * 获得剩余牌
     * 
     * @return
     */
    public List<Integer> getRemainCards() {
        return remainCards;
    }

    public Map<Integer, List<Integer>> getDesktopCardMap() {
        return desktopCardMap;
    }

    public Map<Integer, List<Integer>> getSendDesktopCardMap() {
        return sendDesktopCardMap;
    }

    public List<CallCardList> getCallCardLists() {
        return callCardLists;
    }

    /**
     * 当前指针指向的目标牌
     * 
     * @return
     * @author wcy 2017年6月16日
     */
    public int getCurrentCardSeatIndex() {
        return currentCardSeatIndex;
    }

    public void setCurrentCardSeatIndex(int currentCardSeatIndex) {
        this.currentCardSeatIndex = currentCardSeatIndex;
    }

    public List<CallCardList> getHuCallCardLists() {
        return huCallCardLists;
    }

    public Map<String, GameOverResult> getStatisticResultMap() {
        return statisticResultMap;
    }

    public boolean isSaveData() {
        return isSaveData;
    }

    public void setSaveData(boolean isSaveData) {
        this.isSaveData = isSaveData;
    }

    public int getRandiooMoney() {
        return randiooMoney;
    }

    public void setRandiooMoney(int randiooMoney) {
        this.randiooMoney = randiooMoney;
    }

    @Override
    public String toString() {
        String n = System.getProperty("line.separator");
        String t = "\t";
        StringBuilder sb = new StringBuilder();
        sb.append("Game :[").append(n);
        sb.append(t).append("gameId=>").append(gameId).append(n);
        sb.append(t).append("roleGameInfoMap").append(n);
        for (RoleGameInfo roleGameInfo : roleIdMap.values()) {
            sb.append(t).append(roleGameInfo.toString()).append(n);
        }
        sb.append(t).append("callCardLists=>");
        for (CallCardList callCardList : callCardLists) {
            sb.append(t).append(callCardList).append(n);
        }
        sb.append(t).append("huCallCardList=>").append(n);
        for (CallCardList callCardList : huCallCardLists) {
            sb.append(t).append(callCardList).append(n);
        }
        sb.append(t).append("currentRoleIndex=>").append(currentRoleIdIndex).append(n);
        sb.append(t).append("currentCardIndex=>").append(currentCardSeatIndex).append(n);
        sb.append(t).append("sendCardCount=>").append(sendCardCount).append(n);
        sb.append(t).append("remainCards=>").append(remainCards).append(n);
        sb.append(t).append("]").append(n);
        return sb.toString();
    }

}
