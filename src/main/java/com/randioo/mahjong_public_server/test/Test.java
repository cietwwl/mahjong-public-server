package com.randioo.mahjong_public_server.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.utils.ReflectUtils;

@Component
public class Test {

	@Autowired
	private FightService fightService;

	@Autowired
	private MatchService matchService;

	private Map<String, MethodFunction> methods = new HashMap<>();

	public void fuck() {
		try {
			case1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void case1() {
		createRole();
		createGame();
		readyGame();
		send();
	}

	public Role createRole(String account) {
		Role role = new Role();
		role.setRoleId(Integer.valueOf(account));
		role.setAccount(account);
		role.setName(account);
		role.setHeadImgUrl("");
		RoleCache.putNewRole(role);

		return role;
	}

	public void createRole() {
		createRole("1");
		createRole("2");
		createRole("3");
		createRole("4");
	}

	public Game createGame() {

		Role role1 = role("1");
		Role role2 = role("2");
		Role role3 = role("3");
		Role role4 = role("4");

		GameConfigData gameConfigData = GameConfigData.newBuilder().setMaxCount(4).build();

		matchService.createRoom(role1, gameConfigData);
		matchService.joinGame(role2, 1);
		matchService.joinGame(role3, 1);
		matchService.joinGame(role4, 1);

		Game game = GameCache.getGameMap().get(1);
		return game;
	}

	public Role role(String account) {
		return (Role) RoleCache.getRoleByAccount(account);
	}

	public void readyGame() {
		fightService.readyGame(role("1"));
		fightService.readyGame(role("2"));
		fightService.readyGame(role("3"));
		fightService.readyGame(role("4"));

		print();
	}

	public void send() {
		methods.put("sendCard",
				new MethodFunction(ReflectUtils.getMethod(fightService.getClass(), "sendCard", Role.class, int.class)) {

					@Override
					public Object apply(Object... params) {
						Role role = (Role) params[0];
						String[] arr = (String[]) params[1];
						ReflectUtils.invokeMethod(fightService, method, role, Integer.parseInt(arr[2]));
						return null;
					}

				});

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		System.out.println("enter:");
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				String[] array = in.nextLine().split(",");
				int roleId = Integer.parseInt(array[0]);
				String methodName = array[1];

				Role role = role(roleId + "");
				MethodFunction method = methods.get(methodName);
				method.apply(role, array);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void print() {
		Game game = GameCache.getGameMap().get(1);
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			System.out.println(roleGameInfo);
		}
	}

}
