package com.bidtime.lucene.utils;

public class KeyWordsUtils {
	
	public static final String AND = " AND ";
	public static final String OR = " OR ";

	public static String bracketWords(String fields, String keywords) {
		return bracketWords(fields, keywords, OR);
	}

	public static String bracketWordsAnd(String fields, String keywords) {
		return bracketWords(fields, keywords, AND);
	}

	public static String bracketWords(String fields, String keywords,
			String logicInner) {
		String[] arFields = fields.split(";");
		if (arFields.length<2) {
			return bracketWords(new String[]{fields}, keywords, logicInner);
		} else {
			return bracketWords(arFields, keywords, logicInner);
		}
	}

	public static String bracketWords(String[] fields, String keywords) {
		return bracketWords(fields, keywords, OR);
	}
	
	public static String bracketWords(String[] fields, String keywords,
			String logicInner) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i == 0) {
				sb.append(fields[i]);
				sb.append(":");
				sb.append(keywords);
			} else {
				sb.append(" ");
				sb.append(logicInner);
				sb.append(" ");
				sb.append(fields[i]);
				sb.append(":");
				sb.append(keywords);
			}
		}
		sb.insert(0, "(");
		sb.append(")");
		return sb.toString();
	}

}
