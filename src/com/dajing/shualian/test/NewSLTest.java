package com.dajing.shualian.test;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dajing.image.BufferedImageService;
import com.dajing.shualian.SLJsonHelper;
import com.dajing.shualian.ShuaLianHelper;
import com.dajing.util.FileUtil;

public class NewSLTest {
	private ShuaLianHelper helper;

	long delayTime = 6 * 60 * 1000;
	int userId = 16127175;
	String username = "18049511425";
	String password = "";
	String deviceId = "00000000-1f0f-07be-357c-";
	int adId = 10594;
	int adUserId = 1562539;

	// http://www.lbwhds.com/wap/HZJZ1704271425h2RbS1/buy/littleplugin/index.htm?area=eyJwcm92aW5jZSI6IuWbm%2BW3neecgSIsImNpdHkiOiLmiJDpg73luIIiLCJkaXN0cmljdCI6Ium%2Bmeaziempv%2BWMuiJ9
	String shopId = "HZJZ1704271425h2RbS1";

	String province = "陕西省";
	String city = "渭南市";
	// 义乌市//29.306756,120.07514
	// 浦江县,29.479046,119.899383
	String district = "韩城市";
	double latitude = 35.480057;
	double longitude = 110.436424;

	String castgc = "TGT-4820472-OOOkNvjW9QQuW6FFaZ0cxAUxStKdeHnnk6bJHSYc3bVHLYus5k-cas";
	String sessionid = "61fd5e24-f5fd-440f-8b34-418e05a42eb3";

	Map<String, String> cookies;

	@Test
	public void loginTest() {

		// String androidid = SLJsonHelper.randomStr(16,
		// SLJsonHelper.radomStrL);
		// String imsi = SLJsonHelper.randomStr(20, SLJsonHelper.radomNumber);
		// String imei = SLJsonHelper.randomStr(15, SLJsonHelper.radomNumber);
		// String deviceId = SLJsonHelper.getUUID(androidid, imsi, imei);
		String deviceId = "ffffffff-8e05-40df-bd67-f1d74d9e86b4";

		username = "13586966660";
		password = "a01389569";
		String result = helper.login(username, password, deviceId, cookies);
		System.out.println(cookies);
		System.out.println(result);
		result = helper.messageCode(username, deviceId);
		System.out.println(result);
	}

	@Test
	public void initInfo() {

		String result = helper.initInfo(sessionid, cookies);
		System.out.println(cookies);
		System.out.println(result);
	}

