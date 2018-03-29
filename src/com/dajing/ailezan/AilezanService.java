package com.dajing.ailezan;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.dajing.shualian.RsaHelper;
import com.dajing.shualian.SLJsonHelper;
import com.dajing.shualian.ShuaLianInfo;
import com.dajing.util.DJsoupUtil;

public class AilezanService {

	private static Logger logger = Logger.getLogger(AilezanService.class);
	private final static String ailezanAPIUrl = "http://api.hellotrue.com/api/do.php";

	/**
	 * 登录方法[loginIn]
	 * 
	 * action=loginIn 提交参数：name=API账号&password=密码
	 * 调用实例：http://api.hellotrue.com/api/do.php?action=loginIn&name=API账号&password=密码
	 * 返回值：1|token(token是重要的返回参数，后面所有的请求都要传这个参数值)
	 * API账号每个用户会对应一个，具体用户对应的API账号请下载客户端查看
	 * 
	 * @return
	 */
	public static String login(String username, String password) {

		String reuslt = null;

		Connection conn = (Connection) Jsoup.connect(ailezanAPIUrl).method(Method.POST);
		conn.ignoreContentType(true);

		try {
			JSONObject dataJsonObject = new JSONObject();

			dataJsonObject.put("action", "loginIn");
			dataJsonObject.put("name", username);
			dataJsonObject.put("password", password);

			logger.debug(dataJsonObject.toString());

			conn.data(DJsoupUtil.json2Map(dataJsonObject));
			Response res = conn.execute();
			reuslt = res.body();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return reuslt;

	}

	/**
	 * 获取一个手机号[getPhone]
	 * <p/>
	 * 
	 * action=getPhone 提交参数： 一般调用参数：sid=项目id&token=登录时返回的令牌
	 * 指定区域调用参数：sid=项目id&token=登录时返回的令牌&locationMatching=include|exclude&locationLevel=p|c&location=重庆
	 * 
	 * 同时取两个以上的码，项目id之间用逗号(,)隔开，如sid=1000,1001。如果要获取指定号码，再在后面加一个phone=要指定获取的号码
	 * locationMatching、locationLevel、location三个为可选参数。用来取某些区域的手机号或者不要某些区域的手机号
	 * locationMatching的参数值只能是include或者exclude中的一个。include指的是包含区域，exclude指的是不包含区域
	 * locationLevel参数只能是p或者c中的一个。p指的是省（province），c指的是市（city）
	 * location指的是区域，中文值。可以在取验证码中查询到具体中文内容。需要utf8编码一下 现在，我来举个例子：
	 * locationMatching=include&locationLevel=c&location=开封 匹配 城市
	 * 开封（意思是只选城市开封的号） locationMatching=exclude&locationLevel=c&location=开封 排除
	 * 城市 开封（意思是不选城市开封的号） locationMatching=include&locationLevel=p&location=河南
	 * 匹配 省份 河南（意思是只选省份河南的号）
	 * locationMatching=exclude&locationLevel=p&location=河南 排除 省份
	 * 开封（意思是不选省份河南的号）
	 * <p/>
	 * 注：location 参数为中文，编要编码 例上海 编码后为 %E4%B8%8A%E6%B5%B7
	 * 编码工具：http://tool.oschina.net/encode?type=4
	 * <p/>
	 * 其它参数： A。指定一个手机号取号，请在参数后多加一个phone=你要指定获取的号码
	 * B。指定运营商取号，请在参数后多加一个参数phoneType=CMCC，CMCC是指移动，UNICOM是指联通，TELECOM是指电信
	 * C。指定虚拟运营商或排除虚拟运营商 请在参数后多加一个参数。vno=1 表示指定只取虚拟运营商， vno=0 表示排除过滤虚拟运营商。
	 * 
	 * <p/>
	 * 调用实例：http://api.hellotrue.com/api/do.php?action=getPhone&sid=项目id&token=登录时返回的令牌
	 * 返回值：1|手机号 当返回0|系统暂时没有可用号码，请过3秒再重新取号。 当返回
	 * 0|余额不足，当前余额为0.00元，其中使用中的项目锁定0.40元。 存在余额不足的字眼，请停止软件运行。 当返回
	 * 0|超出频率，请延时3秒再请求。 返回 0| 请软件主动延时3秒再请求，对于没加任何延时的，平台监控到并发高的会封号处理。
	 * 如何一个手机号接收多条短信
	 * <p/>
	 * 方法一：同个项目取多条短信(针对平台没提供多个项目的) 第一条取出短信后，再调用获取手机号指定手机号调用实例：
	 * http://api.hellotrue.com/api/do.php?action=getPhone&sid=项目id&phone=手机号&token=登录时返回的令牌
	 * <p/>
	 * 方法二：不同项目取多条短信(针对平台已有提供多个项目的) s *
	 * 平台一个项目对应一种短信模板，一般热门项目平台会建成2个项目如果你做的项目平台有建成多个项目则使用方法.传多个不同的项目ID进去取号(逗号隔开)，取码和加黑也一样.
	 * <p/>
	 * 调用实例：http://api.hellotrue.com/api/do.php?action=getPhone&sid=项目id1,项目id2&phone=手机号&token=登录时返回的令牌
	 * 
	 * @param sid
	 * @param token
	 * @return
	 */

	public static String getPhone(String token, String sid, String locationMatching, String locationLevel,
			String location) {

		String reuslt = null;

		Connection conn = (Connection) Jsoup.connect(ailezanAPIUrl).method(Method.GET);
		conn.ignoreContentType(true);

		try {
			JSONObject dataJsonObject = new JSONObject();

			dataJsonObject.put("action", "getPhone");
			dataJsonObject.put("sid", sid);
			dataJsonObject.put("token", token);
			dataJsonObject.put("locationMatching", locationMatching);
			dataJsonObject.put("locationLevel", locationLevel);
			dataJsonObject.put("location", location);

			logger.debug("getPhone:" + dataJsonObject.toString());

			conn.data(DJsoupUtil.json2Map(dataJsonObject));
			Response res = conn.execute();
			reuslt = res.body();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return reuslt;

	}

	/**
	 * 获取验证码[getMessage]
	 * 
	 * action=getMessage 提交参数：sid=项目id&phone=取出来的手机号&token=登录时返回的令牌&author=软件作者
	 * 用户名(这里是传作者注册时的用户名)。同时取两个以上的码，项目id之间用逗号(,)隔开，如sid=1000,1001
	 * 调用实例：http://api.hellotrue.com/api/do.php?action=getMessage&sid=项目id&phone=取出来的手机号&token=登录时返回的令牌
	 * 返回值：1|短信内容
	 * 备注：当返回0|还没有接收到短信，请过3秒再试，请软件主动3秒再重新取短信内容。一般项目的短信在1分钟左右能取到，个别比较慢的也应该在3分钟左右能取到。所以重试间隔3秒的情况下一般循环获取20~60次之间即可。如果一超过60次取不到短信，可以加黑该手机号。
	 * 返回 0| 请软件主动延时3秒再请求，对于没加任何延时的，平台监控到并发高的会封号处理。
	 * 
	 * @param token
	 * 
	 * @return
	 */
	public static String getMessage(String token, String sid, String phone) {

		String reuslt = null;

		Connection conn = (Connection) Jsoup.connect(ailezanAPIUrl).method(Method.POST);
		conn.ignoreContentType(true);

		try {
			JSONObject dataJsonObject = new JSONObject();

			dataJsonObject.put("action", "getMessage");
			dataJsonObject.put("sid", sid);
			dataJsonObject.put("token", token);
			dataJsonObject.put("phone", phone);

			logger.debug("getMessage:" + phone + ", " + dataJsonObject.toString());

			conn.data(DJsoupUtil.json2Map(dataJsonObject));
			Response res = conn.execute();
			reuslt = res.body();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return reuslt;

	}

	/**
	 * 释放指定手机号[cancelRecv]
	 * 
	 * action=cancelRecv 提交参数：sid=项目id&phone=要释放的手机号&token=登录时返回的令牌
	 * 调用实例：http://api.hellotrue.com/api/do.php?action=cancelRecv&sid=项目id&phone=要释放的手机号&token=登录时返回的令牌
	 * 返回值：1|操作成功 备注：取果是正常取到了短信，是不用操作加入黑名单和释放手机号的
	 * 
	 * @param token
	 * @param sid
	 * @param phone
	 * @return
	 */

	public static String cancelRecv(String token, String sid, String phone) {

		String reuslt = null;

		Connection conn = (Connection) Jsoup.connect(ailezanAPIUrl).method(Method.POST);
		conn.ignoreContentType(true);

		try {
			JSONObject dataJsonObject = new JSONObject();

			dataJsonObject.put("action", "cancelRecv");
			dataJsonObject.put("sid", sid);
			dataJsonObject.put("token", token);
			dataJsonObject.put("phone", phone);

			logger.debug("cancelRecv:" + phone + ", " + dataJsonObject.toString());

			conn.data(DJsoupUtil.json2Map(dataJsonObject));
			Response res = conn.execute();
			reuslt = res.body();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return reuslt;

	}

	public static String cancelAllRecv(String token, String sid) {

		String reuslt = null;

		Connection conn = (Connection) Jsoup.connect(ailezanAPIUrl).method(Method.POST);
		conn.ignoreContentType(true);

		try {
			JSONObject dataJsonObject = new JSONObject();

			dataJsonObject.put("action", "cancelAllRecv");
			dataJsonObject.put("sid", sid);
			dataJsonObject.put("token", token);

			logger.debug("cancelAllRecv:" + dataJsonObject.toString());

			conn.data(DJsoupUtil.json2Map(dataJsonObject));
			Response res = conn.execute();
			reuslt = res.body();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return reuslt;

	}

	/**
	 * 手机号加入黑名单[addBlacklist]
	 * 
	 * action=addBlacklist 提交参数：sid=项目id&phone=要加入黑名单的手机号&token=登录时返回的令牌
	 * 调用实例：http://api.hellotrue.com/api/do.php?action=addBlacklist&sid=项目id&phone=要加入黑名单的手机号&token=登录时返回的令牌
	 * 返回值：1|操作成功 备注：取果是正常取到了短信，是不用操作加入黑名单和释放手机号的
	 * 
	 * @param token
	 * @param sid
	 * @return
	 */
	public static String addBlacklist(String token, String sid, String phone) {

		String reuslt = null;

		Connection conn = (Connection) Jsoup.connect(ailezanAPIUrl).method(Method.POST);
		conn.ignoreContentType(true);

		try {
			JSONObject dataJsonObject = new JSONObject();

			dataJsonObject.put("action", "addBlacklist");
			dataJsonObject.put("sid", sid);
			dataJsonObject.put("token", token);
			dataJsonObject.put("phone", phone);

			logger.debug("addBlacklist:" + phone + ", " + dataJsonObject.toString());

			conn.data(DJsoupUtil.json2Map(dataJsonObject));
			Response res = conn.execute();
			reuslt = res.body();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return reuslt;

	}

}
