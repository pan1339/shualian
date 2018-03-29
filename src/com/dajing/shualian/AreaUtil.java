package com.dajing.shualian;

import com.dajing.base64.AndroidBase64;

/**
 * 刷脸地址转换,拷贝自反编译apk
 * 
 * @author Administrator
 *
 */
public class AreaUtil {
	private static char[] charArray;
	private static byte[] byteArray;

	static {
		charArray = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
				'7', '8', '9', '+', '/' };
		byteArray = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
				52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30,
				31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1,
				-1 };
	}

	public static String areaEncode(String areaStr) {
		return areaEncode(areaStr.getBytes());
	}

	public static String areaEncode(byte[] areaByte) {
		StringBuffer stringBuffer = new StringBuffer();
		int areaByteLength = areaByte.length;
		int index = 0;
		while (index < areaByteLength) {
			int nextIndex = index + 1;
			int byteInt = areaByte[index] & 255;
			if (nextIndex == areaByteLength) {// 最后位处理
				// 无符号右移2位
				stringBuffer.append(charArray[byteInt >>> 2]);
				stringBuffer.append(charArray[(byteInt & 3) << 4]);
				stringBuffer.append("==");
			} else {
				int nNextIndex = nextIndex + 1;
				nextIndex = areaByte[nextIndex] & 255;
				if (nNextIndex == areaByteLength) {
					stringBuffer.append(charArray[byteInt >>> 2]);
					stringBuffer.append(charArray[(byteInt & 3) << 4 | (nextIndex & 240) >>> 4]);
					stringBuffer.append(charArray[(nextIndex & 15) << 2]);
					stringBuffer.append("=");
				} else {
					index = nNextIndex + 1;
					nNextIndex = areaByte[nNextIndex] & 255;
					stringBuffer.append(charArray[byteInt >>> 2]);
					stringBuffer.append(charArray[(byteInt & 3) << 4 | (nextIndex & 240) >>> 4]);
					stringBuffer.append(charArray[(nextIndex & 15) << 2 | (nNextIndex & 192) >>> 6]);
					stringBuffer.append(charArray[nNextIndex & 63]);
					continue;
				}
			}

			break;
		}
		return stringBuffer.toString();
	}
}
