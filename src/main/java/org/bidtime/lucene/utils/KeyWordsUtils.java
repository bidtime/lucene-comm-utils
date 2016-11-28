package org.bidtime.lucene.utils;

import org.apache.lucene.queryparser.classic.QueryParser;

public class KeyWordsUtils {
	
	public static final String AND = "AND";
	public static final String OR = "OR";

	public static String bracketEscWords(String fields, String key,
			String logic) {
		String[] arFields = fields.split(";");
		if (arFields.length<2) {
			return bracketEscWords(new String[]{fields}, key, logic);
		} else {
			return bracketEscWords(arFields, key, logic);
		}
	}
	
	public static String bracketEscWords(String[] fields, String key,
			String logic) {
		String keyEsc = QueryParser.escape(key);
		String keys[] = keyEsc.split(" ");
		if (keys.length<2) {
			StringBuilder sb = new StringBuilder();
			KeyWordsUtils.bracketKey(fields, key, logic, sb);
			return sb.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			//sb.append("(");
			for (int i = 0; i < keys.length; i++) {
				if (i == 0) {
					KeyWordsUtils.bracketKey(fields, keys[i], logic, sb);
				} else {
					sb.append(" ");
					sb.append(KeyWordsUtils.OR);
					sb.append(" ");
					KeyWordsUtils.bracketKey(fields, keys[i], logic, sb);
				}
			}
			//sb.insert(0, "(");
			//sb.append(")");
			return sb.toString();
		}
	}

	public static String bracketWords(String fields, String key) {
		return bracketWords(fields, key, null);
	}
	
	public static String bracketWords(String fields, String key,
			String logic) {
		StringBuilder sb = new StringBuilder();
		String[] arFields = fields.split(";");
		if (arFields.length<2) {
			bracketKey(new String[]{fields}, key, logic, sb);
		} else {
			bracketKey(arFields, key, logic, sb);
		}
		return sb.toString();
	}

	public static void bracketKey(String[] fields, String key,
			String logic, StringBuilder sb) {
		//StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < fields.length; i++) {
			if (i == 0) {
				sb.append(fields[i]);
				sb.append(":");
				sb.append(key);
			} else {
				sb.append(" ");
				if (key != null && !key.isEmpty()) {
					sb.append(logic);
					sb.append(" ");
				}
				sb.append(fields[i]);
				sb.append(":");
				sb.append(key);
			}
		}
		//sb.insert(0, "(");
		sb.append(")");
		//return sb.toString();
	}

}
