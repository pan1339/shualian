package com.dajing.a;

import com.dajing.aa.SLThreadRunNew;
import com.dajing.db.DbHelper;

public class AnswerRun {

	public final static String password = "z123456";
	public final static String accountPath = "E:/javacode/workspaces/ShuaLian/acocunt";
	public final static int[] areaids = {3501 }; //13758910202,3427,1136,1287,3663 
	//1287, 1136 
	// 744 //3642
	// 3427,2593
	//2433,3663 
	//804,3667 
	
	
	public final static int threadNum = 5;// 线程数

	public final static int perThreadDelayTime = 10*60*60; // 前2个账号线程延误时间,没有题库的情况下建议延时设为120秒+,
														// 后面的账号线程会自动延时5秒启动

	public final static int randomSecond = 20; // 随机延时时间,答题每步骤的延时时间,建议设置5-10秒,设置会太长答题超时/验证码失效

	public final static String dbType = DbHelper.db_type_mysql;// 数据库类型
																// DbHelper.db_type_sqlite
																// DbHelper.db_type_mysql

	public static void main(String[] args) {
		SLThreadRunNew.run(areaids, password, threadNum, perThreadDelayTime, randomSecond, dbType, accountPath);
	}

}
