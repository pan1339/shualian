package com.dajing.aa;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dajing.base64.AndroidBase64;
import com.dajing.db.DbHelper;
import com.dajing.ocr.OCRUtil;
import com.dajing.ocr.shualian.SLOCRService;
import com.dajing.shualian.SLJsonHelper;
import com.dajing.shualian.ShuaLianHelper;

public class LoginGetAdInfo {

	private static Logger logger = Logger.getLogger(LoginGetAdInfo.class);

	public static void main(String[] args) throws JSONException {

//		SLOCRService slocrService = new SLOCRService();

		String username = "13905721458";
		String password = "z123456";
		DbHelper dbHelper = new DbHelper();
		ShuaLianHelper helper = new ShuaLianHelper(true);
		Map<String, String> cookies = new HashMap<String, String>();

		JSONObject jsonObject = dbHelper.queryUsername(username);
		String deviceId = jsonObject.optString("deviceId");
		password = jsonObject.optString("password");

		String jsessionid = jsonObject.optString("sessionId");

		String result = helper.login(username, password, deviceId, cookies);
//		System.out.println(result);
		jsonObject = new JSONObject(result);
		
		int userId = jsonObject.optJSONObject("data").optJSONObject("data").optInt("userId");

		userId=17366937;
		result = helper.getAdInfo(31521, jsessionid, cookies);
		System.out.println(result);
		
		
		result = helper.shopCerPartInfo(userId, jsessionid, cookies);
		System.out.println(result);
		
		result = helper.homePageDetail(userId, jsessionid, cookies);
		System.out.println(result);
		
		jsonObject = new JSONObject(result);
		String loginAccount = jsonObject.optJSONObject("data").optJSONObject("data").optString("loginAccount");
		
		result =helper.showCard(loginAccount, jsessionid, cookies);
		System.out.println(result);

//		result = helper.verifyImage(80, 80, jsessionid, cookies);
//		// System.out.println(result);
//
//		JSONObject verifyImageJsonObject = new JSONObject(result);
//		JSONArray imageList = verifyImageJsonObject.optJSONObject("data").optJSONObject("data")
//				.optJSONArray("imageList");
//
//		String ocrCode = null;
//		if (imageList != null) {
//			StringBuilder stringBuilder = new StringBuilder();
//			for (int picIndex = 0; picIndex < imageList.length(); picIndex++) {
//				byte[] imageByte = AndroidBase64.decode(imageList.optString(picIndex), 2);
//				String appStr = "_";
//				if (picIndex == 3) {
//					appStr = "";
//				}
//				stringBuilder.append(slocrService.checkCodeByByte(imageByte, 0) + appStr);
//			}
//			ocrCode = stringBuilder.toString();
//			logger.info("verifyIma ocrCode:" + ocrCode);
//		}


	}

	

}
