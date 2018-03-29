package com.dajing.shualian.test;

import org.junit.Test;

import com.dajing.ailezan.AilezanService;

public class AilezanTest {
	@Test
	public void ailezanTest() {
		AilezanService ailezanService = new AilezanService();
		String result = ailezanService.login("api-v0ce0rwa", "qqqq1111");
		System.out.println(result);

		String token = "53e2cba7-215a-440b-80f4-5caec9ce55b8";
//		if (result != null && result.startsWith("1|")) {
//			token = result.substring(2, result.length());
//		}
//		result = ailezanService.getPhone(token, "10135", "include", "p", "北京");
//		System.out.println("getPhone:"+result);
		
		
		String phone = "17091172059";
//		if (result != null && result.startsWith("1|")) {
//			phone = result.substring(2, result.length());
//		}
//		result = ailezanService.getMessage("53e2cba7-215a-440b-80f4-5caec9ce55b8", "10135", phone);
//		System.out.println("getMessage:"+result);
//		
	}

}
