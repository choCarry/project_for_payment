package com.payment.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtil {

	private final static Logger logger = LoggerFactory.getLogger(EncryptUtil.class);

	private static SecretKeySpec secretKey;
	private static byte[] key;
    private String iv = "1234567890123456";

	public EncryptUtil() {
		setKey("kakaopay_cardpaytest");
	}

	public static void setKey(String tmpKey) {

		MessageDigest sha = null;

		try {

			key = tmpKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-256");
			sha.reset();
			sha.update(key);
			key = sha.digest(key);

			key = Arrays.copyOf(key, 16);

			secretKey = new SecretKeySpec(key, "AES");


		} catch (NoSuchAlgorithmException e) {
			logger.error("setKey NoSuchAlgorithmException Error : " + e.toString());
		} catch (UnsupportedEncodingException e) {
			logger.error("setKey UnsupportedEncodingException Error : " + e.toString());
		} catch (Exception e) {
			logger.error("setKey Exception Error : " + e.toString());
		}
	}

	public String encrypt (String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
    	String enStr = "";
    	try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
			byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
			enStr = new String(Base64.encodeBase64(encrypted));


		} catch (NoSuchAlgorithmException e) {
			logger.error("encrypt NoSuchAlgorithmException : " + e.toString());
		} catch (UnsupportedEncodingException e) {
			logger.error("encrypt UnsupportedEncodingException : " + e.toString());
		} catch (Exception e) {
			logger.error("encrypt Exception : " + e.toString());
		}
		return enStr;
	}

	public String decrypt (String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
		String deStr = "";
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secretKey ,new IvParameterSpec(iv.getBytes()));
			byte[] byteStr = Base64.decodeBase64(str.getBytes("UTF-8"));
			deStr = new String(c.doFinal(byteStr), "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			logger.error("decrypt NoSuchAlgorithmException : " + e.toString());
		} catch (UnsupportedEncodingException e) {
			logger.error("decrypt UnsupportedEncodingException : " + e.toString());
		} catch (Exception e) {
			logger.error("decrypt Exception : " + e.toString());
		}
		return deStr;

	}

}
