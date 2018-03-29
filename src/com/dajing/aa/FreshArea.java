package com.dajing.aa;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dajing.db.DbHelper;
import com.dajing.shualian.SLJsonHelper;
import com.dajing.shualian.ShuaLianHelper;
import com.dajing.util.FileUtil;

public class FreshArea {

	private static Logger logger = Logger.getLogger(FreshArea.class);
	public static final String username = "15619414016";

	public static void main(String[] args) {
		run(username);

	}

	public static void areaList() {
		DbHelper dbHelper = new DbHelper();

		String rootPath = FileUtil.getRootPath();
		rootPath = rootPath + "areas.txt";
		FileUtil.saveFile("".getBytes(), rootPath);
		List<JSONObject> areas = dbHelper.queryAreaOrderBy();
		for (JSONObject areaJson : areas) {

			int areaid = areaJson.optInt("areaid");
			String province = areaJson.optString("province");
			String city = areaJson.optString("city");
			String district = areaJson.optString("district");
			double longitude = areaJson.optDouble("longitude");
			double latitude = areaJson.optDouble("latitude");
			int vedionum = areaJson.optInt("vedionum");

			String areaStr = areaid + "-" + province + "-" + city + "-" + district + "-" + longitude + "-" + latitude
					+ "-" + vedionum;
			FileUtil.saveFile(areaStr.getBytes(), rootPath, true);
		}

		logger.info("总计" + areas.size() + "地址记录, " + rootPath);

	}

	public static void run(String username) {
		DbHelper dbHelper = new DbHelper();

		ShuaLianHelper helper = new ShuaLianHelper();
		Map<String, String> cookies = new HashMap<String, String>();

		List<JSONObject> areaList = dbHelper.queryAreaOrderBy();
		// JSONObject arer = dbHelper.queryArea(3378);
		// List<JSONObject> areaList = new ArrayList<JSONObject>();
		// areaList.add(arer);
		/**
		 * 从数据库中查询用户信息
		 */
		JSONObject object = dbHelper.queryUsername(username);
		if (object == null) {
			logger.error("数据库总不存在" + username + "账号");
			return;
		}
		String password = object.optString("password");

		messageStep(username, password, dbHelper, helper);

		String deviceId = object.optString("deviceId");
		// deviceId = "00000000-0000-0000-0000-000000000000";
		String castgc = object.optString("castgc");
		String sessionId = object.optString("sessionId");
		String shopId = object.optString("shopId");
		int userId = object.optInt("id");

		cookies.put("CASTGC", castgc);
		cookies.put("SESSION", sessionId);

		String result = helper.initInfo(sessionId, cookies);
		logger.info("获取用户信息 result:" + result);

		try {
			JSONObject resultJson = new JSONObject(result);
			shopId = resultJson.optJSONObject("data").optJSONObject("data").optJSONObject("shop").optString("no");
			logger.info("获取用户信息 shopId:" + shopId);

			int index = 0;
			for (JSONObject areaJson : areaList) {

				int areaid = areaJson.optInt("areaid");
				String province = areaJson.optString("province");
				String city = areaJson.optString("city");
				String district = areaJson.optString("district");
				double longitude = areaJson.optDouble("longitude");
				double latitude = areaJson.optDouble("latitude");
				int vedionum = areaJson.optInt("vedionum");

				String area = makeArea(province, city, district, latitude, longitude);
				result = helper.getVideoList(area, shopId, "", cookies);

				logger.info("result：" + result);

				
				resultJson = new JSONObject(result);
				vedionum = resultJson.optJSONObject("info").optInt("totalRow");

				boolean flag = dbHelper.updateAreaVedionum(areaid, vedionum);
				logger.info(index++ + "更新地区广告条数 :" + flag + ", " + areaid + ", " + vedionum);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String makeArea(String province, String city, String district, double latitude, double longitude) {
		String area = null;
		try {
			JSONObject datas = new JSONObject();
			datas.put("province", province);
			datas.put("city", city);
			datas.put("district", district);
			datas.put("latitude", latitude);
			datas.put("longitude", longitude);

			area = Base64.getEncoder().encodeToString(datas.toString().getBytes());

		} catch (JSONException e) {
			// TODO: handle exception
		}
		return area;
	}

	private static void messageStep(String username, String password, DbHelper dbHelper, ShuaLianHelper helper) {
		Map<String, String> cookies;

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
