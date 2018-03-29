package com.dajing.a;

import com.dajing.aa.FreshArea;

public class RunArea {
	public static void main(String[] args) {
		// try {
		// Thread.sleep(60000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// 刷新地址
		 FreshArea.run(FreshArea.username);
		// 从数据库中取地址
		FreshArea.areaList();
	}
}
