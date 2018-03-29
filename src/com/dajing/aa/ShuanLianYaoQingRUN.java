package com.dajing.aa;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dajing.ailezan.AilezanService;
import com.dajing.db.DbHelper;
import com.dajing.shualian.SLJsonHelper;

public class ShuanLianYaoQingRUN {

	private static Logger logger = Logger.getLogger(ShuanLianYaoQingRUN.class);

	private static final String token = "53e2cba7-215a-440b-80f4-5caec9ce55b8";
	private static final String sid = "10135";
	private static final String invitationCode = "10137444";// 10055621//10137444
	// private static final String[] province = { "北京市", "上海市", "天津市", "重庆市",
	// "黑龙江", "辽宁", "吉林", "河北", "河南", "湖北", "湖南",
	// "山东", "山西", "陕西", "安徽", "浙江", "江苏", "福建", "广东", "海南", "四川", "云南", "贵州",
	// "青海", "甘肃", "江西" };
	private static final String[] province = { "北京", "上海", "湖北", "浙江", "江苏", "广东" };
	private static final String password = "qwe12399";
	// private static final String[] province = { "未知" };

	private static final String[] industry = { "1000000000|IT互联网", "1000000001|研发", "1000000002|销售",
			"1000000003|运营\\/编辑", "1000000004|产品", "1000000005|市场商务", "1000000006|设计", "1000000007|高管",
			"1000000008|运维\\/安全", "1000000009|人力HR", "1000000010|行政后勤", "1000000011|测试", "1000000012|项目管理",
			"1000000013|客服", "1000100000|文化传媒", "1000100001|设计\\/动画", "1000100002|行政人事", "1000100003|销售",
			"1000100004|品牌\\/公关", "1000100005|策划", "1000100006|高管", "1000100007|艺人\\/经纪人", "1000100008|市场商务",
			"1000100009|演出\\/会展", "1000100010|编导制作", "1000100011|编辑记者", "1000100012|艺术家\\/收藏", "1000100013|出版发行",
			"1000200000|通信电子", "1000200001|销售", "1000200002|生产制造", "1000200003|技工普工", "1000200004|工程\\/维护",
			"1000200005|硬件研发", "1000200006|经营管理", "1000200007|市场商务", "1000200008|行政人事", "1000200009|工业设计",
			"1000200010|采购物控", "1000200011|增值业务", "1000300000|金融", "1000300001|销售\\/理财", "1000300002|银行",
			"1000300003|财税审计", "1000300004|交易证卷", "1000300005|市场商务", "1000300006|高管", "1000300007|风投\\/投行",
			"1000300008|保险", "1000300009|担保信贷", "1000300010|人力资源", "1000300011|行政后勤", "1000300012|客户服务",
			"1000300013|融资租赁", "1000300014|咨询服务", "1000300015|拍卖典当", "1000400000|学生", "1000400001|关注文化传媒",
			"1000400002|关注服务业", "1000400003|关注IT互联网", "1000400004|关注金融业", "1000400005|关注教育科研", "1000400006|关注医疗生物",
			"1000400007|关注房产建筑"

	};

	public static void main(String[] args) {

		// AilezanService.cancelAllRecv(token, sid);
		AilezanService.login("api-v0ce0rwa", "qqqq1111");

		// 创建一个线程池
		ExecutorService pool = Executors.newFixedThreadPool(1);

		DbHelper dbHelper = new DbHelper();

		int num = 0;
		while (true) {
			num++;
			pool.execute(ailezanGetphone(dbHelper));
			if (num > 10) {
				break;
			}
			try {
				Thread.sleep(1700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		pool.shutdown();

	}

	private static Runnable ailezanGetphone(DbHelper dbHelper) {

		return new Runnable() {

			@Override
			public void run() {

				int randonInt = (int) (Math.random() * province.length);
				String location = province[randonInt];
				String result = AilezanService.getPhone(token, sid, "include", "p", location);
				String phone = null;
				if (result != null && result.contains("1|")) {
					phone = result.substring(2, result.length());
					logger.debug("获取号码成功:" + phone + ", " + location + ", " + result);
				} else {
					logger.debug("获取号码失败:" + phone + ", " + location + ", " + result);

					return;
				}

				String androidid = SLJsonHelper.randomStr(16, SLJsonHelper.radomStrL);
				String imsi = SLJsonHelper.randomStr(20, SLJsonHelper.radomNumber);
				String imei = SLJsonHelper.randomStr(15, SLJsonHelper.radomNumber);
				String deviceId = SLJsonHelper.getUUID(androidid, imsi, imei);

				int industryInt = (int) (Math.random() * industry.length);
				String industryStr = industry[industryInt];

				String[] industryStrs = industryStr.split("\\|");
				String industryCode = industryStrs[0];
				String industryName = industryStrs[1];

				JSONObject jsonObject = null;// ShuaLianHelper.reg(phone,
												// "qwe12399", invitationCode,
												// "1");

				logger.debug("注册下发短信:" + phone + ", industryName:" + industryName + ", industryCode:" + industryCode
						+ ", " + result);

				long timePassed = System.currentTimeMillis();

				do {

					// 【刷脸APP】此验证码只用于你的刷脸账号登录，验证码提供给他人将导致账号被盗。
					// 验证码为224929，请在3分钟内提交验证，请勿将此验证(来自10657120670094001)
					String messageStr = AilezanService.getMessage(token, sid, phone);
					if (messageStr != null) {
						if (messageStr.contains("1|") && messageStr.contains("刷脸APP")) {

							// 0|还没有接收到短信，请过3秒再试
							// 0|短信已取回或手机号已释放
							// 1|【刷脸APP】验证码：336670，将于10分钟后失效，请勿将验证码告知他人。如非本人操作，请致电客服400-000-3777咨询。(来自106911841231)
							int startIndex = messageStr.indexOf("验证码：") + "验证码：".length();
							String messageCode = messageStr.substring(startIndex, startIndex + 6);

							String uniFlag = System.currentTimeMillis() + "";

							// jsonObject = ShuaLianHelper.regComit(phone,
							// password, invitationCode, uniFlag, messageCode,
							// industryName, industryCode);
							logger.debug("提交下发短信:" + phone + ", " + messageCode + ", " + result);

							dbHelper.insertUsername(phone, password, androidid, imsi, imei, deviceId);

							// ShuaLianHelper.login(phone, password, deviceId);

							break;
						} else if (messageStr.contains("0|短信已取回")) {
							break;
						}
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					long timeFlag = System.currentTimeMillis() - timePassed;
					if (timeFlag >= 3 * 40 * 1000) {
						logger.debug("未收到短息超时:" + phone);
						break;
					}
				} while (true);

				AilezanService.cancelRecv(token, sid, phone);
				AilezanService.addBlacklist(token, sid, phone);

			}
		};
	}
}
