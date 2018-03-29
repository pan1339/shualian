package com.dajing.shualian;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.dajing.base64.AndroidBase64;

public class RsaHelper {
	//public static final String PUBLIC_KEY_SHUALIAN = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2+lLR5EbQprpy7cYFlFJKilTKnTK7y8VOFM00WjXXSNrR1LM8k0p7X8/pdUtwY0Qb48df8r6xUb53lu5ZjPcPnuSz1JyIapza5dmnoqDiVh8/gWuqq0V8/JAn+pH7ADBuB9gNsTHG9umYjD2cRSh4p7inFu20/m10xca0LEfyQIDAQAB";
	  public static final String PUBLIC_KEY_SHUALIAN = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdwcBufwHKfelCZhRmi9oixSB/18zy2CHRjC04FP3GUPE+lXQC9mT/Oj1g7M1wcmiID4U/BWUWfk7ywrXrx9mGkbKhfbz5xB4+QcaP38Z8xwrDjJeET0YvoYDQEA8hPGV9aXFggrie1uaWtKJbDchOsC6n27PgibHGJ+xEw1wORwIDAQAB";

	public static final String FORMATION_RSA = "RSA";
	public static final String FORMATION_PKCS1Padding = "RSA/ECB/PKCS1Padding";
	public static final String FORMATION_NoPadding = "RSA/ECB/NoPadding";

	public static byte[] rsaInitENCRYPT_MODE(byte[] bArr, String transformation) {
		try {
			Cipher instance = Cipher.getInstance(transformation);
			instance.init(Cipher.ENCRYPT_MODE, createPublicKey(PUBLIC_KEY_SHUALIAN));
			return instance.doFinal(bArr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// public static byte[] rsaInitENCRYPT_MODE(byte[] bArr) {
	// try {
	// Cipher instance = Cipher.getInstance("RSA/ECB/NoPadding");
	// instance.init(Cipher.ENCRYPT_MODE, createPublicKey(PUBLIC_KEY_SHUALIAN));
	// return instance.doFinal(bArr);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	// public static byte[] rsaInitDECRYPT_MODE(byte[] bArr) {
	// try {
	// Cipher instance = Cipher.getInstance("RSA/ECB/NoPadding");
	// instance.init(Cipher.DECRYPT_MODE, createPublicKey(PUBLIC_KEY_SHUALIAN));
	// return instance.doFinal(bArr);
	// } catch (Exception e) {
	// return null;
	// }
	// }

	public static PublicKey createPublicKey(String publicKeyStr) throws Exception {
		PublicKey result = null;
		try {
			result = (RSAPublicKey) KeyFactory.getInstance(FORMATION_RSA)
					.generatePublic(new X509EncodedKeySpec(AndroidBase64.decode(publicKeyStr, 0)));
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e2) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e3) {
			throw new Exception("公钥数据为空");
		}
		return result;
	}

	public static String byte2String(byte[] arrayOfByte) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		char[] arrayChar = new char[arrayOfByte.length << 1];
		for (int i = 0; i < arrayOfByte.length; i++) {
			int k = 0xFF & arrayOfByte[i];
			arrayChar[2 * i] = chars[k >> 4];
			arrayChar[2 * i + 1] = chars[k & 0xF];
		}
		return new String(arrayChar);
	}

}
