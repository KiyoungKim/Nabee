package com.nabsys.common.cipher.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	
	public static String getMD5Hash(String value) throws NoSuchAlgorithmException
	{
		String hash = "";
		
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(value.getBytes());
		
		byte[] digest = md.digest();
		
		for(int i=0; i<digest.length; i++)
		{
			hash += Integer.toHexString(digest[i] & 0xFF).toUpperCase();
		}
		
		return hash;
	}
	
	public static String getMD5Hash(byte[] value) throws NoSuchAlgorithmException
	{
		String hash = "";
		
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(value);
		
		byte[] digest = md.digest();
		
		for(int i=0; i<digest.length; i++)
		{
			hash += Integer.toHexString(digest[i] & 0xFF).toUpperCase();
		}
		
		return hash;
	}
}
