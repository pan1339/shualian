package com.dajing.shualian;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.dajing.jsoup.JsoupService;
import com.dajing.jsoup.JsoupService.DJsoupKey;
import com.dajing.jsoup.JsoupServiceImpl;
import com.dajing.ocr.OCRUtil;

/**
 * 请求需要设置头字段,不然json数据没法识别
 * 
 * @author Administrator
 * 
 *
 */
public class ShuaLianHelper {

	private static JsoupService jsoupService;

	private static Logger logger = Logger.getLogger(ShuaLianHelper.class);

	/**
	 * 用于调试判断,设置代理
	 */
	private boolean isProxy;

	public ShuaLianHelper(boolean isProxy) {
		this.isProxy = isProxy;
		initHttps();
	}

	public ShuaLianHelper() {
		initHttps();
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(boolean isProxy) {
		this.isProxy = isProxy;
	}

	static {
		if (jsoupService == null) {
			jsoupService = new JsoupServiceImpl();
		}
	}

	private void proxy(Connection conn) {
		if (isProxy) {
			conn.proxy("192.168.3.84", 8888);
		}
	}

	/**
	 * 1.一个方法不能改变一个基本数据类型的参数（即数值型和布尔型）
	 * <p/>
	 * 2.一个方法可以改变一个对象参数的状态
	 * <p/>
	 * 3.一个方法不能让对象参数引用一个新的对象
	 */
	private JSONObject initRsaData(JSONObject dataJson) {

		String datasStr = SLJsonHelper.date2RsaBase(dataJson.toString(), RsaHelper.FORMATION_NoPadding);// data进行加密

		dataJson = new JSONObject();

		try {
			dataJson.put("data", datasStr);
			dataJson.put("versionCode", 550);
			dataJson.put("device", 1);
			// dataJson.put("source", 1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataJson;
	}

	/**
	 * 
	 * @param byteData
	 * @param imagePath
	 * @return
	 */
	public static boolean saveOcrImage(byte[] byteData, String orcImagePath) {
		File outFile = new File(orcImagePath);
		OutputStream os = null;
		try {
			os = new FileOutputStream(outFile);
			os.write(byteData);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;

	}

	/**
	 * 账号登陆
	 * 
	 * @param username
	 * @param password
	 * @return
	 */

	public String login(String username, String password, String deviceId, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "http://www.o2osl.com/assistant/j_spring_ass_check";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("username", username);
			datas.put("password", password);
			datas.put("deviceId", deviceId);
			String randomNum = SLJsonHelper.randomStr(10, SLJsonHelper.radomStrUL) + "_" + System.currentTimeMillis();
			datas.put("randomNum", randomNum);

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

			JSONObject resultJson = new JSONObject(result);
			resultJson = resultJson.optJSONObject("data");
			int resultCode = resultJson.optInt("code");
			if (resultCode == 0) {
				logger.debug("登陆成功" + username);
				resultJson = resultJson.optJSONObject("data");
				String castgc = resultJson.optString("castgc");
				cookies.put("CASTGC", castgc);
			} else if (resultCode == 1144) {
			}

		} catch (JSONException e) {
			e.printStackTrace();

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	/**
	 * http://www.o2osl.com/assistant/ass/initInfo.json;jsessionid=973c6e3a-38f0-4873-b06c-e3dd36890662
	 *
	 */
	/**
	 * :{"deviceType":"android","times":1}
	 * 
	 * @param jsessionid
	 * @param cookies
	 * @return
	 */
	public String initInfo(String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "https://www.o2osl.com/assistant/ass/initInfo.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("deviceType", "android");
			datas.put("android", "1");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/any/files/uptokenAll.json;jsessionid=cc749594-ac2e-40ed-9c9a-4dc4eed8ac79
	 */
	public String uptokenAll(String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "https://www.o2osl.com/assistant/ass/any/files/uptokenAll.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			// Map<String, String> datas = new HashMap<String, String>();
			// datas.put("deviceType", "android");
			// datas.put("android", "1");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject();
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("登陆未知错误,请核对密码login error " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/user/reliance/isUpgrade.json;jsessionid=4e344a27-a1ef-4daf-a5c7-50295870e927
	 */
	public String isUpgrade(String jsessionid, Map<String, String> cookies) {
		String url = "https://www.o2osl.com/assistant/ass/user/reliance/isUpgrade.json;jsessionid=";
		return loginInit(jsessionid, url, cookies);
	}

	/**
	 * http://www.o2osl.com/assistant/ass/community/new/circle/getTags.json;jsessionid=4e344a27-a1ef-4daf-a5c7-50295870e927
	 * 
	 * @param jsessionid
	 * @param cookies
	 * @return
	 */
	public String getTags(String jsessionid, Map<String, String> cookies) {
		String url = "https://www.o2osl.com/assistant/ass/community/new/circle/getTags.json;jsessionid=";
		return loginInit(jsessionid, url, cookies);
	}

	/**
	 * http://www.o2osl.com/assistant/ass/shop/category.json;jsessionid=4e344a27-a1ef-4daf-a5c7-50295870e927
	 * 
	 * @param jsessionid
	 * @param cookies
	 * @return
	 */
	public String category(String jsessionid, Map<String, String> cookies) {
		String url = "https://www.o2osl.com/assistant/ass/shop/category.json;jsessionid=";
		return loginInit(jsessionid, url, cookies);
	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/mulchat/findGroupInfoListByUserId.json;jsessionid=4e344a27-a1ef-4daf-a5c7-50295870e927
	 * {"data":"IvNrKNQgjDBEnG5uPFeh6VGncRQ4KM8tkSxr8Sj1wkFPa7\/Mb+NcneBhhbeVs0235cJ\/aNps1UuO\nTx03JMvPj1PStHEIFbjGupRlQynumQTQwvxkoaAVcWIjIj5PGndMVFeK5nHGrGcOCsFJlFrt6CGQ
	 * 
	 * @param jsessionid
	 * @param cookies
	 * @return
	 */
	public String findGroupInfoListByUserId(String jsessionid, Map<String, String> cookies) {
		String url = "http://www.o2osl.com/assistant/ass/mulchat/findGroupInfoListByUserId.json;jsessionid=";
		return loginInit(jsessionid, url, cookies);
	}

	public String loginInit(String jsessionid, String url, Map<String, String> cookies) {

		String result = null;
		try {

			Connection conn = Jsoup.connect(url + jsessionid).timeout(15 * 1000).method(Method.POST);

			// Map<String, String> datas = new HashMap<String, String>();
			// datas.put("deviceType", "android");
			// datas.put("android", "1");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject();
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/contacts/update.json;jsessionid=c334ab1b-9794-4211-9ff4-68d876d5afd2
	 */
	public String contactsUpdate(String deviceId, String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "https://www.o2osl.com/assistant/ass/contacts/update.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("deviceId", deviceId);

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/slcoin/userSignIn.json;jsessionid=4e344a27-a1ef-4daf-a5c7-50295870e927
	 *
	 */
	public String userSignIn(int userId, String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "https://www.o2osl.com/assistant/ass/slcoin/userSignIn.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("userId", userId + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	public String initInfo(Map<String, String> cookies) {

		String result = null;
		try {

			String url = "http://www.o2osl.com/assistant/ass/initInfo.json;jsessionid=" + cookies.get("SESSION");
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("deviceType", "android");
			datas.put("android", "1");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * 
	 * 提交地理位置信息
	 * http://www.o2osl.com/wap/HZJZ1704271425h2RbS1/buy/littleplugin/index.htm?area=eyJwcm92aW5jZSI6Iua1meaxn%2BecgSIsImNpdHkiOiLph5HljY7luIIiLCJkaXN0cmljdCI6Iua1puaxn%2BWOvyIsImxhdGl0dWRlIjoyOS40NzkwNDYsImxvbmdpdHVkZSI6MTE5Ljg5OTM4M30%3D
	 * {"province":"浙江省","city":"金华市","district":"浦江县","latitude":29.479046,"longitude":119.899383}
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/buy/littleplugin/index.htm?area=eyJwcm92aW5jZSI6Iuaxn%2BiLj%2BecgSIsImNpdHkiOiLmiazlt57luIIiLCJkaXN0cmljdCI6IuW5v%2BmZteWMuiIsImxhdGl0dWRlIjozMi4zOCwibG9uZ2l0dWRlIjoxMTkuNDN9
	 * 
	 * @return
	 */

	public String littlepluginIndex(String area, String shopId, Map<String, String> cookies) {
		String result = null;

		try {
			String url = "http://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/index.htm";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.GET);
			conn.data("area", URLEncoder.encode(area));

			// 忽略返回类型格式
			conn.ignoreContentType(true);
			conn.followRedirects(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			// User-Agent: Mozilla/5.0 (Linux; Android 6.0; Nexus 6P
			// Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko)
			// Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043221
			// Safari/537.36; Shualian/Android/5.2.5(BF#450#FB)
			// Accept:
			// text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
			// Accept-Encoding: gzip, deflate

			//
			conn.header("Upgrade-Insecure-Requests", "1");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

			// conn.header("X-Requested-With", "com.zjlp.bestface");
			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/buy/littleplugin/getVideoList.htm
	 * 
	 * pageSize=20&start=0&curPage=1&area=eyJwcm92aW5jZSI6IuWbm%252BW3neecgSIsImNpdHkiOiLmiJDpg73luIIiLCJkaXN0cmljdCI6Ium%252Bmeaziempv%252BWMuiJ9&searchKey=
	 */

	public String getVideoList(int pageSize, String area, String shopId, String refererUrl,
			Map<String, String> cookies) {
		String result = null;

		try {

			String url = "https://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/getVideoList.htm";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			conn.data("pageSize", "" + pageSize);
			conn.data("start", "0");
			conn.data("curPage", "1");
			conn.data("area", URLEncoder.encode(area));
			conn.data("searchKey", "");

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("Upgrade-Insecure-Requests", "1");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	public String getVideoList(String area, String shopId, String refererUrl, Map<String, String> cookies) {
		return getVideoList(20, area, shopId, refererUrl, cookies);
	}

	/**
	 * http://www.o2osl.com/face-data-web/assistant/ass/data/productStatistics.json
	 * 
	 * {"data":[{"userId":"16127175","userAccount":"18049511425","pageId":1001,"pageName":"小插件广场页","pageViewTime":1496506689296,"deviceType":4}]}
	 * {"data":[{"userId":16127175,"userAccount":"18049511425","pageId":1002,"pageName":"视频详情页","pageViewTime":1496508120124,"deviceType":4}]}
	 */
	public String productStatistics(String username, int userId, long pageViewTime, int pageId, String pageName,
			String refererUrl, Map<String, String> cookies) {

		String result = null;

		try {
			String url = "https://www.o2osl.com/face-data-web/assistant/ass/data/productStatistics.json";

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);
			JSONObject datas = new JSONObject();

			JSONObject json = new JSONObject();
			json.put("userId", userId);
			json.put("userAccount", username);
			json.put("pageId", pageId);
			json.put("pageName", pageName);
			json.put("pageViewTime", pageViewTime);
			json.put("deviceType", 4);

			JSONArray data = new JSONArray();
			data.put(json);

			datas.put("data", data);

			conn.requestBody(datas.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
			conn.header("Content-Type", "application/json;charset=UTF-8");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("X-Requested-With", "XMLHttpRequest");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		} catch (JSONException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	// http://www.o2osl.com/face-data-web/assistant/ass/data/productStatistics.json
	// {"data":[{"userId":16127175,"userAccount":"18049511425","pageId":1002,"pageName":"视频详情页","pageViewTime":1496508120124,"deviceType":4,
	// "buttonId":1003,"buttonName":"小插件播放","nextPageId":1007,"nextPageName":"小插件播放页","pageJumpStatus":1}]}
	public String productStatisticsVideo(String username, int userId, long pageViewTime, int pageId, String pageName,
			String refererUrl, Map<String, String> cookies) {

		String result = null;

		try {
			String url = "https://www.o2osl.com/face-data-web/assistant/ass/data/productStatistics.json";

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);
			JSONObject datas = new JSONObject();

			JSONObject json = new JSONObject();
			json.put("userId", userId);
			json.put("userAccount", username);
			json.put("pageId", pageId);
			json.put("pageName", pageName);
			json.put("pageViewTime", pageViewTime);
			json.put("deviceType", 4);
			json.put("buttonId", 1003);
			json.put("buttonName", "小插件播放");
			json.put("nextPageId", 1007);
			json.put("nextPageName", "小插件播放页");
			json.put("pageJumpStatus", 1);

			JSONArray data = new JSONArray();
			data.put(json);

			datas.put("data", data);

			conn.requestBody(datas.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("Content-Type", "application/json;charset=UTF-8");
			conn.header("X-Requested-With", "XMLHttpReques");
			conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		} catch (JSONException e) {
			logger.error("login error " + e.toString());
		}
		return result;
	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/activity/littleplugin/detail.htm?adId=25252&userId=16419121&pageSize=20&start=20&totalRow=86
	 * 
	 * @return
	 */
	public String littlepluginDetail(int adId, int adUserId, int totalRow, String shopId, Map<String, String> cookies) {

		String result = null;
		try {
			String url = "https://www.o2osl.com/wap/" + shopId + "/activity/littleplugin/detail.htm";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.GET);
			conn.data("adId", adId + "");
			conn.data("userId", adUserId + "");
			conn.data("pageSize", "20");
			conn.data("start", "20");
			conn.data("totalRow", totalRow + "");

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			if (cookies != null) {
				// 设置cookies
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("User-Agent",

					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

			// conn.header("Accept-Encoding", "gzip, deflate");
			// conn.header("Accept-Language", "zh-CN,en-US;q=0.8");
			conn.header("Upgrade-Insecure-Requests", "1");
			conn.header("X-Requested-With", "com.zjlp.bestface");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1704271425h2RbS1/buy/littleplugin/videoDetail.htm
	 */
	public String videoDetail(int adId, int adUserId, String shopId, String refererUrl, Map<String, String> cookies) {

		String result = null;

		try {

			String url = "https://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/videoDetail.htm";

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			conn.data("adId", adId + "");
			conn.data("userId", adUserId + "");

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			// conn.header("Referer", refererUrl);
			conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
			conn.header("Origin", "http://www.o2osl.com");
			conn.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.header("X-Requested-With", "XMLHttpRequest");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/buy/littleplugin/tjwxxcj.htm
	 */
	public String tjwxxcj(String shopId, String refererUrl, Map<String, String> cookies) {

		String result = null;

		try {

			String url = "https://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/tjwxxcj.htm";

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("Accept", "*/*");
			conn.header("X-Requested-With", "XMLHttpRequest");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/buy/littleplugin/tjwxxcj.htm
	 */
	public String cnzz(String cnzzUrl, String refererUrl, Map<String, String> cookies) {

		String result = null;

		try {
			Connection conn = Jsoup.connect(cnzzUrl).timeout(15 * 1000).method(Method.POST);
			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("Accept", "*/*");
			conn.header("Host", "s95.cnzz.com");
			conn.header("User-Agent",

					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.validateTLSCertificates(false);
			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/buy/littleplugin/recordAdPageView.htm
	 */
	public String recordAdPageView(int adId, String shopId, String refererUrl, Map<String, String> cookies) {

		String result = null;

		try {

			String url = "https://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/recordAdPageView.htm";

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			conn.data("adId", adId + "");

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("X-Requested-With", "XMLHttpRequest");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/any/littleplugin/verifyImage.htm?4
	 */
	public String verifyIma(String shopId, String refererUrl, Map<String, String> cookies) {

		String ocrImagePath = OCRUtil.getImageTimeStampPath();

		try {

			String url = "https://www.o2osl.com/wap/" + shopId + "/any/littleplugin/verifyImage.htm?"
					+ (int) (Math.random() * 100);

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.GET);

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("Accept-Language", "zh-CN,en-US;q=0.8");
			conn.header("Accept", "image/webp,image/*,*/*;q=0.8");
			conn.header("User-Agent",

					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("X-Requested-With", "com.zjlp.bestface");

			Response res = conn.execute();

			cookies.putAll(res.cookies());
			logger.debug("res cookies" + res.cookies());
			logger.debug("req cookies" + cookies);

			// 保存到该位置
			saveOcrImage(res.bodyAsBytes(), ocrImagePath);

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return ocrImagePath;
	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/any/littleplugin/verifyImage.htm?4
	 */
	public byte[] verifyImaBytes(String shopId, String refererUrl, Map<String, String> cookies) {

		byte[] imageBytes = null;
		try {

			String url = "http://www.o2osl.com/wap/" + shopId + "/any/littleplugin/verifyImage.htm?"
					+ (int) (Math.random() * 100);

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.GET);

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("Referer", refererUrl);
			conn.header("X-Requested-With", "com.zjlp.bestface");
			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("Accept-Language", "zh-CN,en-US;q=0.8");
			conn.header("Accept", "image/webp,image/*,*/*;q=0.8");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			Response res = conn.execute();

			cookies.putAll(res.cookies());
			logger.debug("res cookies" + res.cookies());
			logger.debug("req cookies" + cookies);

			// 保存到该位置
			imageBytes = res.bodyAsBytes();

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return imageBytes;
	}

	/**
	 * http://www.o2osl.com/wap/HZJZ1705302211mgSWe1/buy/littleplugin/answerQuestions.htm
	 * adId=25252&questionId_1=65416&userAnswerId_1=68352&questionId_2=65714&userAnswerId_2=91205&verificationCode=0_0_0_0&
	 * area=eyJwcm92aW5jZSI6IuWbm%252BW3neecgSIsImNpdHkiOiLmiJDpg73luIIiLCJkaXN0cmljdCI6Ium%252Bmeaziempv%252BWMuiJ9
	 * 
	 * adId=25202&questionId_1=73622&userAnswerId_1=31711&questionId_2=41857&userAnswerId_2=82058&verificationCode=0_1_2_3
	 * &area=eyJwcm92aW5jZSI6IumZleilv%2BecgSIsImNpdHkiOiLmuK3ljZfluIIiLCJkaXN0cmljdCI6IumfqeWfjuW4giIsImxhdGl0dWRlIjozNS40ODAwNTcsImxvbmdpdHVkZSI6MTEwLjQzNjQyNH0%3D
	 * 
	 * 
	 */

	public String answerQuestions(int adId, int questionId_1, int userAnswerId_1, int questionId_2, int userAnswerId_2,
			String verificationCode, String shopId, String area, String refererUrl, Map<String, String> cookies) {
		String result = null;

		try {

			String url = "https://www.o2osl.com/wap/" + shopId + "/buy/littleplugin/answerQuestions.htm";

			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			conn.data("adId", adId + "");
			conn.data("questionId_1", questionId_1 + "");
			conn.data("userAnswerId_1", userAnswerId_1 + "");
			conn.data("questionId_2", questionId_2 + "");
			conn.data("userAnswerId_2", userAnswerId_2 + "");
			conn.data("verificationCode", verificationCode);
			conn.data("area", URLEncoder.encode(area));

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// Content-Type: application/x-www-form-urlencoded; charset=UTF-8
			conn.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			conn.header("Host", "www.o2osl.com");
			conn.header("Connection", "keep-alive");
			conn.header("Accept", "application/json, text/javascript, */*; q=0.01");
			conn.header("Origin", "http://www.o2osl.com");
			conn.header("X-Requested-With", "XMLHttpRequest");
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("Referer", refererUrl);
			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("answerQuestions error " + e.toString());
		}
		return result;

	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/plugin/getAdInfo.json;jsessionid=05a81ca6-7474-45d1-a789-cf049222e035
	 */
	public String getAdInfo(int adId, String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "https://www.o2osl.com/assistant/ass/plugin/getAdInfo.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("adId", adId + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("getAdInfo err " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * http://www.o2osl.com/assistant/ass/plugin/verifyImage.json
	 */
	public String verifyImage(int width, int height, String jsessionid, Map<String, String> cookies) {
		String result = null;

		try {

			String url = "https://www.o2osl.com/assistant/ass/plugin/verifyImage.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("width", width + "");
			datas.put("height", height + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("verifyImage err " + e.toString());
		}
		return result;
	}

	/**
	 * http://www.o2osl.com/assistant/ass/shopcertify/shopCerPartInfo.json
	 * 
	 */
	public String shopCerPartInfo(int userId, String jsessionid, Map<String, String> cookies) {
		String result = null;

		try {

			String url = "https://www.o2osl.com/assistant/ass/shopcertify/shopCerPartInfo.json;jsessionid="
					+ jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("userId", userId + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("verifyImage err " + e.toString());
		}
		return result;
	}

	/**
	 * http://www.o2osl.com/assistant/ass/community/new/homePage/detail.json
	 * 
	 * {"acceptUserId":"146838"}
	 */
	public String homePageDetail(int acceptUserId, String jsessionid, Map<String, String> cookies) {
		String result = null;

		try {

			String url = "https://www.o2osl.com/assistant/ass/community/new/homePage/detail.json;jsessionid="
					+ jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("acceptUserId", acceptUserId + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("verifyImage err " + e.toString());
		}
		return result;
	}

	/**
	 * http://www.o2osl.com/assistant/ass/user/showCard.json
	 * {"loginAccount":"15905897337"}
	 * 
	 * myInvitationCode 刷脸号
	 * 
	 */
	public String showCard(String loginAccount, String jsessionid, Map<String, String> cookies) {
		String result = null;

		try {

			String url = "http://www.o2osl.com/assistant/ass/user/showCard.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("loginAccount", loginAccount + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("verifyImage err " + e.toString());
		}
		return result;
	}

	/**
	 * 
	 * httpvolly.d
	 * js:{"adId":32062,"questionId_1":"52336","userAnswerId_1":"27124","questionId_2":"74938",
	 * "userAnswerId_2":"12572","verificationCode":"0_0_0_0",
	 * "area":"eyJwcm92aW5jZSI6Iua1meaxn%2BecgSIsImNpdHkiOiLph5HljY7luIIiLCJkaXN0cmljdCI6Iua1puaxn%2BWOvyIsImNpdHlDb2RlIjoiMzMwNzI2IiwibGF0aXR1ZGUiOjI5LjQ3OTEyNSwibG9uZ2l0dWRlIjoxMTkuODk5NDI5fQ%3D%3D",
	 * "netWifi":true}
	 */
	public String answerQuestions(int adId, int questionId_1, int userAnswerId_1, int questionId_2, int userAnswerId_2,
			String verificationCode, String jsessionid, String area, Map<String, String> cookies, boolean netWifi) {
		String result = null;

		try {

			String url = "https://www.o2osl.com/assistant/ass/plugin/answerQuestions.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("adId", adId + "");
			datas.put("questionId_1", questionId_1 + "");
			datas.put("userAnswerId_1", userAnswerId_1 + "");
			datas.put("questionId_2", questionId_2 + "");
			datas.put("userAnswerId_2", userAnswerId_2 + "");
			datas.put("verificationCode", verificationCode);
			datas.put("area", URLEncoder.encode(area));
			datas.put("netWifi", "true");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			logger.info(datasJson.toString());
			
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			conn.header("Host", "www.o2osl.com");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("answerQuestions err " + e.toString());
		}
		return result;
	}

	/**
	 * http://php.o2osl.com/home/AndroidPathStart AppStart /home/AppStart/index
	 * {"app_name":"5.2.5","shualianNO":"","patch_code":0}
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	String result = null;

	public String androidPathStart(Map<String, String> cookies) {
		try {

			String url = "http://php.o2osl.com/home/AndroidPathStart";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("app_name", "5.2.6");
			datas.put("shualianNO", "");
			datas.put("patch_code", "0");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * AppStart /home/AppStart/index
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public String appStart(String deviceId, Map<String, String> cookies) {
		String result = null;
		try {

			String url = "http://php.o2osl.com/home/AndroidPathStart";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("deviceId", deviceId);
			datas.put("model", "Android");
			datas.put("osVersion", "7.1");
			datas.put("versionName", "5.3");
			datas.put("versionCode", "550");
			datas.put("device", "1");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * 第一次启动获取SESSION
	 * http://www.o2osl.com/assistant/ass/good/departments.json;jsessionid=null
	 * {"app_name":"5.2.5","shualianNO":"","patch_code":0},
	 * 
	 * @param accountInfo
	 * @param cookiesMap
	 * @return
	 */

	public String departments(String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "http://www.o2osl.com/assistant/ass/good/departments.json;jsessionid=" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			// Map<String, String> datas = new HashMap<String, String>();
			//
			// datas.put("app_name", "5.2.6");
			// datas.put("shualianNO", "");
			// datas.put("patch_code", "0");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject();
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * userTest
	 * http://www.o2osl.com/assistant/ass/user/test.json;jsessionid=null
	 * {"app_name":"5.2.5","shualianNO":"","patch_code":0},
	 * 
	 * @param accountInfo
	 * @param cookiesMap
	 * @return
	 */

	public String userTest(String jsessionid, Map<String, String> cookies) {

		String result = null;
		try {

			String url = "http://www.o2osl.com/assistant/ass/user/test.json;jsessionid=null" + jsessionid;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			// Map<String, String> datas = new HashMap<String, String>();
			//
			// datas.put("app_name", "5.2.6");
			// datas.put("shualianNO", "");
			// datas.put("patch_code", "0");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject();
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * 提交用户的经纬度 http://www.o2osl.com/assistant/any/gis/save.json
	 * {"loginAccount":"15888813151","longitude":119.899348,"latitude":29.479207}
	 * 
	 * @param accountInfo
	 * @param cookiesMap
	 * @return
	 */
	public String gis(String username, double longitude, double latitude, Map<String, String> cookies) {

		String result = null;
		try {

			String url = " http://www.o2osl.com/assistant/any/gis/save.json";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("username", username);
			datas.put("latitude", latitude + "");
			datas.put("longitude", longitude + "");

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * 下发登陆时短信验证码 必须提交username,deviceId字段
	 * http://www.o2osl.com/assistant/any/device/code.json;jsessionid=7e16feeb-087b-479d-b504-83bcfb1be1a3
	 * {"loginAccount":"15619399514","deviceId":"00000000-1f0f-07be-357c-1c2b6a237fa1"}
	 * {"data":"GLajB9pN7r\/PjM36wcdBIJooaGqZpb91+yAFOwY\/Z1Y04EdRDVbmkk1Kq3\/3MoIHDJdknIwjyKgW\nkF0d52xQS7\/guFp8hQIWElDZU35k+LJWTswGPjAapvlbYHoFtFEyVIcubGYT2A4GTHHryRuZIKh4\nm+7+m9+yRuKWnUbvqVA=\n",
	 * "versionCode":440,"device":1}
	 * 
	 * http://www.o2osl.com/assistant/any/device/code.json;jsessionid=e19b9da5-e11a-40a5-a456-3aeea13069b8
	 * http://www.o2osl.com/assistant/any/device/code.json;jsessionid=9fb7e6fb-4dca-4484-9b3a-c2a9f639cebb
	 * 
	 * @return
	 */
	public String messageCode(String username, String deviceId) {

		String result = null;
		try {

			String url = "http://www.o2osl.com/assistant/any/device/code.json;jsessionid=9fb7e6fb-4dca-4484-9b3a-c2a9f639cebb";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("loginAccount", username);
			datas.put("deviceId", deviceId);

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);

			System.out.println(datasJson.toString());
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * 下发登陆时短信验证码 必须提交username,deviceId字段
	 * http://www.o2osl.com/assistant/any/device/checkCode.json;jsessionid=7e16feeb-087b-479d-b504-83bcfb1be1a3
	 * :{"loginAccount":"15619399514","code":"800226","deviceId":"00000000-1f0f-07be-357c-1c2b6a237fa1"}
	 * 
	 * @return
	 */
	public String checkMessageCode(String username, String messageCode, String deviceId) {

		String result = null;
		try {

			String url = "http://www.o2osl.com/assistant/any/device/checkCode.json;jsessionid=null";
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.POST);

			Map<String, String> datas = new HashMap<String, String>();
			datas.put("loginAccount", username);
			datas.put("code", messageCode);
			datas.put("deviceId", deviceId);

			// 设置请求json数据
			JSONObject datasJson = new JSONObject(datas);
			datasJson = initRsaData(datasJson);
			conn.requestBody(datasJson.toString());

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置headers
			// if (headers != null) {
			// }

			conn.header("Content-Type", "application/json; charset=utf-8");
			conn.header("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0; Nexus 6P Build/MDB08M)");

			Response res = conn.execute();// 执行请求
			result = res.body();
		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	/**
	 * 注册账号时第一步下发短信验证码
	 * <p/>
	 * http://www.o2osl.com/assistant/any/reg_4P993_step_1.json
	 * {"phone":"13544813592","pwd":"qwe123","invitationCode":"10137444","mediaType":1}
	 * 
	 * @return
	 */
	public JSONObject reg(String username, String password, String invitationCode, String mediaType) {

		JSONObject postJsonData = jsoupService.initPostJsonData("shualian");
		try {
			String requestUrl = ShuaLianInfo.reg1Url;

			postJsonData.put(DJsoupKey.REQUEST_URL, requestUrl);

			JSONObject datas = new JSONObject();
			datas.put("phone", username);
			datas.put("pwd", password);
			datas.put("invitationCode", invitationCode);
			datas.put("mediaType", mediaType);

			initRsaData(datas);

			jsoupService.post(postJsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return postJsonData;
	}

	/**
	 * 注册账号时第二步提交短信验证码
	 * <p/>
	 * http://www.o2osl.com/assistant/any/reg_4P993_step_2.json
	 * {"phone":"13544813592","pwd":"qwe123","invitationCode":"10137444","uniFlag":"1495788995174","vCode":"184300","industryName":"IT互联网
	 * 研发","industryCode":1000000001}
	 * 
	 * {"code":1000400000,"name":"学生","parentId":0}
	 * 
	 * @return
	 */
	public JSONObject regComit(String username, String password, String invitationCode, String uniFlag,
			String messageCode, String industryName, String industryCode) {

		JSONObject postJsonData = jsoupService.initPostJsonData("shualian");
		try {

			postJsonData.put(DJsoupKey.REQUEST_URL, ShuaLianInfo.reg2Url);

			JSONObject datas = new JSONObject();
			datas.put("phone", username);
			datas.put("pwd", password);
			datas.put("invitationCode", invitationCode);
			datas.put("uniFlag", uniFlag);
			datas.put("vCode", messageCode);
			datas.put("industryName", industryName);
			datas.put("industryCode", industryCode);

			initRsaData(datas);

			jsoupService.post(postJsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return postJsonData;
	}

	/**
	 * 激活邀请码 可获得颜值188
	 * http://www.o2osl.com/wap/HZJZ17041711142xbcT1/buy/personal/activeInvitationCode_4P991.htm
	 * Cookie:
	 * CASTGC=TGT-3433238-U73jAzddnScZe0AMSXgZLIdwXFN1Vgc3EwBmrTHSt2LZb9xSu9-cas;
	 * SESSION=7e16feeb-087b-479d-b504-83bcfb1be1a3
	 * 
	 * 
	 * @param invitationCode
	 *            激活的邀请码
	 * @param accountInfo
	 * @return
	 */
	public static JSONObject activeInvitationCode(String invitationCode, String shopId, String sessionid,
			String castgc) {

		JSONObject postJsonData = jsoupService.initPostJsonData("shualian");
		try {
			postJsonData.put(DJsoupKey.REQUEST_URL, MessageFormat.format(ShuaLianInfo.activeInvitationCodeUrl, shopId));

			JSONObject datas = postJsonData.optJSONObject(DJsoupKey.REQUEST_DATAS);
			JSONObject cookies = postJsonData.optJSONObject(DJsoupKey.REQUEST_COOKIES);
			cookies.put("CASTGC", castgc);
			cookies.put("SESSION", sessionid);

			datas.put("invitationCode", invitationCode);

			jsoupService.post(postJsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return postJsonData;
	}

	public String makeArea(String province, String city, String district, double latitude, double longitude) {
		String area = null;
		try {
			JSONObject datas = new JSONObject();
			datas.put("province", province);
			datas.put("city", city);
			datas.put("district", district);
			// datas.put("latitude", latitude);
			// datas.put("longitude", longitude);

			area = Base64.getEncoder().encodeToString(datas.toString().getBytes());

		} catch (JSONException e) {
			// TODO: handle exception
		}
		return area;
	}

	/**
	 * http://file.o2osl.com/video/14959373464075483010340385048173.mp4
	 * 
	 * GET http://file.o2osl.com/video/1496729471287307322954629570912.mp4
	 * 
	 */
	public String video(String videoUrl, String refererUrl, Map<String, String> cookies) {
		String result = null;

		try {
			String url = videoUrl;
			Connection conn = Jsoup.connect(url).timeout(15 * 1000).method(Method.GET);

			// 忽略返回类型格式
			conn.ignoreContentType(true);

			// 设置代理,方便抓包分析
			proxy(conn);

			// 设置cookies
			if (cookies != null) {
				conn.cookies(cookies);
			}

			// 设置headers
			// if (headers != null) {
			// }
			conn.header("User-Agent",
					"Mozilla/5.0 (Linux; Android 6.0; Nexus 6P Build/MDB08M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/44.0.2403.117 Mobile Safari/537.36; Shualian/Android/5.3(BF#550#FB)");

			conn.header("Host", "file.o2osl.com");
			conn.header("Referer", refererUrl);
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("Range", "bytes=0-1");
			conn.header("X-Requested-With", "com.zjlp.bestface");
			conn.header("Accept-Encoding", "gzip, deflate");
			conn.header("Accept-Language", "zh-CN,en-US;q=0.8");
			conn.header("Accept", "*/*");

			Response res = conn.execute();// 执行请求
			result = res.body();
			cookies.putAll(res.cookies());

		} catch (IOException e) {
			logger.error("login error " + e.toString());
		}
		return result;

	}

	HostnameVerifier hv = new HostnameVerifier() {
		public boolean verify(String urlHostName, SSLSession session) {
			System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
			return true;
		}
	};

	private void initHttps() {
		try {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

}
