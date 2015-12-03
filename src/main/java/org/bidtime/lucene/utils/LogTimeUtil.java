/*
 * 打印执行的sql语句及其参数
 */
package org.bidtime.lucene.utils;

import java.util.Calendar;

public class LogTimeUtil {
	
//	public static String getSpanFmtNow(Long startTime) {
//		return "\tspan:" + LogTimeUtil.getFmtDiffNowMs(str, startTime);
//	}
	
	public static String getSpanFmtNow(String str, Long startTime) {
		return str + LogTimeUtil.getFmtDiffNowMs(str, startTime);
	}

	public static String getFmtDiffNowMs(String str, long start) {
		long end = System.currentTimeMillis();
		return getFmtDiffStartEndMs(str, start, end);
	}
	
	public static String getFmtDiffStartEndMs(String str, long start, long end) {
		return getFmtDiffMS(str, end - start);
	}
	
	public static String getFmtDiffMS(String str, long spanSeconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(spanSeconds);
		return getFmtCalendar(str, c);
	}
	
	public static String getFmtCalendar(String str, Calendar c) {
		StringBuilder sb = new StringBuilder(str);
		sb.append(c.get(Calendar.MINUTE));
		sb.append("m:");
		sb.append(c.get(Calendar.SECOND));
		sb.append("s:");
		sb.append(c.get(Calendar.MILLISECOND));
		sb.append("ms");
		sb.append(".");
		return sb.toString();
	}

}
