package me.kirnirn.java.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
	public boolean isNotBlank(String src){
		if(src== null || src.trim().isEmpty()) {
			return false;
		}
		
		return true;
	}
}
