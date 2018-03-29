package com.dajing.shualian;

public class ShuaLianInfo {
	public static final int YAOQING_CODE = 10137444;

	// 用于data长度判断,长度长于117
	public static final int UUCP_PATH = 117;

	public static final String hostUrl = "http://www.o2osl.com/assistant";
	public static final String ptpHostUrl = "http://php.o2osl.com";

	// 账号登陆
	public static final String loginUrl = hostUrl + "/j_spring_ass_check";

	// App启动
	public static final String appStartUrl = ptpHostUrl + "/home/AppStart/index";

	// 程序初始化默认访问
	public static final String androidPathStartUrl = ptpHostUrl + "/home/AndroidPathStart";

	// 激活邀请码 可获得颜值188
	public static final String activeInvitationCodeUrl = hostUrl
			+ "/wap/{0}/buy/personal/activeInvitationCode_4P991.htm";

	// 下发登陆时的短信验证码
	public static final String messageCodeUrl = hostUrl + "/any/device/code.json";
	// 下发登陆时的短信验证码
	public static final String departmentsUrl = hostUrl + "/ass/good/departments.json";
	public static final String userTestUrl = hostUrl + "/ass/user/test.json";
	public static final String checkMessageCodeUrl = hostUrl + "/any/device/checkCode.json";

	public static final String reg1Url = hostUrl + "/any/reg_4P993_step_1.json";
	public static final String reg2Url = hostUrl + "/any/reg_4P993_step_2.json";

	/**
	 * 提交经纬度
	 */
	public static final String gisUrl = hostUrl + "/any/gis/save.json";

	/**
	 * http://www.lbwhds.com/wap/HZJZ1704271425h2RbS1/buy/littleplugin/index.htm?area=eyJwcm92aW5jZSI6Iua1meaxn%2BecgSIsImNpdHkiOiLph5HljY7luIIiLCJkaXN0cmljdCI6Iua1puaxn%2BWOvyIsImxhdGl0dWRlIjoyOS40NzkwNDYsImxvbmdpdHVkZSI6MTE5Ljg5OTM4M30%3D
	 */
	public static final String littlepluginAreaUrl = "http://www.lbwhds.com/wap/{0}/buy/littleplugin/index.htm?area=";
	public static final String littlepluginDetailUrl = " * http://www.lbwhds.com/wap/{0}/activity/littleplugin/detail.htm?adId=24525&userId=15929890&pageSize=20&start=20&totalRow=292";

	/**
	 * http://www.lbwhds.com/wap/HZJZ1704271425h2RbS1/buy/littleplugin/getVideoList.htm
	 */
	public static final String getVideoListUrl = "http://www.lbwhds.com/wap/{0}/buy/littleplugin/getVideoList.htm";
	public static final String videoDetailUrl = "http://www.lbwhds.com/wap/{0}/buy/littleplugin/videoDetail.htm";
	public static final String tjwxxcjUrl = "http://www.lbwhds.com/wap/{0}/buy/littleplugin/tjwxxcj.htm";
	public static final String recordAdPageViewUrl = "http://www.lbwhds.com/wap/{0}/buy/littleplugin/recordAdPageView.htm";

	public static final String productStatisticsUrl = "http://www.lbwhds.com/face-data-web/assistant/ass/data/productStatistics.json";
	public static final String answerQuestionsUrl = "http://www.lbwhds.com/wap/{0}/buy/littleplugin/answerQuestions.htm";
	public static final String verifyImageUrl = "http://www.lbwhds.com/wap/{0}/any/littleplugin/verifyImage.htm";

}
