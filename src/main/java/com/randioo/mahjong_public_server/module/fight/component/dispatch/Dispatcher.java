package com.randioo.mahjong_public_server.module.fight.component.dispatch;

import java.util.List;

public interface Dispatcher {
    /**
     * 分牌
     * 
     * @param originCards
     * @return
     * @author wcy 2017年8月2日
     */
    public List<CardPart> dispatch(List<Integer> originCards, int partCount, int everyPartCount);
}
