package net.dwade.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MD5Utils {

	static Logger logger = LoggerFactory.getLogger(MD5Utils.class);

	static final String encoding="utf-8";
	
	/**
	 * md5校验两密串是否相等
	 * @param  src<String> 源密串
	 * @param  des<String> 目标密串
	 * @return fase: 验证匹配 失败  true 验证匹配成功
	 */
	static public boolean verify(String src, String des) {
		MessageDigest md5 = null;
		StringBuffer hexValue = new StringBuffer(32);
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] byteArray = null;
			byteArray = src.getBytes(encoding);
			byte[] md5Bytes = md5.digest(byteArray);
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("MD5加密出现 UnsupportedEncodingException异常:",e);
			return false;
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5加密出现 NoSuchAlgorithmException异常:",e);
			return false;
		}
		return hexValue.toString().equals(des);
	}

	/**
	 * 使用指定的字符编码加进行md5加密
	 * @param  src<String> 源串
	 * @param  charset<String> 编码格式
	 * @return 加密后的密串
	 */
	static public String md5(String src, String charset){
		MessageDigest md5 = null;
		StringBuffer hexValue = new StringBuffer(32);
		try{
			md5 = MessageDigest.getInstance("MD5");

			byte[] byteArray = null;
			byteArray = src.getBytes(charset);

			byte[] md5Bytes = md5.digest(byteArray);

			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("MD5加密出现 UnsupportedEncodingException异常:",e);
			return "";
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5加密出现 NoSuchAlgorithmException异常:",e);
			return "";
		}
		return hexValue.toString();
	}

	/**
	 * 使用默认编码进行md5加密
	 * @param  src<String> 源串
	 * @return 加密后的密串
	 */
	static public String md5(String src){
		return md5(src, encoding);
	}

}
