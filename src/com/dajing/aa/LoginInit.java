package com.dajing.aa;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.dajing.db.DbHelper;
import com.dajing.shualian.SLJsonHelper;
import com.dajing.shualian.ShuaLianHelper;

public class LoginInit {

	private static Logger logger = Logger.getLogger(LoginInit.class);

	public static void main(String[] args) {
		String[] usernames = { "15314833633" };
		String password = "321321";
		DbHelper dbHelper = new DbHelper();
		ShuaLianHelper helper = new ShuaLianHelper();
		Map<String, String> cookies = new HashMap<String, String>();
//
//		messageStep(usernames, password, dbHelper, helper);

		 String username = usernames[0];
		 String messageCode = "020398";
//////		
		 JSONObject jsonObject = dbHelper.queryUsername(username);
		 String deviceId = jsonObject.optString("deviceId");
		 password = jsonObject.optString("password");
//		
		 String result = helper.checkMessageCode(username, messageCode,
		 deviceId);
		 System.out.println(result);
//		 result = helper.login(username, password, deviceId, cookies);
//		 System.out.println(result);

	}

	private static void messageStep(String[] usernames, String password, DbHelper dbHelper, ShuaLianHelper helper) {
		Map<String, String> cookies;
		for (String username : usernames) {

			// String username = "15594699651";
			String deviceId = "";
			String castgc = "";
			String session = "";

			cookies = new HashMap<String, String>();

			boolean isExist = dbHelper.checkUsername(username);
			if (!isExist) {
				String androidid = SLJsonHelper.randomStr(16, SLJsonHelper.radomStrL);
				String imsi = SLJsonHelper.randomStr(20, SLJsonHelper.radomNumber);
				String imei = SLJsonHelper.randomStr(15, SLJsonHelper.radomNumber);
				deviceId = SLJsonHelper.getUUID(androidid, imsi, imei);
				dbHelper.insertUsername(username, password, androidid, imsi, imei, deviceId);
			} else {
				JSONObject object = dbHelper.queryUsername(username);
				password = object.optString("password");
				deviceId = object.optString("deviceId");
				castgc = object.optString("castgc");
				session = object.optString("sessionId");
				cookies.put("SESSION", session);
				cookies.put("CASTGC", castgc);
			}

			String result = null;

			result = helper.login(username, password, deviceId, cookies);
			System.out.println(cookies);
			logger.info(result);

			try {
				JSONObject resultJson = new JSONObject(result);
				resultJson = resultJson.optJSONObject("data");
				int resultCode = resultJson.optInt("code");
				if (resultCode == 0) {
					logger.info("登陆成功" + username);
					session = resultJson.optString("jsessionid");
					resultJson = resultJson.optJSONObject("data");
					castgc = resultJson.optString("castgc");
					int userId = resultJson.optInt("userId");
					int imUserId = resultJson.optJSONObject("Imdata").optInt("imUserId");
					String imPassword = resultJson.optJSONObject("Imdata").optString("password");

					dbHelper.updateUserLoginInfo(username, session, castgc, userId, imUserId, imPassword);

				} else if (resultCode == 1144) {
					result = helper.messageCode(username, deviceId);
					logger.info("首次登陆下发短信:" + result);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

}
