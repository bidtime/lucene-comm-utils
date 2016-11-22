/*
 * 打印执行的sql语句及其参数
 */
package org.bidtime.lucene.ldbc.connection.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogDeleteSql extends LogSqlUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LogDeleteSql.class);
	
	public static boolean logInfoOrDebug() {
		return (logger.isInfoEnabled() || logger.isDebugEnabled());
	}

	public static void logFormatTimeNow(long startTime, String sql, Object[] params, int nResult) {
		long endTime = System.currentTimeMillis();
		logFormatEndTimeNow(startTime, endTime, sql, params, nResult);
	}
	
	public static void logFormatTimeNow(long startTime, String sql, Object[] params) {
		long endTime = System.currentTimeMillis();
		logFormatEndTimeNow(startTime, endTime, sql, params);
	}

	public static void logFormatEndTimeNow(long startTime, long endTime, String sql, Object[] params, int nResult) {
		logFormatEndTimeNow(startTime, endTime, sql, params, nResult, logger);
	}
	
	public static void logFormatEndTimeNow(long startTime, long endTime, String sql, Object[] params) {
		logFormatEndTimeNow(startTime, endTime, sql, params, logger);
	}
	
}
