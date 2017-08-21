package com.randioo.mahjong_public_server.module.login.component;

import com.randioo.randioo_server_base.module.login.LoginInfo;

/**
 * 登录配置
 * 
 * @author wcy 2017年8月4日
 *
 */
public class LoginConfig extends LoginInfo {
    /** 头像 */
    private String headImageUrl;
    /** 昵称 */
    private String nickname;

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LoginConfig [headImageUrl=").append(headImageUrl).append(", nickname=").append(nickname)
                .append("]");
        return builder.toString();
    }

}
