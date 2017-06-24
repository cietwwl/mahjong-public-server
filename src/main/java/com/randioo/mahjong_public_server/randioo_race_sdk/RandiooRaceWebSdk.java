package com.randioo.mahjong_public_server.randioo_race_sdk;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.randioo.mahjong_public_server.entity.po.RaceRole;
import com.randioo.mahjong_public_server.entity.po.RaceStateInfo;
import com.randioo.mahjong_public_server.util.HttpConnnection;

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
		String urlStr = "http://10.0.51.6/APPadmin/gateway/PhpServices/Hhmajiang/getRoom.php?key=f4f3f65d6d804d138043fbbd1843d510&room="
				+ raceId;
		try {
			String result = HttpConnnection.sendMessageGet(urlStr);
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
		System.out.println(urlStr);
		try {
			System.out.println(HttpConnnection.sendMessageGet(urlStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void state(RaceStateInfo raceInfo) {
		String urlStr = "http://10.0.51.21:8080/game-server-web/accountMahjongRace";
		try {
			String param = "&raceId=" + raceInfo.raceId + "&isFinal=" + raceInfo.isFinal;
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
			info.isFinal = 0;
			info.raceId = 1;

			sdk.state(info);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
