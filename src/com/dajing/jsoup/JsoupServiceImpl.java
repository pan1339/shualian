package com.dajing.jsoup;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.dajing.util.DJsoupUtil;
import com.dajing.util.FileViewer;
import com.dajing.util.PropertiesUtil;

public class JsoupServiceImpl implements JsoupService {

	private static Logger logger = Logger.getLogger(JsoupServiceImpl.class);

	public JsoupServiceImpl() {

	}

	public JSONObject executeFromProperties(String propertiesPath, String... formatStrs) throws IOException {
		JSONObject postJsonData = initPostJsonData();
		parserProperties(postJsonData, propertiesPath, formatStrs);
		execute(postJsonData);
		return postJsonData;
	}

	public void post(JSONObject postJsonData) throws IOException {

		try {
			postJsonData.put(DJsoupKey.REQUEST_METHOD, "POST");
		} catch (JSONException e) {
			logger.error("post error:" + e.toString());
		}
		execute(postJsonData);
	}

	public void get(JSONObject postJsonData) throws IOException {
		try {
			postJsonData.put(DJsoupKey.REQUEST_METHOD, "GET");
		} catch (JSONException e) {
			logger.error("get error:" + e.toString());
		}
		execute(postJsonData);
	}

	/**
	 * 执行请求
	 * 
	 * @param postJsonData
	 * @throws IOException
	 */
	public void execute(JSONObject postJsonData) throws IOException {

		// 取出请求链接REQUEST_URL
		String requestUrl = postJsonData.optString(DJsoupKey.REQUEST_URL);

		// 取出方法REQUEST_METHOD
		String requestMethod = postJsonData.optString(DJsoupKey.REQUEST_METHOD, "GET").toUpperCase();

		// 取出请求头REQUEST_HEADERS
		JSONObject headers = postJsonData.optJSONObject(DJsoupKey.REQUEST_HEADERS);

		// 取出请求数据REQUEST_DATAS
		JSONObject datas = postJsonData.optJSONObject(DJsoupKey.REQUEST_DATAS);

		// 取出请求cookies REQUEST_COOKIES
		JSONObject cookies = postJsonData.optJSONObject(DJsoupKey.REQUEST_COOKIES);

		// 去除请求连接状态
		// 包括REQUEST_TIMEOUT/REQUEST_IGNORE_CONTENT_TYPE/REQUEST_FOLLOW_REDIRECTS
		// REQUEST_IGNORE_HTTP_ERRORS/REQUEST_POST_DATA_CHARSET
		JSONObject conns = postJsonData.optJSONObject(DJsoupKey.REQUEST_CONNECT_STATUS);

		int timeout = conns.optInt(DJsoupKey.REQUEST_TIMEOUT, 30000);
		boolean ignoreContentType = conns.optBoolean(DJsoupKey.REQUEST_IGNORE_CONTENT_TYPE, true);
		boolean followRedirects = conns.optBoolean(DJsoupKey.REQUEST_FOLLOW_REDIRECTS, true);
		boolean ignoreHttpErrors = conns.optBoolean(DJsoupKey.REQUEST_IGNORE_HTTP_ERRORS, false);
		boolean requestAsBody = conns.optBoolean(DJsoupKey.REQUEST_AS_BODY, false);

		// String charset = jsonData.optString(JsoupService.POST_DATA_CHARSET);

		Method method = Method.GET;
		if (requestMethod.equals("POST")) {
			method = Method.POST;
		}
		Connection conn = (Connection) Jsoup.connect(requestUrl).method(method);
		// 设置请求头
		conn.headers(DJsoupUtil.json2Map(headers));

		// 设置请求包内容
		if (requestAsBody && (method == Method.POST)) {
			logger.debug("requestAsBody execute:" + method + ", url:" + requestUrl + ", " + datas.toString());
			conn.requestBody(datas.toString());
		} else {
			Map<String, String> datasStr = DJsoupUtil.json2Map(datas);
			logger.debug("execute method:" + method + ", url:" + requestUrl + ", " + datasStr.toString());
			conn.data(datasStr);
		}

		// 设置请求的一些状态timeout/ignoreContentType/followRedirects/ignoreHttpErrors
		conn.ignoreContentType(ignoreContentType).followRedirects(followRedirects).ignoreHttpErrors(ignoreHttpErrors)
				.timeout(timeout);

		// 设置请求cookies
		Map<String, String> cookiesMap = null;
		if (cookies != null && cookies.length() > 0) {
			cookiesMap = DJsoupUtil.json2Map(cookies);
			conn.cookies(cookiesMap);
			logger.debug("request cookies:" + cookies.toString());
		}

		// 发送请求
		Response response = conn.execute();

		try {
			// 保存返回的RESPONSE_HEADERS
			postJsonData.put(DJsoupKey.RESPONSE_HEADERS, DJsoupUtil.map2Json(response.headers()));
			// 保存返回的RESPONSE_COOKIES
			postJsonData.put(DJsoupKey.RESPONSE_COOKIES, DJsoupUtil.map2Json(response.cookies()));
			// 保存返回的RESPONSE_BODY
			postJsonData.put(DJsoupKey.RESPONSE_BODY, response.body());
			logger.debug("execute response_body:" + method + ", " + response.body() + ", header:" + response.headers()
					+ ", cookies:" + response.cookies());

		} catch (JSONException e) {
			logger.error("execute response error:" + method + ", " + e.toString());
		}

	}

