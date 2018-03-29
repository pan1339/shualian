package com.dajing.shualian.test;

import org.junit.Test;

import com.dajing.db.DbHelper;
import com.dajing.shualian.SLJsonHelper;

public class DBTest {
	@Test
	public void testDB() {

		DbHelper dbHelper = new DbHelper();

		// dbHelper.insertAilezanPhone("15988417751", "10135", "浙江");
		// dbHelper.insertAilezanMessage("15988417751", "10135", "hi dajing ",
		// "15888888888");
		// dbHelper.insertAilezanMessage("15988417751", "10135", "hi dajing ",
		// "15888888888");

		// e48d275a693b2ff2,89860018111551029244,867979021433112

		String username = "";
		String password = "";
		
		
		String androidid = SLJsonHelper.randomStr(16, SLJsonHelper.radomStrL);
		String imsi = SLJsonHelper.randomStr(20, SLJsonHelper.radomNumber);
		String imei = SLJsonHelper.randomStr(15, SLJsonHelper.radomNumber);
		String deviceId = SLJsonHelper.getUUID(androidid, imsi, imei);

		System.out.println(androidid.length());
		System.out.println(imsi.length());
		System.out.println(imei.length());

		dbHelper.insertUsername(username, password, androidid, imsi, imei, deviceId);

		dbHelper.updateAilezanMessageStatus("15988417751", 1);
		dbHelper.updateAilezanPhoneStatus("15988417751", 1);

	}
}
