package com.qiuxs.codegenerate.utils;

public class ComnUtils {
	public static boolean isBlank(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static String formatName(String columnName) {
		String[] names = columnName.split("_");
		StringBuilder sb = new StringBuilder();
		sb.append(names[0]);
		for (int i = 1; i < names.length; i++) {
			sb.append(firstToUpperCase(names[i]));
		}
		return sb.toString();
	}

	public static String firstToUpperCase(String name) {
		StringBuilder sb = new StringBuilder(String.valueOf(name.charAt(0)).toUpperCase());
		sb.append(name.substring(1));
		return sb.toString();
	}
}