	public JSONObject initPostJsonData() {
		return initPostJsonData("default");
	}

	/**
	 * 读取pathName单个properties文件
	 */
	public JSONObject initPostJsonData(String pathName) {
		JSONObject postJsonData = new JSONObject();

		try {

			String propertiesPath = "config/" + pathName + "/{0}.properties";

			postJsonData.put(DJsoupKey.REQUEST_METHOD, "GET");

			MessageFormat.format(propertiesPath, DJsoupKey.REQUEST_HEADERS);

			postJsonData.put(DJsoupKey.REQUEST_HEADERS,
					parserProperties(MessageFormat.format(propertiesPath, DJsoupKey.REQUEST_HEADERS)));
			postJsonData.put(DJsoupKey.REQUEST_DATAS,
					parserProperties(MessageFormat.format(propertiesPath, DJsoupKey.REQUEST_DATAS)));
			postJsonData.put(DJsoupKey.REQUEST_CONNECT_STATUS,
					parserProperties(MessageFormat.format(propertiesPath, DJsoupKey.REQUEST_CONNECT_STATUS)));
			postJsonData.put(DJsoupKey.REQUEST_COOKIES,
					parserProperties(MessageFormat.format(propertiesPath, DJsoupKey.REQUEST_COOKIES)));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return postJsonData;
	}

	/**
	 * 读取pathName路径下的所有properties文件
	 * 
	 * @param pathName
	 * @return
	 */
	public JSONObject initAllPostJsonData(String pathName) {
		return PropertiesUtil.readAllProperties2JSONObject("config" + "/" + pathName);
	}

	private JSONObject parserProperties(String propertiesPath) {
		return PropertiesUtil.readProperties2JSONObject(propertiesPath);
	}

	private void parserProperties(JSONObject postJsonData, String propertiesPath, String... formatStrs) {
		JSONObject propertiesJson = parserProperties(propertiesPath);

		JSONObject requestHeaders = postJsonData.optJSONObject(DJsoupKey.REQUEST_HEADERS);
		if (requestHeaders == null) {
			requestHeaders = new JSONObject();
		}

		Iterator it = propertiesJson.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			String keyLower = key.toUpperCase();
			String value = propertiesJson.optString(key);

			// 格式化输出,可变参数
			value = MessageFormat.format(value, formatStrs);
			// logger.debug("parserProperties value:" + value);

			try {
				if (keyLower.equals("POST")) {
					postJsonData.put(DJsoupKey.REQUEST_METHOD, "POST");
					postJsonData.put(DJsoupKey.REQUEST_URL, value);
				} else if (keyLower.equals("GET")) {
					postJsonData.put(DJsoupKey.REQUEST_METHOD, "GET");
					postJsonData.put(DJsoupKey.REQUEST_URL, value);
				} else if (keyLower.equals("DATAS")) {
					// JSONObject datas = new JSONObject();
					postJsonData.put(DJsoupKey.REQUEST_DATAS, value);
				} else {
					requestHeaders.put(key, value);
				}
			} catch (JSONException e) {
				logger.error("parserProperties error:" + e.toString());
			}
		}
	}

}
