package com.dajing.aa;

import com.dajing.db.DbHelper;
import com.dajing.shualian.SLJsonHelper;

public class AddUsername {

	public static void main(String[] args) {
		String androidid = SLJsonHelper.randomStr(16, SLJsonHelper.radomStrL);
		String imsi = SLJsonHelper.randomStr(20, SLJsonHelper.radomNumber);
		String imei = SLJsonHelper.randomStr(15, SLJsonHelper.radomNumber);
		String deviceId = SLJsonHelper.getUUID(androidid, imsi, imei);
		
		DbHelper dbHelper = new DbHelper();
		dbHelper.insertUsername("13586966660", "a01389569", androidid, imsi, imei, deviceId);

	}

}
