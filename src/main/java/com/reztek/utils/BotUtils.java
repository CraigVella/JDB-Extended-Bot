package com.reztek.utils;

public abstract class BotUtils {
	public static String getPaddingForLen(String toPad, int desiredLen) {
		String padding = "";
		for (int y = 0; y < (desiredLen - toPad.length()); ++y) {
			padding += ' ';
		}
		return padding;
	}
}
