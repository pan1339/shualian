package com.dajing.aa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dajing.base64.AndroidBase64;
import com.dajing.bean.SLAccount;
import com.dajing.db.DbHelper;
import com.dajing.ocr.shualian.SLOCRService;
import com.dajing.shualian.ShuaLianHelper;
import com.dajing.util.FileUtil;
import com.dajing.util.TimeUtil;

public class SLThreadRunNew {
	private static Logger logger = Logger.getLogger(SLThreadRunNew.class);

	public static void run(int[] areaids, String password, int threadNum, int perThreadDelayTime, int randomSecond,
			String dbType, String accountPath) {
		// 创建一个线程池
		ExecutorService pool = Executors.newFixedThreadPool(threadNum);

		ShuaLianHelper shuaLianHelper = new ShuaLianHelper();

		DbHelper dbHelper = new DbHelper(dbType);
		SLOCRService slocrService = new SLOCRService();

		List<SLAccount> accounts = getAccountFromFile(accountPath, password);

		int indexAccount = 0;
		for (SLAccount account : accounts) {
			String username = account.getUsername();
			String uuid = account.getUuid();
			String mPassword = account.getPassword();
			if (!dbHelper.checkUsername(username)) {
				dbHelper.insertUsername(username, mPassword, "", "", "", uuid);
			}
			pool.execute(
					SLThreadRunNew.SLAnswer(username, areaids, randomSecond, shuaLianHelper, dbHelper, slocrService));
			if (accounts.size() > 1 && indexAccount < 2) {
				delay(perThreadDelayTime);
			} else {
				delay(25);
			}
			indexAccount++;
		}
		pool.shutdown();
	}

