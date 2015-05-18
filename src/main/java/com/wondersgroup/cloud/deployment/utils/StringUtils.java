package com.wondersgroup.cloud.deployment.utils;

import java.util.regex.Pattern;

public final class StringUtils {
	private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

	public static boolean isInteger(String str) {
		if (str == null || str.length() == 0)
			return false;
		return INT_PATTERN.matcher(str).matches();
	}
}
