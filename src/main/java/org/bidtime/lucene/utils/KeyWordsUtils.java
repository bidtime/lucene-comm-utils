package org.bidtime.lucene.utils;

public class KeyWordsUtils {
	
	public static final String AND = "AND";
	public static final String OR = "OR";

//	public static String bracketWords(String fields, String keywords) {
//		return bracketWords(fields, keywords, OR);
//	}
//
//	public static String bracketWordsAnd(String fields, String keywords) {
//		return bracketWords(fields, keywords, AND);
//	}

	public static String bracketWords(String fields, String key,
			String logic) {
		String[] arFields = fields.split(";");
		if (arFields.length<2) {
			return bracketKey(new String[]{fields}, key, logic);
		} else {
			return bracketKey(arFields, key, logic);
		}
	}

//	public static String bracketWords(String[] fields, String keywords) {
//		return bracketWords(fields, keywords, OR);
//	}
	
	public static String bracketKey(String[] fields, String key,
			String logic) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i == 0) {
				sb.append(fields[i]);
				sb.append(":");
				sb.append(key);
			} else {
				sb.append(" ");
				sb.append(logic);
				sb.append(" ");
				sb.append(fields[i]);
				sb.append(":");
				sb.append(key);
			}
		}
		sb.insert(0, "(");
		sb.append(")");
		return sb.toString();
	}

}
