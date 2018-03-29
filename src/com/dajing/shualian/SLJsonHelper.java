package com.dajing.shualian;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dajing.base64.AndroidBase64;

public class SLJsonHelper {

	private static Logger logger = Logger.getLogger(SLJsonHelper.class);
	public final static String radomStrL = "abcdefghijklmnopqrstuvwxyz0123456789";
	public final static String radomStrU = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public final static String radomStrUL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public final static String radomNumber = "0123456789";

	public static String getUUID(String androidId, String simSerialNumber, String imei) {

		long androidIdLong = (long) androidId.hashCode();
		long simSerialNumberLong = (long) simSerialNumber.hashCode();
		long imeiLong = (long) imei.hashCode() << 32;

		logger.debug(androidId + "," + simSerialNumber + "," + imei);
		String uuidStr = new UUID(androidIdLong, simSerialNumberLong | imeiLong).toString();
		logger.debug(androidIdLong + "," + (simSerialNumberLong | imeiLong) + ", " + uuidStr);
		return uuidStr;
	}

	/**
	 * 初始化登陆信息
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
//	public static String map2Json(Map<String, String> dataMap) {
//
//		JSONObject jSONObject = new JSONObject();
//		try {
//			if (dataMap != null) {
//				Iterator it = dataMap.entrySet().iterator();
//				while (it.hasNext()) {
//					Map.Entry entry = (Map.Entry) it.next();
//					String key = (String) entry.getKey();
//					String value = (String) entry.getValue();
//					jSONObject.put(key, value);
//
//				}
//			}
//			return jSONObject.toString();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * rsa机密处理data
	 * 
	 * @param data
	 * @return
	 */
	public static String date2RsaBase(String data, String transformation) {
		String result = null;
		byte[] dataByte = data.getBytes();
		if (data.length() >= 117) {

			int length = data.length();
			int hafLength = length / 2;

			String subStr1 = data.substring(0, hafLength);
			String subStr2 = data.substring(hafLength, length);

			subStr1 = date2RsaBase(subStr1, transformation);
			subStr2 = date2RsaBase(subStr2, transformation);
			result = subStr1 + "," + subStr2;
		} else {
			byte[] byts = RsaHelper.rsaInitENCRYPT_MODE(dataByte, transformation);
			result = AndroidBase64.encodeToString(byts, 0);

		}
		return result;
	}

	/**
	 * 初始化postJson字符串
	 * 
	 * @param data
	 * @param versionCode
	 * @param device
	 * @return
	 */
	public static String initPostJson(String data, int versionCode, int device) {
		JSONObject jSONObject = new JSONObject();
		String jsonStr = null;
		try {
			jSONObject.put("data", data);
			jSONObject.put("versionCode", versionCode);
			jSONObject.put("device", device);
			jsonStr = jSONObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	/**
	 * 随机字符串生成
	 * 
	 * @param length
	 * @return
	 */

	public static String randomStr(int length, String radomStr) {
		Random random = new Random();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i2 = 0; i2 < length; i2++) {
			stringBuffer.append(radomStr.charAt(random.nextInt(radomStr.length())));
		}
		return stringBuffer.toString();
	}
}
