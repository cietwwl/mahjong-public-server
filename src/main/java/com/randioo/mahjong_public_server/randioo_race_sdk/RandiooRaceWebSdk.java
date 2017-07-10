package com.randioo.mahjong_public_server.randioo_race_sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.randioo.mahjong_public_server.entity.po.RaceRole;
import com.randioo.mahjong_public_server.entity.po.RaceStateInfo;
import com.randioo.mahjong_public_server.util.HttpConnnection;
import com.randioo.randioo_server_base.utils.HttpUtils;

public class RandiooRaceWebSdk {

	private Gson gson;
	private TypeAdapter<RaceExistResponse> raceExistResponseAdapter;
	private boolean debug;

	public void init() {
		gson = new Gson();
		raceExistResponseAdapter = gson.getAdapter(RaceExistResponse.class);
	}

	public void debug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * 返回比赛配置表
	 * 
	 * @param raceId
	 * @return
	 * @author wcy 2017年6月23日
	 */
	public RaceExistResponse exist(int raceId) {
		// 需判定判定
		String urlStr = "http://10.0.51.18/APPadmin/gateway/PhpServices/Hhmajiang/getRoom.php?key=f4f3f65d6d804d138043fbbd1843d510&room="
				+ raceId;
		urlStr = (debug ? "http://10.0.51.18/APPadmin" : "http://manager.app.randioo.com")
				+ "/gateway/PhpServices/Hhmajiang/getRoom.php";

		HttpParameter parameter = new HttpParameter();
		parameter.put("key", "f4f3f65d6d804d138043fbbd1843d510");
		parameter.put("room", raceId + "");

		try {
			// String result = HttpConnnection.sendMessageGet(urlStr);
			String result = HttpUtils.get(urlStr, parameter.getParameterMap());
			RaceExistResponse response = raceExistResponseAdapter.fromJson(result);
			return response.errorCode == 1 ? response : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void create(int raceId, String createRaceAccount) {
		String urlStr = "http://10.0.51.21:8080/game-server-web/createMahjongRace" + "?raceId=" + raceId
				+ "&createRaceAccount=" + createRaceAccount + "&state=" + (debug ? "debug" : "run");

		urlStr = (debug ? "http://10.0.51.21:8080" : "http://localhost:8080") + "/game-server-web/createMahjongRace";
		HttpParameter parameter = new HttpParameter();
		parameter.put("raceId", raceId + "");
		parameter.put("createRaceAccount", createRaceAccount);
		parameter.put("state", (debug ? "debug" : "run"));

		System.out.println(urlStr);
		try {
			HttpUtils.get(urlStr, parameter.getParameterMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void state(RaceStateInfo raceInfo) {
		String urlStr = "http://10.0.51.21:8080/game-server-web/accountMahjongRace";
		try {
			String param = "&raceId=" + raceInfo.raceId + "&isFinal=" + (raceInfo.isFinal ? 1 : 0);
			// JSONArray accounts = new JSONArray();
			JSONArray accountsObj = new JSONArray();
			for (RaceRole seat : raceInfo.accounts) {
				JSONObject obj = new JSONObject();
				obj.put("account", seat.account);
				obj.put("score", seat.score);
				accountsObj.put(obj);
			}

			JSONArray waits = new JSONArray();
			for (RaceRole seat : raceInfo.queueAccount) {
				JSONObject obj = new JSONObject();
				obj.put("account", seat.account);
				obj.put("score", seat.score);
				waits.put(obj);
			}

			param += "&state=" + (debug ? "debug" : "run") + "&playList=" + accountsObj + "&waitList=" + waits;

			System.out.println(param);

			HttpConnnection.sendMessagePost(urlStr, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			RandiooRaceWebSdk sdk = new RandiooRaceWebSdk();
			sdk.init();
			sdk.debug(true);
			RaceExistResponse response = sdk.exist(1);

			System.out.println(response);
			// sdk.create(1, "wcy");
			RaceStateInfo info = new RaceStateInfo();
			for (int i = 0; i < 10; i++) {
				RaceRole account = new RaceRole();
				account.account = "account" + i;
				account.score = i;
				RaceRole queue = new RaceRole();
				queue.account = "queue" + i;
				queue.score = i;
				info.accounts.add(account);
				info.queueAccount.add(queue);
			}
			info.isFinal = false;
			info.raceId = 1;

			sdk.state(info);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