	private static List<SLAccount> getAccountFromFile(String path, String password) {

		File file = new File(path);

		List<SLAccount> accounts = new ArrayList<SLAccount>();

		try {
			BufferedReader bw = new BufferedReader(new FileReader(file));
			String line = null;
			// 因为不知道有几行数据，所以先存入list集合中
			while ((line = bw.readLine()) != null) {
				line = line.trim();
				if (line != null && !"".endsWith(line)) {
					if (line.contains("----")) {
						String[] acs = line.trim().split("----");
						if (acs.length == 2) {
							if (isPhoneNumberValid(acs[0])) {
								accounts.add(new SLAccount(acs[0], password, acs[1]));
							}
						} else if (acs.length == 3) {
							if (isPhoneNumberValid(acs[0])) {
								accounts.add(new SLAccount(acs[0], acs[1], acs[2]));
							}
						}
					} else {
						if (isPhoneNumberValid(line)) {
							accounts.add(new SLAccount(line));
						}
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accounts;

	}

	/**
	 * 过滤手机号码
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;

		if (phoneNumber == null) {
			return false;
		}

		// String expression = "^//(?(//d{3})//)?[- ]?(//d{3})[- ]?(//d{5})$";
		// String expression2 = "^//(?(//d{3})//)?[- ]?(//d{4})[- ]?(//d{4})$";
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;

		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static Runnable SLAnswer(String username, int[] areaids, int randomSecond, ShuaLianHelper shuaLianHelper,
			DbHelper dbHelper, SLOCRService slocrService) {
		return new Runnable() {
			@Override
			public void run() {
				slAnswer(username, areaids, randomSecond, shuaLianHelper, dbHelper, slocrService);
			}
		};
	}

	public static void slAnswer(String username, int[] areaids, int randomSecond, ShuaLianHelper shuaLianHelper,
			DbHelper dbHelper, SLOCRService slocrService) {
		for (int areaid : areaids) {
			slAnswer(username, areaid, randomSecond, shuaLianHelper, dbHelper, slocrService);
		}
	}

	public static void slAnswer(String username, int areaid, int randomSecond, ShuaLianHelper shuaLianHelper,
			DbHelper dbHelper, SLOCRService slocrService) {

		String area = initArea(dbHelper.queryArea(areaid), shuaLianHelper);

		/**
		 * 从数据库中查询用户信息
		 */
		JSONObject object = dbHelper.queryUsername(username);
		if (object == null) {
			logger.error("数据库总不存在" + username + "账号");
			return;
		}
		String password = object.optString("password");
		String deviceId = object.optString("deviceId");
		String castgc = object.optString("castgc");
		String sessionId = object.optString("sessionId");
		String shopId = object.optString("shopId");
		int userId = object.optInt("id");

		Map<String, String> cookiesPhp = new HashMap<String, String>();
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put("CASTGC", castgc);
		cookies.put("SESSION", sessionId);

		int vedionum = 500;

		try {
			String result = null;

			// result = shuaLianHelper.androidPathStart(cookiesPhp);
			// logger.info("androidPathStart result:" + result);
			//
			// result = shuaLianHelper.departments(sessionId, cookies);
			// logger.info("departments result:" + result);
			//
			// // result = shuaLianHelper.userTest(sessionId, cookies);
			// // logger.info("userTest result:" + result);
			//
			// result = shuaLianHelper.appStart(deviceId, cookiesPhp);
			// logger.info("appStart result:" + result);

			result = shuaLianHelper.uptokenAll(sessionId, cookies);
			logger.info("uptokenAll result:" + result);

			result = shuaLianHelper.initInfo(sessionId, cookies);
			logger.info("获取用户信息 result:" + result);

			// shuaLianHelper.login(username, password, deviceId, cookies);
			JSONObject resultJson = null;
			int resultCode = 0;
			if (result != null) {
				resultJson = new JSONObject(result);
				resultCode = resultJson.optJSONObject("data").optInt("code");
			}

			if (result == null || resultCode == 1001) {
				boolean check = checkLogin(shuaLianHelper, username, password, deviceId, dbHelper, cookies);
				if (!check) {
					return;
				}
				/**
				 * 从数据库中查询用户信息
				 */
				object = dbHelper.queryUsername(username);
				if (object == null) {
					logger.error("数据库总不存在" + username + "账号");
					return;
				}
				password = object.optString("password");
				deviceId = object.optString("deviceId");
				// deviceId = "00000000-0000-0000-0000-000000000000";
				castgc = object.optString("castgc");
				sessionId = object.optString("sessionId");
				shopId = object.optString("shopId");
				userId = object.optInt("id");

				cookiesPhp = new HashMap<String, String>();
				cookies = new HashMap<String, String>();
				cookies.put("CASTGC", castgc);
				cookies.put("SESSION", sessionId);

				result = shuaLianHelper.initInfo(sessionId, cookies);
				resultJson = new JSONObject(result);
			} else if (resultCode == 0) {

			} else {
				logger.info("登陆不成功,未知错误:" + result);
				return;
			}

			loginInit(shuaLianHelper, userId, deviceId, sessionId, cookies);

			shopId = resultJson.optJSONObject("data").optJSONObject("data").optJSONObject("shop").optString("no");
			logger.info("获取用户信息 shopId:" + shopId);

			// 1 littlepluginIndex
			result = shuaLianHelper.littlepluginIndex(area, shopId, cookies);
			// FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());

			String referer = "https://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/index.htm?area="
					+ URLEncoder.encode(area);

			// 2 获取广告列表
			result = shuaLianHelper.getVideoList(vedionum, area, shopId, referer, cookies);
			// logger.info("获取广告列表 getVideoList:" + result);

			// 3 productStatistics 小插件广场页
			shuaLianHelper.productStatistics(username, userId, System.currentTimeMillis(), 1001, "小插件广场页", referer,
					cookies);

			shuaLianHelper.cnzz(
					"https://s95.cnzz.com/z_stat.php?id=1258715214&web_id=1258715214&_=" + System.currentTimeMillis(),
					referer, cookies);

			resultJson = new JSONObject(result);
			vedionum = resultJson.optJSONObject("info").optInt("totalRow");

			logger.info("获取广告列表 " + vedionum + ", getVideoList:" + result);

			JSONArray array = resultJson.optJSONObject("info").optJSONArray("datas");

			for (int adIndex = array.length() - 1; adIndex >= 0; adIndex--) {

				JSONObject adJson = array.optJSONObject(adIndex);
				logger.info(adJson.toString());

				int adId = adJson.optInt("adId");

				/*
				 * 判断是否已经答题
				 */
				if (dbHelper.checkWork(adId, username)) {
					logger.info(vedionum + ", " + adIndex + ", " + username + ", " + adId + ", 已经答题");
					continue;
				}

				String adTitle = adJson.optString("adTitle");
				String imgUrl = adJson.optString("imgUrl");
				String industry = adJson.optString("industry");
				String position = adJson.optString("position");

				int adUserId = adJson.optInt("userId");
				String adUserName = adJson.optString("userName");
				String userPhotoUrl = adJson.optString("userPhotoUrl");
				double videoTime = adJson.optDouble("videoTime");
				int visitCount = adJson.optInt("visitCount");

				if (!dbHelper.checkAd(adId)) {
					dbHelper.insertAd(areaid, adId, adTitle, imgUrl, industry, position, adUserId, adUserName,
							userPhotoUrl, videoTime, visitCount);
				}

				// tishi

				///////////////////////////////// 第二步进入单个视频详细页/////////////////////////////////////////////////

				// 1 littlepluginDetail详细页
				result = shuaLianHelper.littlepluginDetail(adId, adUserId, vedionum, shopId, cookies);
				FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());

				referer = "https://www.o2osl.com/wap/" + shopId + "/activity/littleplugin/detail.htm?adId=" + adId
						+ "&userId=" + adUserId + "&pageSize=20&start=20&totalRow=" + vedionum;

				// 2 videoDetail 获取视频详细信息
				result = shuaLianHelper.videoDetail(adId, adUserId, shopId, referer, cookies);
				logger.info("第二步单个视频详页 getVideoList:" + result);

				/*
				 * 解析视频的statusCode,判断是否已经看过或者异常
				 */
				resultJson = new JSONObject(result);
				int statusCode = resultJson.optJSONObject("info").optJSONObject("ad").optInt("code");
				String videoUrl = resultJson.optJSONObject("info").optJSONObject("ad").optString("videoUrl");
				// videoTime
				Double myVideoTime = resultJson.optJSONObject("info").optJSONObject("ad").optDouble("videoTime");

				String adDesc = parserVideoDesc(result, adUserId, cookies, sessionId, shuaLianHelper);

				logger.info("视频描述 videoDesc:" + adDesc);

				int sleepTime = 0;
				if (myVideoTime < 15) {
					sleepTime = 18;
				} else {
					sleepTime = (int) (myVideoTime + 2);
				}

				/*
				 * 只有当statusCode为0时才继续
				 */
				if (statusCode == 0 || statusCode == 1128 //
						|| statusCode == 1131 // 进入鱼塘开始养鱼计划即可升级为平台的信任用户，信任用户可观看更多小插件并领取答题奖励！
						|| statusCode == 1132// 今日答题数量已达上限，明日再来吧！
				// || statusCode == 1120// 你的其它帐号已经领取过奖励了
				) {

				} else {// 已经答题错误
					/*
					 * 保存已经答题日志1109错误3次,1112已经答题
					 */
					if (statusCode == 1112 || statusCode == 1109) {
						dbHelper.insertWork(adId, username);
					}
					logger.error("视频详细页错误提示 statusCode:" + statusCode + ", " + result);
					continue;
				}

				delay(4, randomSecond);

				// 解析答案
				// List<JSONObject> answerList = parserAnswer(result, adUserId);

				// 3 tjwxxcj
				result = shuaLianHelper.tjwxxcj(shopId, referer, cookies);
				// logger.info(result);

				// 4 productStatistics视频详情页
				// {"data":[{"userId":16127175,"userAccount":"18049511425","pageId":1002,"pageName":"视频详情页","pageViewTime":1496514397280,"deviceType":4}]}

				long pageViewTime = System.currentTimeMillis();
				result = shuaLianHelper.productStatistics(username, userId, pageViewTime, 1002, "视频详情页", referer,
						cookies);
				// logger.info(result);

				delay(3, randomSecond);
				///////////////////////////////// 第三步进入单个视频mp4/////////////////////////////////////////////////

				/*
				 * 从数据库中重新排序答案
				 */
				// logger.info(answerList);
				// answerList = checkAnswer(adId, answerList, dbHelper);
				// logger.info(answerList);
				/*
				 * 遍历答题
				 */

				int imageCodeErrorTimes = 0;

				// 1 recordAdPageView
				result = shuaLianHelper.recordAdPageView(adId, shopId, referer, cookies);
				// logger.info("recordAdPageView" + result);
				// 2 productStatisticsVideo
				result = shuaLianHelper.productStatisticsVideo(username, userId, pageViewTime, 1002, "视频详情页", referer,
						cookies);
				// logger.info("productStatisticsVideo:" + result);

				result = shuaLianHelper.video(videoUrl, referer, cookies);
				// logger.info("video:" + result);

				delay(sleepTime, randomSecond);

				/////////////////// 获取题目/////////////////////////////////////////////////////

				int answerErrorTimes = 0;

				while (true) {

					result = shuaLianHelper.getAdInfo(adId, sessionId, cookies);
					logger.info("getAdInfo:" + result);

					JSONObject answer = parserAnswer(adId, result, adUserId, adDesc, dbHelper);

					// delay();
					///////// 获取验证码////////////////////////////////////////////////////////////////////////

					result = shuaLianHelper.verifyImage(80, 80, sessionId, cookies);
					// System.out.println(result);

					JSONObject verifyImageJsonObject = new JSONObject(result);
					JSONArray imageList = verifyImageJsonObject.optJSONObject("data").optJSONObject("data")
							.optJSONArray("imageList");

					String ocrCode = null;
					if (imageList != null) {
						StringBuilder stringBuilder = new StringBuilder();
						for (int picIndex = 0; picIndex < imageList.length(); picIndex++) {
							byte[] imageByte = AndroidBase64.decode(imageList.optString(picIndex), 2);
							String appStr = "_";
							if (picIndex == 3) {
								appStr = "";
							}
							stringBuilder.append(slocrService.checkCodeByByte(imageByte, 0) + appStr);
						}
						ocrCode = stringBuilder.toString();
						logger.info("verifyIma ocrCode:" + ocrCode);
					}

					delay(4, randomSecond);

					////////////////////////////////////////////////////////////////////////

					int questionId_1 = answer.optInt("questionId_1");
					String questionContent_1 = answer.optString("questionContent_1");
					int userAnswerId_1 = answer.optInt("userAnswerId_1");
					String userAnswerContent_1 = answer.optString("userAnswerContent_1");

					int questionId_2 = answer.optInt("questionId_2");
					String questionContent_2 = answer.optString("questionContent_2");
					int userAnswerId_2 = answer.optInt("userAnswerId_2");
					String userAnswerContent_2 = answer.optString("userAnswerContent_2");

					result = shuaLianHelper.answerQuestions(adId, questionId_1, userAnswerId_1, questionId_2,
							userAnswerId_2, ocrCode, sessionId, area, cookies, true);

					logger.info(vedionum + ", " + adIndex + ", answerQuestions:" + result);

					// 判断答题结果
					JSONObject answerQuestionsJsonObject = new JSONObject(result);
					boolean status = answerQuestionsJsonObject.optJSONObject("data").optJSONObject("data")
							.optBoolean("success");

					if (status) {

						/*
						 * 保存答案
						 */
						boolean exists = dbHelper.checkAnswer(adId, questionContent_1, userAnswerContent_1);
						if (!exists) {
							dbHelper.insertAnswer(adId, questionId_1, questionContent_1, userAnswerId_1,
									userAnswerContent_1);
						}
						exists = dbHelper.checkAnswer(adIndex, questionContent_2, userAnswerContent_2);
						if (!exists) {
							dbHelper.insertAnswer(adId, questionId_2, questionContent_2, userAnswerId_2,
									userAnswerContent_2);
						}

						/*
						 * 保存答题日志
						 */
						dbHelper.insertAnswerLog(username, adId, questionId_1, questionContent_1, userAnswerId_1,
								userAnswerContent_1, questionId_2, questionContent_2, userAnswerId_2,
								userAnswerContent_2, 1, result);
						/*
						 * 保存已经答题日志
						 */
						dbHelper.insertWork(adId, username);

						result = shuaLianHelper.productStatisticsVideo(username, userId, pageViewTime, 1003, "答题成功页",
								referer, cookies);
						// logger.info(result);

						logger.info(vedionum + ", " + adIndex + ", " + username + ", 答题成功:success=" + status);
						logger.info(
								"///////////////////////////////我是分割线/////////////////////////////////////////////////////////////");
						delay(3, randomSecond);
						break;
					} else {
						delay(3, randomSecond);
						int code = answerQuestionsJsonObject.optJSONObject("data").optInt("code");
						if (code == 1112) {// 已经答题错误
							dbHelper.insertAnswerLog(username, adId, questionId_1, questionContent_1, userAnswerId_1,
									userAnswerContent_1, questionId_2, questionContent_2, userAnswerId_2,
									userAnswerContent_2, 2, result);
							/*
							 * 保存已经答题日志
							 */
							dbHelper.insertWork(adId, username);
							imageCodeErrorTimes = 0;
							break;
						} else if (code == 1116) {// // 请输入正确的图形验证码 请点击图片旋转至正向朝上

							imageCodeErrorTimes++;
							if (imageCodeErrorTimes >= 4) {
								result = shuaLianHelper.login(username, password, deviceId, cookies);
								logger.info(username + ", 用户登录 result:" + result);
								resultJson = new JSONObject(result);
								sessionId = resultJson.optJSONObject("data").optString("jsessionid");
								castgc = resultJson.optJSONObject("data").optJSONObject("data").optString("castgc");
								userId = resultJson.optJSONObject("data").optJSONObject("data").optInt("userId");
								cookies.put("SESSION", sessionId);
								cookies.put("CASTGC", castgc);

								////////////////////////////////// 获取用户信息//////////////////////////////////////////////////////
								result = shuaLianHelper.initInfo(sessionId, cookies);
								logger.info(username + ", 获取用户信息 result:" + result);
								resultJson = new JSONObject(result);
								shopId = resultJson.optJSONObject("data").optJSONObject("data").optJSONObject("shop")
										.optString("no");
								logger.info(username + ", 获取用户信息 shopId:" + shopId);
								imageCodeErrorTimes = 0;
							}

						} else if (code == 1109) {// 答错3次啦，无法获得奖励
							imageCodeErrorTimes = 0;
							dbHelper.insertAnswerLog(username, adId, questionId_1, questionContent_1, userAnswerId_1,
									userAnswerContent_1, questionId_2, questionContent_2, userAnswerId_2,
									userAnswerContent_2, 3, result);
							/*
							 * 保存已经答题日志
							 */
							dbHelper.insertWork(adId, username);

							break;
						} else if (code == 1111) {
							dbHelper.insertAnswerLog(username, adId, questionId_1, questionContent_1, userAnswerId_1,
									userAnswerContent_1, questionId_2, questionContent_2, userAnswerId_2,
									userAnswerContent_2, 4, result);
							break;

						} else if (code == 1110) {// 很遗憾，答错了哦
							answerErrorTimes++;

							dbHelper.insertAnswerLog(username, adId, questionId_1, questionContent_1, userAnswerId_1,
									userAnswerContent_1, questionId_2, questionContent_2, userAnswerId_2,
									userAnswerContent_2, 4, result);

							if (answerErrorTimes >= 2) {
								System.err.println("连续答题错误两次, " + username + ", " + adId);
								break;
							}
							continue;
						} else if (code == 1120) {// 你的其它帐号已经领取过奖励了
							return;
						} else if (code == 1131) {// 进入鱼塘开始养鱼计划即可升级为平台的信任用户，信任用户可观看更多小插件并领取答题奖励！
							return;
						} else if (code == 1132) {// 今日答题数量已达上限，明日再来吧！
							return;
						} else if (code == 1127) {// 请求间隔时间太短
						} else if (code == 1123) {// 答题超时
						} else if (code == 1124) {// 尊敬的用户，为了维护良好的小插件使用环境，每位用户仅可领取每日初始定位区域（限制到区）的小插件答题奖励
							return;
						} else if (code == 1129) {// 您的版本过低，为了不影响您的正常使用请尽快升级至最新版本！
							return;
						} else {
							return;
						}

						dbHelper.insertAnswerLog(username, adId, questionId_1, questionContent_1, userAnswerId_1,
								userAnswerContent_1, questionId_2, questionContent_2, userAnswerId_2,
								userAnswerContent_2, 0, result);

					}
					delay(5 + randomSecond);
				}
			}
		} catch (

		JSONException e) {
			e.printStackTrace();
		}

	}

	private static boolean checkLogin(ShuaLianHelper shuaLianHelper, String username, String password, String deviceId,
			DbHelper dbHelper, Map<String, String> cookies) {
		String result = shuaLianHelper.login(username, password, deviceId, cookies);
		System.out.println(cookies);
		logger.info(username + ", " + password + ", " + result);

		try {
			JSONObject resultJson = new JSONObject(result);
			resultJson = resultJson.optJSONObject("data");
			int resultCode = resultJson.optInt("code");
			if (resultCode == 0) {
				logger.info("登陆成功" + username);
				String session = resultJson.optString("jsessionid");
				resultJson = resultJson.optJSONObject("data");
				String castgc = resultJson.optString("castgc");
				int userId = resultJson.optInt("userId");
				int imUserId = resultJson.optJSONObject("Imdata").optInt("imUserId");
				String imPassword = resultJson.optJSONObject("Imdata").optString("password");
				dbHelper.updateUserLoginInfo(username, session, castgc, userId, imUserId, imPassword);

			} else if (resultCode == 1144) {
				result = shuaLianHelper.messageCode(username, deviceId);
				String saveStr = "首次登陆下发短信:" + username + ", " + result;
				logger.info(saveStr);
				logger.info(FileUtil.getRootPath() + "error.txt");
				FileUtil.saveFile((TimeUtil.getCurrentTime() + ", " + saveStr).getBytes(),
						FileUtil.getRootPath() + "error.txt", true);
				return false;
			} else {
				FileUtil.saveFile((TimeUtil.getCurrentTime() + ", " + username + ", " + result).getBytes(),
						FileUtil.getRootPath() + "error.txt", true);
				return false;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return true;
	}

	private static void loginInit(ShuaLianHelper shuaLianHelper, int userId, String deviceId, String sessionId,
			Map<String, String> cookies) {
		String loginLog = shuaLianHelper.contactsUpdate(deviceId, sessionId, cookies);
		logger.info("contactsUpdate result:" + loginLog);

		loginLog = shuaLianHelper.isUpgrade(sessionId, cookies);
		logger.info("isUpgrade result:" + loginLog);

		loginLog = shuaLianHelper.userSignIn(userId, sessionId, cookies);
		logger.info("userSignIn result:" + loginLog);

		loginLog = shuaLianHelper.category(sessionId, cookies);
		logger.info("category result:" + loginLog);

		loginLog = shuaLianHelper.getTags(sessionId, cookies);
		logger.info("getTags result:" + loginLog);

	}

	/**
	 * 从数据库中重新排序答案
	 * 
	 * @param answerList
	 * @param dbHelper
	 */
	private static List<JSONObject> checkAnswer(int adId, List<JSONObject> answerList, DbHelper dbHelper) {

		List<JSONObject> list = new ArrayList<JSONObject>();

		System.err.println("in:" + answerList.toString());

		for (JSONObject object : answerList) {
			int sub = 0;
			String questionContent_1 = object.optString("questionContent_1");
			String userAnswerId_1 = object.optString("userAnswerId_1");
			String userAnswerContent_1 = object.optString("userAnswerContent_1");
			String questionContent_2 = object.optString("questionContent_2");
			String userAnswerId_2 = object.optString("userAnswerId_2");
			String userAnswerContent_2 = object.optString("userAnswerContent_2");

			System.err.println("object:" + object);

			JSONObject question_1 = dbHelper.queryAnswer(adId, questionContent_1);

			System.err.println("question_1:" + question_1);

			if (question_1 != null && userAnswerContent_1.equals(question_1.optString("answerContent"))) {
				sub = sub + 1;
			}

			JSONObject question_2 = dbHelper.queryAnswer(adId, questionContent_2);

			System.err.println("question_2:" + question_2);
			if (question_2 != null && userAnswerContent_2.equals(question_2.optString("answerContent"))) {
				sub = sub + 1;
			}

			if (sub == 2) {
				list.add(0, object);
			} else if (sub == 1) {
				if (list.size() > 0) {
					list.add(1, object);
				} else {
					list.add(0, object);
				}
			} else {
				list.add(list.size(), object);
			}
		}
		System.err.println("out:" + list);

		return list;

	}

	/**
	 * 从数据库中重新排序答案
	 * 
	 * @param answerList
	 * @param dbHelper
	 */
	private static List<JSONObject> checkAnswerLog(int adId, List<JSONObject> answerList, DbHelper dbHelper) {

		List<JSONObject> logs = dbHelper.queryAnswerLog(adId);

		Iterator<JSONObject> it = answerList.iterator();

		while (it.hasNext()) {

			JSONObject object = it.next();

			String questionContent_1 = object.optString("questionContent_1");
			String userAnswerId_1 = object.optString("userAnswerId_1");
			String userAnswerContent_1 = object.optString("userAnswerContent_1");
			String questionContent_2 = object.optString("questionContent_2");
			String userAnswerId_2 = object.optString("userAnswerId_2");
			String userAnswerContent_2 = object.optString("userAnswerContent_2");

			for (JSONObject log : logs) {
				String questionContent_log_1 = log.optString("questionContent_1");
				String userAnswerId_log_1 = log.optString("userAnswerId_1");
				String userAnswerContent_log_1 = log.optString("userAnswerContent_1");
				String questionContent_log_2 = log.optString("questionContent_2");
				String userAnswerId_log_2 = log.optString("userAnswerId_2");
				String userAnswerContent_log_2 = log.optString("userAnswerContent_2");

				if (questionContent_1.equals(questionContent_log_1)
						&& questionContent_2.equals(questionContent_log_2)) {
					if (userAnswerContent_1.equals(userAnswerContent_log_1)
							&& userAnswerContent_2.equals(userAnswerContent_log_2)) {
						it.remove();
						continue;
					}
				}
				if (questionContent_1.equals(questionContent_log_2)
						&& questionContent_2.equals(questionContent_log_1)) {
					if (userAnswerContent_1.equals(userAnswerContent_log_2)
							&& userAnswerContent_2.equals(userAnswerContent_log_1)) {
						it.remove();
						continue;
					}
				}
			}
		}
		return answerList;
	}

	/**
	 * 根据json生成area
	 * 
	 * @param object
	 * @return
	 */
	private static String initArea(JSONObject object, ShuaLianHelper shuaLianHelper) {
		String province = object.optString("province");
		String city = object.optString("city");
		String district = object.optString("district");
		double latitude = object.optDouble("latitude");
		double longitude = object.optDouble("longitude");

		String area = shuaLianHelper.makeArea(province, city, district, latitude, longitude);
		return area;
	}

	public static String getImageTimeStampPath() {
		return ShuaLianHelper.class.getResource("/").getPath().toString() + ShuaLianHelper.class.getSimpleName()
				+ System.currentTimeMillis() + ".html";
	}

	public static void delay(int second, int randomSecond) {
		try {
			Thread.sleep(1000 * second + (int) Math.random() * 1000 * randomSecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void delay(int second) {
		delay(second, 0);
	}

	public static JSONObject parserAnswer(int adId, String jsonStr, int adUserId, String adDesc, DbHelper dbHelper)
			throws JSONException {

		JSONObject returnJson = null;
		List<JSONObject> list = new ArrayList<JSONObject>();

		JSONObject resultJson = new JSONObject(jsonStr);

		JSONObject adJson = resultJson.optJSONObject("data").optJSONObject("data");
		JSONArray questionArray = adJson.optJSONArray("questionList");

		String questionId_1 = "";
		String questionContent_1 = "";
		String userAnswerId_1 = "";
		String userAnswerContent_1 = "";

		String questionId_2 = "";
		String questionContent_2 = "";
		String userAnswerId_2 = "";
		String userAnswerContent_2 = "";

		if (questionArray.length() == 2) {

			questionId_1 = questionArray.optJSONObject(0).optString("questionId");
			questionContent_1 = questionArray.optJSONObject(0).optString("questionContent");
			JSONArray userAnswerIdArray_1 = questionArray.optJSONObject(0).optJSONArray("answerList");

			questionId_2 = questionArray.optJSONObject(1).optString("questionId");
			questionContent_2 = questionArray.optJSONObject(1).optString("questionContent");
			JSONArray userAnswerIdArray_2 = questionArray.optJSONObject(1).optJSONArray("answerList");

			LinkedList<JSONObject> answerList_1 = sort(adDesc, userAnswerIdArray_1);
			boolean sortFlag_1 = sortFlag(adDesc, userAnswerIdArray_1);

			LinkedList<JSONObject> answerList_2 = sort(adDesc, userAnswerIdArray_2);
			boolean sortFlag_2 = sortFlag(adDesc, userAnswerIdArray_2);

			if (sortFlag_2) {
				for (JSONObject answer2 : answerList_2) {
					for (JSONObject answer1 : answerList_1) {

						userAnswerId_1 = answer1.optString("answerId");
						userAnswerContent_1 = answer1.optString("answerContent");

						userAnswerId_2 = answer2.optString("answerId");
						userAnswerContent_2 = answer2.optString("answerContent");

						JSONObject object = new JSONObject();
						object.put("questionId_1", questionId_1);
						object.put("questionContent_1", questionContent_1);
						object.put("userAnswerId_1", userAnswerId_1);
						object.put("userAnswerContent_1", userAnswerContent_1);

						object.put("questionId_2", questionId_2);
						object.put("questionContent_2", questionContent_2);
						object.put("userAnswerId_2", userAnswerId_2);
						object.put("userAnswerContent_2", userAnswerContent_2);
						list.add(object);
					}
				}

			} else {

				for (JSONObject answer1 : answerList_1) {
					for (JSONObject answer2 : answerList_2) {

						userAnswerId_1 = answer1.optString("answerId");
						userAnswerContent_1 = answer1.optString("answerContent");

						userAnswerId_2 = answer2.optString("answerId");
						userAnswerContent_2 = answer2.optString("answerContent");

						JSONObject object = new JSONObject();
						object.put("questionId_1", questionId_1);
						object.put("questionContent_1", questionContent_1);
						object.put("userAnswerId_1", userAnswerId_1);
						object.put("userAnswerContent_1", userAnswerContent_1);

						object.put("questionId_2", questionId_2);
						object.put("questionContent_2", questionContent_2);
						object.put("userAnswerId_2", userAnswerId_2);
						object.put("userAnswerContent_2", userAnswerContent_2);
						list.add(object);
					}
				}
			}
		}

		list = checkAnswer(adId, list, dbHelper);
		list = checkAnswerLog(adId, list, dbHelper);
		returnJson = list.get(0);
		return returnJson;

	}

	public static String parserVideoDesc(String jsonStr, int adUserId, Map<String, String> cookies, String jsessionid,
			ShuaLianHelper shuaLianHelper) throws JSONException {

		StringBuilder adDesc = new StringBuilder();

		JSONObject resultJson = new JSONObject(jsonStr);

		JSONObject adJson = resultJson.optJSONObject("info").optJSONObject("ad");

		String adTitle = adJson.optString("adTitle");
		// String skipContent = adJson.optString("skipContent");.

		String adDescription = adJson.optString("adDescription");
		// String shopInfo =
		// resultJson.optJSONObject("info").optJSONObject("shopInfo").toString();
		// String merchantName =
		// resultJson.optJSONObject("info").optString("merchantName");

		adDesc.append(adTitle + adDescription + adUserId
		// + shopInfo + merchantName
		);

		String result = shuaLianHelper.homePageDetail(adUserId, jsessionid, cookies);
		resultJson = new JSONObject(result);
		String loginAccount = resultJson.optJSONObject("data").optJSONObject("data").optString("loginAccount");

		result = shuaLianHelper.showCard(loginAccount, jsessionid, cookies);
		resultJson = new JSONObject(result);

		JSONObject card = resultJson.optJSONObject("data").optJSONObject("data").optJSONObject("card");
		String addressDetail = card.optString("addressDetail");
		String companyName = card.optString("companyName");
		String industryName = card.optString("industryName");
		String industryProvide = card.optString("industryProvide");

		JSONObject user = resultJson.optJSONObject("data").optJSONObject("data").optJSONObject("user");
		int id = user.optInt("id");
		String myInvitationCode = user.optString("myInvitationCode");
		String nickname = user.optString("nickname");
		String contacts = user.optString("contacts");

		String fishPondName = resultJson.optJSONObject("data").optJSONObject("data").optString("fishPondName");
		String shopName = resultJson.optJSONObject("data").optJSONObject("data").optString("shopName");

		adDesc.append(addressDetail + companyName + industryName + industryProvide);
		adDesc.append(id + myInvitationCode + nickname + contacts + fishPondName + shopName);

		return adDesc.toString();

	}

	private static LinkedList<JSONObject> sort(String adDescription, JSONArray userAnswerIdArray) {
		LinkedList<JSONObject> answerList = new LinkedList<JSONObject>();
		LinkedList<JSONObject> answerListUn = new LinkedList<JSONObject>();

		for (int i = 0; i < userAnswerIdArray.length(); i++) {
			JSONObject userAnswerJson = userAnswerIdArray.optJSONObject(i);
			if (adDescription.contains(userAnswerJson.optString("answerContent"))) {
				answerList.add(userAnswerJson);
			} else {
				answerListUn.add(userAnswerJson);
			}
		}
		answerList.addAll(answerListUn);
		return answerList;
	}

	private static boolean sortFlag(String adDescription, JSONArray userAnswerIdArray) {

		for (int i = 0; i < userAnswerIdArray.length(); i++) {
			JSONObject userAnswerJson = userAnswerIdArray.optJSONObject(i);
			if (adDescription.contains(userAnswerJson.optString("answerContent"))) {
				return true;
			}
		}
		return false;
	}

}
