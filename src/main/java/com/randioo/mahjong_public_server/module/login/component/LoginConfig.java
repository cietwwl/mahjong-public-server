package com.randioo.mahjong_public_server.module.login.component;

import com.randioo.randioo_server_base.module.login.LoginInfo;

/**
 * 登录配置
 * 
 * @author wcy 2017年8月4日
 *
 */
public class LoginConfig extends LoginInfo {
    /**
     * 头像url
     */
    private String headImageUrl;

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    @Override
    public String toString() {
        return "LoginConfig [headImageUrl=" + headImageUrl + "]";
    }

}
