package com.dh.kakaopay.util;

public class Tool {
	
	public static boolean isNull(String value) {
		if(value == null || "".equals(value.trim())) {
			return true;
		}
		return false;
		
	}
}
