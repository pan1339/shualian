package com.dajing.shualian.test;

import java.awt.HeadlessException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.dajing.db.DbHelper;
import com.dajing.jsoup.JsoupService.DJsoupKey;
import com.dajing.shualian.ShuaLianHelper;
import com.dajing.shualian.ShuaLianHelper;
import com.dajing.util.FileUtil;
import com.dajing.util.PropertiesUtil;

public class SLTest {

	String userId = "16127175";
	String username = "18049511425";

	String shopId = "HZJZ1705302211mgSWe1";
	// String province = "浙江省";
	// String city = "金华市";
	// // 义乌市//29.306756,120.07514
	// // 浦江县,29.479046,119.899383
	// String district = "义乌市";
	// double latitude = 29.306756;
	// double longitude = 120.07514;

	String province = "海南省";
	String city = "海口市";
	// 义乌市//29.306756,120.07514
	// 浦江县,29.479046,119.899383
	String district = "";
	double latitude = 20.050057;
	double longitude = 110.206424;

	// String district = "浦江县";
	// double latitude = 29.479046;
	// double longitude = 119.899383;
	String castgc = "TGT-4672634-wUBjbjV5QePZugHN0dlbkbMtSMovLD1gPuQ5iFXAtJWEtNjWb1-cas";
	String sessionid = "8ea37f9d-ff4c-4f79-b84b-21ac44d87d3e";

	@Test
	public void productStatistics() {
		// String area = ShuaLianHelper.makeArea(province, city, district,
		// latitude, longitude);
		//
		// System.out.println(new String(Base64.getDecoder().decode(
		// "eyJwcm92aW5jZSI6Iua1meaxn+ecgSIsImNpdHkiOiLph5HljY7luIIiLCJkaXN0cmljdCI6Iua1puaxn+WOvyIsImxhdGl0dWRlIjoyOS40NzkwNDYsImxvbmdpdHVkZSI6MTE5Ljg5OTM4M30=")));
		// long pageViewTime = System.currentTimeMillis() - 1000 * 10;
		// ShuaLianHelper.productStatistics(username, userId, shopId,
		// pageViewTime, area, castgc, sessionid);

	}

