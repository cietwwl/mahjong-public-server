package com.randioo.mahjong_public_server.module.login.service;

import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.dao.RoleDao;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.login.LoginConstant;
import com.randioo.mahjong_public_server.module.login.component.LoginConfig;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.module.race.service.RaceService;
import com.randioo.mahjong_public_server.module.role.service.RoleService;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.RoleData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Login.LoginGetRoleDataResponse;
import com.randioo.mahjong_public_server.protocol.Login.SCLoginOtherSide;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.Tool;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.module.login.Facility;
import com.randioo.randioo_server_base.module.login.LoginHandler;
import com.randioo.randioo_server_base.module.login.LoginInfo;
import com.randioo.randioo_server_base.module.login.LoginModelConstant;
import com.randioo.randioo_server_base.module.login.LoginModelService;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.StringUtils;

@Service("loginService")
public class LoginServiceImpl extends ObserveBaseService implements LoginService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private LoginModelService loginModelService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RaceService raceService;

    @Autowired
    private GameDB gameDB;

    @Autowired
    private MatchService matchService;

    @Override
    public void init() {
        // 初始化所有已经有过的帐号和昵称
        add(RoleCache.getNameSet(), roleDao.getAllNames());
        add(RoleCache.getAccountSet(), roleDao.getAllAccounts());

        loginModelService.setLoginHandler(new LoginHandlerImpl());
    }

    @Override
    public void initService() {
        loggerinfo("initService");
    }

    private void add(Map<String, String> map, List<String> list) {
        for (String str : list) {
            map.put(str, str);
        }
    }

    private class LoginHandlerImpl implements LoginHandler {

        @Override
        public RoleInterface getRoleInterfaceFromDBById(int roleId) {
            return roleDao.get(roleId);
        }

        @Override
        public RoleInterface getRoleInterfaceFromDBByAccount(String account) {
            return roleDao.getRoleByAccount(account);
        }

        @Override
        public void loginRoleModuleDataInit(RoleInterface roleInterface) {
            // 将数据库中的数据放入缓存中
            Role role = (Role) roleInterface;

            roleService.roleInit(role);
            raceService.raceInit(role);
        }

        @Override
        public boolean createRoleCheckAccount(LoginInfo info, Ref<Integer> errorCode) {
            // 账号姓名不可为空
            if (StringUtils.isNullOrEmpty(info.getAccount())) {
                errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
                return false;
            }

            return true;
        }

        @Override
        public RoleInterface createRole(LoginInfo loginInfo) {
            String account = loginInfo.getAccount();
            // 用户数据
            // 创建用户
            Role role = new Role();
            role.setAccount(account);

            roleService.newRoleInit(role);
            raceService.newRaceInit(role);

            gameDB.getInsertPool().submit(new EntityRunnable<Role>(role) {

                @Override
                public void run(Role entity) {
                    try {
                        roleDao.insert(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return role;
        }

        @Override
        public Facility saveFacility(Facility facility) {
            return null;
        }

        @Override
        public void noticeOtherPlaceLogin(Facility oldFacility) {
            IoSession session = oldFacility.getSession();
            SessionUtils.sc(session, SC.newBuilder().setSCLoginOtherSide(SCLoginOtherSide.newBuilder()).build());
        }

        @Override
        public Facility getFacilityFromDB(int roleId, String macAddress) {
            return null;
        }

    }

    @Override
    public GeneratedMessage getRoleData(LoginInfo loginInfo, IoSession ioSession) {

        Ref<Integer> errorCode = new Ref<>();

        RoleInterface roleInterface = loginModelService.getRoleData(loginInfo, errorCode, ioSession);

        if (roleInterface != null) {
            Role role = (Role) roleInterface;

            // 刷新用户头像
            LoginConfig loginConfig = (LoginConfig) loginInfo;
            role.setHeadImgUrl(loginConfig.getHeadImageUrl());
            
            loggerinfo(role,loginConfig);

            return SC.newBuilder()
                    .setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setRoleData(getRoleData(role)))
                    .build();
        }

        ErrorCode errorEnum = null;
        switch (errorCode.get()) {
        case LoginModelConstant.GET_ROLE_DATA_NOT_EXIST:
            errorEnum = ErrorCode.NO_ROLE_DATA;
            break;
        case LoginModelConstant.GET_ROLE_DATA_IN_LOGIN:
            errorEnum = ErrorCode.IN_LOGIN;
            break;
        }
        SC sc = SC.newBuilder()
                .setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setErrorCode(errorEnum.getNumber()))
                .build();

        return sc;
    }

    @Override
    public RoleData getRoleData(Role role) {
        roleService.roleInit(role);

        int roleId = Tool.regExpression(role.getAccount(), "[0-9]*") ? Integer.parseInt(role.getAccount()) : role
                .getRoleId();
        Game game = GameCache.getGameMap().get(role.getGameId());
        // 游戏不存在或游戏已经结束,钥匙不存在
        String lockString = game == null || game.getGameState() == GameState.GAME_START_END ? null : matchService
                .getLockString(game.getLockKey());
        RoleData.Builder builder = RoleData.newBuilder().setRoleId(roleId).setPoint(1000).setSex(1);

        if (lockString != null)
            builder.setRoomId(lockString);

        return builder.build();
    }

    @Override
    public Role getRoleById(int roleId) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceById(roleId);
        return roleInterface == null ? null : (Role) roleInterface;
    }

    @Override
    public Role getRoleByAccount(String account) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceByAccount(account);
        return roleInterface == null ? null : (Role) roleInterface;
    }
}