	@Test
	public void littlepluginIndex() {
		String area = helper.makeArea(province, city, district, latitude, longitude);
		String result = helper.littlepluginIndex(area, shopId, cookies);
		System.out.println(cookies);
		// System.out.println(result);
		FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());
	}

	@Test
	public void getVideoList() {
		String area = helper.makeArea(province, city, district, latitude, longitude);
		String result = helper.getVideoList(area, shopId, "", cookies);
		System.out.println(result);
	}

	@Test
	public void productStatistics() {
		String result = helper.productStatistics(username, userId, System.currentTimeMillis(), 1001, "小插件广场页", "",
				cookies);
		System.out.println(result);
		FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());
	}

	@Test
	public void littlepluginDetail() {
		// UM_distinctid=15c6ebfe8c522-0079489068381b-21065766-49a10-15c6ebfe8c746;
		// CNZZDATA1258715214=293934699-1496506690-%7C1496506690
		// cookies.put("UM_distinctid",
		// "15c6ebfe8c522-0079489068381b-21065766-49a10-15c6ebfe8c746");
		// cookies.put("CNZZDATA1258715214",
		// "293934699-1496506690-%7C1496506690");
		String result = helper.littlepluginDetail(adId, adUserId, 1, shopId, cookies);
		// System.out.println(result);
		FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());
	}

	@Test
	public void videoDetail() {
		String result = helper.videoDetail(adId, adUserId, shopId, "", cookies);
		System.out.println(result);
	}

	@Test
	public void tjwxxcj() {
		String result = helper.tjwxxcj(shopId, "", cookies);
		System.out.println(result);
	}

	@Test
	public void productStatistics1() {
		String result = helper.productStatistics(username, userId, System.currentTimeMillis(), 1002, "视频详情页", "",
				cookies);
		System.out.println(result);
		// FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());
	}

	@Test
	public void recordAdPageView() {
		String result = helper.recordAdPageView(adId, shopId, "", cookies);
		System.out.println(result);
		// FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());
	}

	@Test
	public void productStatisticsVideo() {
		String result = helper.productStatisticsVideo(username, userId, System.currentTimeMillis(), 1002, "视频详情页", "",
				cookies);
		System.out.println(result);
		// FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());
	}

	@Test
	public void verifyIma() {
		String imagePath = helper.verifyIma(shopId, "", cookies);
		System.out.println(imagePath);

		List<int[]> pixelsListSrc = BufferedImageService
				.readAllPixelsDeviateWeightsArrayFromRoot("/config/ocrlib/images", "jpg");
		String ocrCode = check(imagePath, pixelsListSrc);
		System.out.println(ocrCode);
	}

	@Test
	public void answerQuestions() {
		// helper.makeArea(province, city, district, latitude, longitude);
		String area = "eyJwcm92aW5jZSI6IuWbm+W3neecgSIsImNpdHkiOiLmiJDpg73luIIiLCJkaXN0cmljdCI6Ium+meaziempv+WMuiJ9";
		String imagePath = helper.verifyIma(shopId, "", cookies);
		System.out.println(imagePath);

		List<int[]> pixelsListSrc = BufferedImageService
				.readAllPixelsDeviateWeightsArrayFromRoot("/config/ocrlib/images", "jpg");
		String ocrCode = check(imagePath, pixelsListSrc);
		System.out.println(ocrCode);

		// String result = helper.answerQuestions(adId, "73622", "31711",
		// "41857", "82058", ocrCode, shopId,
		// "eyJwcm92aW5jZSI6IuWbm+W3neecgSIsImNpdHkiOiLmiJDpg73luIIiLCJkaXN0cmljdCI6Ium+meaziempv+WMuiJ9",
		// "",
		// cookies);
		// System.out.println(result);
	}

	@Test
	public void comit() throws JSONException {

		String result = null;

		String area = "eyJwcm92aW5jZSI6Iua1t+WNl+ecgSIsImNpdHkiOiLmtbflj6PluIIiLCJkaXN0cmljdCI6IueQvOWxseWMuiIsImxhdGl0dWRlIjoyMC4wMDAwNTcsImxvbmdpdHVkZSI6MTEwLjM1NjQyNH0=";

		List<int[]> pixelsListSrc = BufferedImageService
				.readAllPixelsDeviateWeightsArrayFromRoot("/config/ocrlib/images", "jpg");

		////////////////////////////////// 初始化登陆///////////////////////////////////////////////////////
		result = helper.login(username, password, deviceId, cookies);
		System.out.println(result);
		System.out.println(cookies);

		JSONObject resultJson = new JSONObject(result);
		sessionid = resultJson.optJSONObject("data").optString("jsessionid");
		castgc = resultJson.optJSONObject("data").optJSONObject("data").optString("castgc");
		userId = resultJson.optJSONObject("data").optJSONObject("data").optInt("userId");
		cookies.put("SESSION", sessionid);
		cookies.put("CASTGC", castgc);

		////////////////////////////////// 获取用户信息//////////////////////////////////////////////////////
		result = helper.initInfo(sessionid, cookies);
		System.out.println(result);
		resultJson = new JSONObject(result);
		shopId = resultJson.optJSONObject("data").optJSONObject("data").optJSONObject("shop").optString("no");

		System.out.println(shopId);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		///////////////////////////////// 第一步进入锦囊妙计/////////////////////////////////////////////////

		// 1 littlepluginIndex
		result = helper.littlepluginIndex(area, shopId, cookies);
		System.out.println(cookies);
		FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());

		String referer = "http://www.lbwhds.com/wap/" + shopId + "/buy/littleplugin/index.htm?area="
				+ URLEncoder.encode(area);

		// 2 getVideoList
		result = helper.getVideoList(area, shopId, referer, cookies);
		System.out.println(result);

		resultJson = new JSONObject(result);
		int totalRow = resultJson.optJSONObject("info").optInt("totalRow");

		JSONArray array = resultJson.optJSONObject("info").optJSONArray("datas");
		resultJson = array.optJSONObject(13);

		adId = resultJson.optInt("adId");
		adUserId = resultJson.optInt("userId");

		// 3 productStatistics 小插件广场页
		// {"data":[{"userId":"16127175","userAccount":"18049511425","pageId":1001,"pageName":"小插件广场页","pageViewTime":1496513972624,"deviceType":4}]}
		result = helper.productStatistics(username, userId, System.currentTimeMillis(), 1001, "小插件广场页", referer,
				cookies);
		System.out.println(result);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		///////////////////////////////// 第二步进入单个视频详细页/////////////////////////////////////////////////

		// 1 littlepluginDetail详细页
		result = helper.littlepluginDetail(adId, adUserId, totalRow, shopId, cookies);
		// System.out.println(result);
		FileUtil.saveFile(result.getBytes(), getImageTimeStampPath());

		referer = "http://www.lbwhds.com/wap/" + shopId + "/activity/littleplugin/detail.htm?adId=" + adId + "&userId="
				+ adUserId + "&pageSize=20&start=20&totalRow=" + totalRow;
		// 2 videoDetail 获取视频详细信息
		result = helper.videoDetail(adId, adUserId, shopId, referer, cookies);
		System.out.println(result);

		resultJson = new JSONObject(result);
		array = resultJson.optJSONObject("info").optJSONObject("ad").optJSONArray("questionList");

		String adDescription = resultJson.optJSONObject("info").optJSONObject("ad").optString("adDescription");
		adDescription = resultJson.optJSONObject("info").optJSONObject("ad").optString("adTitle") + adDescription;

		String questionId_1 = "";
		String userAnswerId_1 = "";
		String questionId_2 = "";
		String userAnswerId_2 = "";
		for (int i = 0; i < array.length(); i++) {
			resultJson = array.optJSONObject(i);

			JSONArray userAnswerIdArray = resultJson.getJSONArray("answerList");

			if (i == 0) {
				questionId_1 = resultJson.optString("questionId");
				for (int j = 0; j < userAnswerIdArray.length(); j++) {
					String answerContent = userAnswerIdArray.optJSONObject(j).optString("answerContent");
					userAnswerId_1 = userAnswerIdArray.optJSONObject(j).optString("answerId");
					System.out.println(userAnswerId_1);
					if (adDescription.contains(answerContent)) {
						break;
					}
				}
			} else {
				questionId_2 = resultJson.optString("questionId");
				for (int j = 0; j < userAnswerIdArray.length(); j++) {
					String answerContent = userAnswerIdArray.optJSONObject(j).optString("answerContent");
					userAnswerId_2 = userAnswerIdArray.optJSONObject(j).optString("answerId");
					System.out.println(questionId_2);
					if (adDescription.contains(answerContent)) {
						break;
					}
				}
			}
			System.out.println(resultJson);
		}

		// 3 tjwxxcj
		result = helper.tjwxxcj(shopId, referer, cookies);
		System.out.println(result);

		// 4 productStatistics视频详情页
		// {"data":[{"userId":16127175,"userAccount":"18049511425","pageId":1002,"pageName":"视频详情页","pageViewTime":1496514397280,"deviceType":4}]}

		long pageViewTime = System.currentTimeMillis();
		result = helper.productStatistics(username, userId, pageViewTime, 1002, "视频详情页", referer, cookies);
		System.out.println(result);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		///////////////////////////////// 第三步进入单个视频mp4/////////////////////////////////////////////////

		// 1 recordAdPageView
		result = helper.recordAdPageView(adId, shopId, referer, cookies);
		System.out.println("recordAdPageView" + result);

		// 2 productStatisticsVideo

		result = helper.productStatisticsVideo(username, userId, pageViewTime, 1002, "视频详情页", referer, cookies);
		System.out.println(result);

		// {"data":[{"userId":16127175,"userAccount":"18049511425","pageId":1002,"pageName":"视频详情页","pageViewTime":1496515329064,"deviceType":4,
		// "buttonId":1003,"buttonName":"小插件播放","nextPageId":1007,"nextPageName":"小插件播放页","pageJumpStatus":1}]}

		try {
			Thread.sleep(17000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 3 verifyIma
		String imagePath = helper.verifyIma(shopId, referer, cookies);
		System.out.println(imagePath);
		String ocrCode = check(imagePath, pixelsListSrc);
		System.out.println(ocrCode);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 4 answerQuestions
		// adId, questionId_1, userAnswerId_1, questionId_2, userAnswerId_2,
		// verificationCode, shopId, area, String refererUrl, cookies

		// System.out.println(cookies);
		// result = helper.productStatisticsVideo(username, userId,
		// pageViewTime, 1003, "答题成功页", referer, cookies);
		System.out.println(result);
		// result = helper.answerQuestions(adId, questionId_1, userAnswerId_1,
		// questionId_2, userAnswerId_2, ocrCode,
		// shopId, area, referer, cookies);
		System.out.println(result);
		result = helper.productStatisticsVideo(username, userId, pageViewTime, 1003, "答题成功页", referer, cookies);

		System.out.println(result);
	}

	@Test

	public void commitTest() {
		// Cookie:
		// CASTGC=TGT-4696871-nGYkg5LxCELXrka9IeTrXQBiycx4PU2LKvydGoqAmZtQaDlyCR-cas;
		// SESSION=00696a16-2611-4d5d-a609-b4b4ef3755d4;
		// UM_distinctid=15c70fb1f411-078d52e41503bf-21065766-49a10-15c70fb1f43a1;
		// CNZZDATA1258715214=757338984-1496544125-%7C1496548972

		cookies.put("CASTGC", "TGT-4696871-nGYkg5LxCELXrka9IeTrXQBiycx4PU2LKvydGoqAmZtQaDlyCR-cas");
		cookies.put("SESSION", "00696a16-2611-4d5d-a609-b4b4ef3755d4");
		cookies.put("UM_distinctid", "15c70fb1f411-078d52e41503bf-21065766-49a10-15c70fb1f43a1");
		cookies.put("CNZZDATA1258715214", "757338984-1496544125-%7C1496548972");

		shopId = "HZJZ1704271425h2RbS1";
		String area = "eyJwcm92aW5jZSI6IuWbm+W3neecgSIsImNpdHkiOiLmiJDpg73luIIiLCJkaXN0cmljdCI6Ium+meaziempv+WMuiJ9";
		// String result = helper.answerQuestions(adId, "33371", "61176",
		// "60140", "8527", "2_1_3_0", shopId, area,
		// "http://www.lbwhds.com/wap/HZJZ1704271425h2RbS1/activity/littleplugin/detail.htm?adId=24814&userId=16585040&pageSize=20&start=20&totalRow=86",
		// cookies);
		// System.out.println(result);
	}

	@Before
	public void before() {
		this.helper = new ShuaLianHelper(true);
		cookies = new HashMap<String, String>();

		// cookies.put("SESSION", sessionid);
		// cookies.put("CASTGC", castgc);

		// cookies.put("UM_distinctid",
		// "15c6ebfe8c522-0079489068381b-21065766-49a10-15c6ebfe8c746");
		// cookies.put("CNZZDATA1258715214",
		// "293934699-1496506690-%7C1496506690");
	}

	@After
	public void after() {
	}

	public static String getImageTimeStampPath() {
		return ShuaLianHelper.class.getResource("/").getPath().toString() + ShuaLianHelper.class.getSimpleName()
				+ System.currentTimeMillis() + ".html";
	}

	private static String check(String imagePath, List<int[]> pixelsListSrc) {

		StringBuffer sb = new StringBuffer();

		BufferedImage bufferedImage = BufferedImageService.readPicture(imagePath);
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		int num = width / height;
		for (int i = 0; i < num; i++) {
			BufferedImage bi = clipImages(bufferedImage, 125 * i, 0, 125, 125);
			// 缩放至32*32图片
			bi = BufferedImageService.scale(bi);

			List<int[]> pixelsList = new ArrayList<int[]>();
			for (int j = 0; j < 4; j++) {
				BufferedImage bufferedImageRotate = BufferedImageService.rotate(bi, j * 90);
				int[] pixels = BufferedImageService.readPixelsDeviateWeightsArray(bufferedImageRotate);
				pixelsList.add(pixels);
			}

			int ocrCode = BufferedImageService.check(pixelsList, pixelsListSrc);
			if (i >= 3) {
				sb.append(ocrCode + "");
			} else {
				sb.append(ocrCode + "_");
			}

			// sb.append(BufferedImageService.check(pixelsListSrc, pixelsList));
		}
		return sb.toString();
	}

	/**
	 * 分割图片
	 */
	private static BufferedImage clipImages(BufferedImage bufferedImageSrc, int srcX, int srcY, int width, int height) {

		return BufferedImageService.clip(bufferedImageSrc, srcX, srcY, width, height);

	}

}