	/**
	 * http://www.lbwhds.com/wap/HZJZ1704271425h2RbS1/buy/littleplugin/index.htm?area=eyJwcm92aW5jZSI6Iua1meaxn%2BecgSIsImNpdHkiOiLph5HljY7luIIiLCJkaXN0cmljdCI6Iua1puaxn%2BWOvyIsImxhdGl0dWRlIjoyOS40NzkwNDYsImxvbmdpdHVkZSI6MTE5Ljg5OTM4M30%3D
	 * {"province":"浙江省","city":"金华市","district":"浦江县","latitude":29.479046,"longitude":119.899383}
	 * {"province":"浙江省","city":"金华市","district":"浦江县","latitude":29.479046,"longitude":119.899383}
	 * 
	 * @throws JSONException
	 */
	@Test
	public void littlepluginArea() {

		ShuaLianHelper helper = new ShuaLianHelper();
		DbHelper dbHelper = new DbHelper();
		try {
			JSONObject jsonObject = PropertiesUtil.readProperties2JSONObject("config/latitude_longitude");
			JSONArray array = new JSONArray();

			Iterator it = jsonObject.keys();

			while (it.hasNext()) {
				String key = (String) it.next();
				String value = jsonObject.getString(key);
				if (key.contains("----")) {
					JSONObject object = new JSONObject();
					String[] keyStrs = key.split("----");
					String[] valueStrs = value.split("----");

					String mprovince = null;
					String mcity = null;
					String mdistrict = null;
					String mlongitude = null;
					String mlatitude = null;

					if (keyStrs.length == 3) {
						mprovince = keyStrs[0];
						mcity = keyStrs[1];
						mdistrict = keyStrs[2];
						object.put("province", keyStrs[0]);
						object.put("city", keyStrs[1]);
						object.put("district", keyStrs[2]);
					} else if (keyStrs.length == 5) {
						mprovince = keyStrs[0];
						mcity = keyStrs[1];
						mdistrict = keyStrs[2];
						mlongitude = keyStrs[3];
						mlatitude = keyStrs[4];
						object.put("province", mprovince);
						object.put("city", mcity);
						object.put("district", mdistrict);
						object.put("longitude", mlongitude);
						object.put("latitude", mlatitude);
					} else if (keyStrs.length == 6) {
						mprovince = keyStrs[0];
						mcity = keyStrs[1];
						mdistrict = keyStrs[2];
						mlongitude = keyStrs[3];
						mlatitude = keyStrs[4];
						object.put("province", mprovince);
						object.put("city", mcity);
						object.put("district", mdistrict);
						object.put("longitude", mlongitude);
						object.put("latitude", mlatitude);
					}
					if (valueStrs.length == 2) {
						mlongitude = valueStrs[0];
						mlatitude = valueStrs[1];
						object.put("longitude", valueStrs[0]);
						object.put("latitude", valueStrs[1]);
					}
					array.put(object);
					// dbHelper.insertArea(mprovince, mcity, mdistrict,
					// mlongitude, mlatitude);
					System.out.println(mprovince + "-" + mcity + "-" + mdistrict + "-" + mlongitude + "-" + mlatitude);
				}
			}

			for (int i = 0; i < array.length(); i++) {
				int pageSize = 500;
				int start = 0;
				int curPage = 1;
				//
				// ShuaLianHelper.productStatistics(username, userId, shopId,
				// province, city, district, latitude,
				// longitude, castgc, sessionid);

				JSONObject object = array.getJSONObject(i);

				String mprovince = object.optString("province");
				String mcity = object.optString("city");
				String mdistrict = object.optString("district");
				double mlongitude = object.optDouble("longitude");
				double mlatitude = object.optDouble("latitude");

				String area = helper.makeArea(mprovince, mcity, mdistrict, mlatitude, mlongitude);
				JSONObject jsonObject2 = null;// helper.getVideoList(area,
												// shopId, pageSize, start,
												// curPage, "",
												// castgc,sessionid);

				String result = jsonObject2.optString(DJsoupKey.RESPONSE_BODY);

				JSONObject resultBody = new JSONObject(result);
				System.out.println(resultBody);

				boolean success = resultBody.optBoolean("success");
				System.out.println(success);
				JSONObject infoObject = resultBody.optJSONObject("info");
				JSONArray jsonArray = infoObject.optJSONArray("datas");

				for (int j = 0; j < jsonArray.length(); j++) {
					JSONObject indexJson = jsonArray.optJSONObject(j);
					System.out.println(indexJson);
				}

				FileUtil.saveFile((mprovince + "-" + mcity + "-" + mdistrict + "-" + mlongitude + "-" + mlatitude + "-"
						+ jsonArray.length()).getBytes(), "d:/ll.txt", true);

				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("=======================================================================");
			}
			//
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void regDream() {
		Map<String, String> cookies = new HashMap<String, String>();

		Map<String, String> accountInfo = new HashMap<String, String>();
		accountInfo.put("username", "17091172059");
		accountInfo.put("password", "qwe123");
		accountInfo.put("deviceId", "99999999-1f0f-07be-397c-1c2b6a237faa");
		accountInfo.put("invitationCode", "10137444");
		accountInfo.put("mediaType", "1");

		// {"isEncryption":0,"data":{"code":0,"msg":"","jsessionid":"null","data":{"uniFlag":"1495791445161"}}}
		accountInfo.put("industryName", "学生");
		accountInfo.put("industryCode", "1000400000");
		// String result1 = ShuaLianHelper.regComit("174613", accountInfo,
		// cookies);
		// System.out.println(result1);
	}

	@Test
	public void loginDream() {

		// android_id,SimSerialNumber,imei:e48d275a693b2ff2,89860018111551029244,867979021433112

		String androidId = "e48d288a693b8888";
		String simSerialNumber = "89860018116751028888";
		String imei = "867979991438888";

		// String deviceId = "00000000-1f5f-07be-8837-1c2b6a237fff";//
		// 18049511425//

		Map<String, String> accountInfo = new HashMap<String, String>();
		Map<String, String> cookies = new HashMap<String, String>();
		// String deviceId = SLJsonHelper.getUUID(androidId, simSerialNumber,
		// imei);
		// accountInfo.put("deviceId", deviceId);

		// accountInfo.put("username", "15888813151");
		// accountInfo.put("password", "qwe123");

		// accountInfo.put("deviceId", "99999999-1f0f-07be-357c-1c2b6a997faa");

		// String result1 = ShuaLianHelper.checkMessageCode("919583",
		// accountInfo, cookies);
		// System.out.println(result1);

		// String resultss2 = ShuaLianHelper.login(accountInfo, cookies);
		// System.out.println(resultss2);
		//
		JSONObject result;
		// //

		// result = HeadlessException.login("15619427723", "qwe123",
		// "00000000-0000-0000-0000-000000000000");

		// print(accountInfo, cookies, result);

		// result = ShuaLianHelper.messageCode("15619427723",
		// "00000000-0000-0000-0000-000000000001");
		////
		// print(accountInfo, cookies, result);

		// result = ShuaLianHelper.checkMessageCode("15619427723", "016136",
		// "00000000-0000-0000-0000-000000000000");
		// System.out.println(result);

		// System.out.println(
		// MessageFormat.format(ShuaLianInfo.activeInvitationCode_4P991,
		// "9999"));

	}
}
