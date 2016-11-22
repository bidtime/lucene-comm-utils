package org.bidtime.lucene.ldbc.sql.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bidtime.dbutils.jdbc.sql.ArrayUtils;
import org.bidtime.lucene.ldbc.sql.xml.parser.ColumnPro;
import org.bidtime.utils.basic.ObjectComm;

/*
 * author: jss
 * sql 工具
 */
public class SqlUtils {

	public static String getWhereSqlOfListObject_like(Object[] listFlds) {
		if (listFlds != null && listFlds.length > 0) {
			StringBuilder sReturn = new StringBuilder();
			for (int i = 0; i < listFlds.length; i++) {
				if (i == 0) {
					sReturn.append((String) listFlds[i]);
					sReturn.append(" like ?");
				} else {
					sReturn.append(" and ");
					sReturn.append((String) listFlds[i]);
					sReturn.append(" like ?");
				}
			}
			return sReturn.toString();
		} else {
			return null;
		}
	}

	private static String getWhereSqlOfListObject(Object[] listFlds) {
		if (listFlds != null && listFlds.length > 0) {
			StringBuilder sReturn = new StringBuilder();
			sReturn.append("(");
			for (int i = 0; i < listFlds.length; i++) {
				if (i == 0) {
					sReturn.append((String) listFlds[i]);
					sReturn.append("=?");
				} else {
					sReturn.append(" and ");
					sReturn.append((String) listFlds[i]);
					sReturn.append("=?");
				}
			}
			sReturn.append(")");
			return sReturn.toString();
		} else {
			return null;
		}
	}

	private static String getInsertFldValsSubSqlOfListObject(String[] listFlds) {
		if (listFlds != null && listFlds.length > 0) {
			StringBuilder sbFlds = new StringBuilder();
			StringBuilder sbVals = new StringBuilder();
			try {
				for (int i = 0; i < listFlds.length; i++) {
					if (i == 0) {
						sbFlds.append(listFlds[i]);
						sbVals.append("?");
					} else {
						sbFlds.append(",");
						sbFlds.append(listFlds[i]);
						//
						sbVals.append(",");
						sbVals.append("?");
					}
				}
				StringBuilder sql = new StringBuilder();
				try {
					sql.append("(");
					sql.append(sbFlds);
					sql.append(")");

					sql.append(" ");
					sql.append("values");

					sql.append("(");
					sql.append(sbVals);
					sql.append(")");
					return sql.toString();
				} finally {
					sql.setLength(0);
					sql = null;
				}
			} finally {
				sbFlds.setLength(0);
				sbFlds = null;
				sbVals.setLength(0);
				sbVals = null;
			}
		} else {
			return null;
		}
	}

	private static String getSelectSubSqlOfListObject(Map<String, ColumnPro> map, List<String> listPk) {
		String sql = null;
		if (map != null && !map.isEmpty()) {
			listPk = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, ColumnPro> entry : map.entrySet()) {
				if (sb.length()>0) {
					sb.append("\r");
				}
				String key = entry.getKey();
				ColumnPro pro = entry.getValue();
				sb.append(key);
				sb.append(" as ");
				sb.append(pro.getColumn());
				if (pro.getPk()) {
					listPk.add(pro.getColumn());
				}
			}
			sql = sb.toString();
		}
		return sql;
	}

	private static String getUpdateSubSqlOfListObject(Object[] listFlds) {
		String sql = null;
		if (listFlds != null && listFlds.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < listFlds.length; i++) {
				String sTemp = (String) listFlds[i];
				if (i > 0) {
					sb.append(",");
				}
				sb.append(sTemp);
				sb.append("=?");
			}
			sql = sb.toString();
		}
		return sql;
	}

	public static String getInsertOfTable(String tableName,
			String sFldValsSubSql) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ");
		sql.append(tableName);
		sql.append(sFldValsSubSql);
		return sql.toString();
	}

	public static String getInsertOfTable(String tableName,	String sFldValsSubSql, String insSql) {
		StringBuilder sql = new StringBuilder();
		sql.append(insSql);
		sql.append(" ");
		sql.append(tableName);
		sql.append(sFldValsSubSql);
		return sql.toString();
	}

	public static String getUpdateOfTable(String tableName, String sFldVals,
			String sWhereSql) {
		StringBuilder sql = new StringBuilder();
		sql.append("update ");
		sql.append(tableName);
		sql.append(" set ");
		sql.append(sFldVals);
		if (StringUtils.isNotEmpty(sWhereSql)) {
			sql.append(" where ");
			sql.append(sWhereSql);
		}
		return sql.toString();
	}

	public static String getSelectOfTable(String tableName, String sFldVals,
			String sWhereSql) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(sFldVals);
		sql.append("from ");
		sql.append(tableName);
		if (StringUtils.isNotEmpty(sWhereSql)) {
			sql.append(" where ");
			sql.append(sWhereSql);
		}
		return sql.toString();
	}

	public static String getDeleteOfTable(String tableName, String sWhereSql) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(tableName);
		if (StringUtils.isNotEmpty(sWhereSql)) {
			sql.append(" where ");
			sql.append(sWhereSql);
		}
		return sql.toString();
	}

	public static String getCountSql(String sql) {
		return "select count(*) from (" + sql + ")a";
	}

	/*
	 * 通过json名称,获取可以执行的insert sql
	 */
	public static String getInsertSql(String tableName, String[] fieldColumn,
			String[] listPK) {
		String[] arAllFlds = ArrayUtils.mergeArrayObject(fieldColumn, listPK);
		return getInsertSql(tableName, arAllFlds);
	}

	/*
	 * 通过json名称,获取可以执行的insert sql
	 */
	public static String getInsertSql(String tableName, String[] arAllFlds) {
		String fldValsql = SqlUtils
				.getInsertFldValsSubSqlOfListObject(arAllFlds);
		return SqlUtils.getInsertOfTable(tableName, fldValsql);
	}

	/*
	 * 通过json名称,获取可以执行的insert sql
	 */
	public static String getInsertSql(String tableName, String[] arAllFlds, String insSql) {
		String fldValsql = SqlUtils
				.getInsertFldValsSubSqlOfListObject(arAllFlds);
		return SqlUtils.getInsertOfTable(tableName, fldValsql, insSql);
	}

	/*
	 * 通过json名称,获取可以执行的update sql
	 */
	public static String getUpdateSql(String tableName, Object[] fieldColumn,
			Object[] listPK) {
		String sFldValsSql = getUpdateSubSqlOfListObject(fieldColumn);
		String sWhereSql = getWhereSqlOfListObject(listPK);
		return getUpdateOfTable(tableName, sFldValsSql, sWhereSql);
	}

	public static String getSelectSql(String tableName, Map<String, ColumnPro> map) {
		List<String> listPk = new ArrayList<String>();
		try {
			String sFldValsSql = getSelectSubSqlOfListObject(map, listPk);
			String sWhereSql = getWhereSqlOfListObject(listPk.toArray());
			return getSelectOfTable(tableName, sFldValsSql, sWhereSql);
		} finally {
			listPk.clear();
			listPk = null;
		}
	}

	/*
	 * 获取可以执行的delete sql
	 */
	public static String getDeleteSql(String tableName, Object[] listPK) {
		String sWhereSql = getWhereSqlOfListObject(listPK);
		return getDeleteOfTable(tableName, sWhereSql);
	}

	/*
	 * 获取可以执行的delete sql
	 */
	public static String getDeleteSql(String tableName, Object[] listPK,
			Object[] pks) {
		StringBuilder sWhereSql = new StringBuilder();
		for (int i = 0; i < pks.length; i++) {
			if (i == 0) {
				sWhereSql.append(getWhereSqlOfListObject(listPK));
			} else {
				sWhereSql.append(" or ");
				sWhereSql.append(getWhereSqlOfListObject(listPK));
			}
		}
		return getDeleteOfTable(tableName, sWhereSql.toString());
	}

	public static final int getObjectType(Object param) {
		if (param instanceof Integer) {
			return Types.INTEGER;
		} else if (param instanceof String) {
			return Types.VARCHAR;
		} else if (param instanceof Double) {
			return Types.DOUBLE;
		} else if (param instanceof Float) {
			return Types.FLOAT;
		} else if (param instanceof BigInteger) {
			return Types.BIGINT;
		} else if (param instanceof Long) {
			return Types.BIGINT;
		} else if (param instanceof Boolean) {
			return Types.BOOLEAN;
		} else if (param instanceof Date) {
			return Types.DATE;
		} else if (param instanceof BigDecimal) {
			return Types.NUMERIC;
		} else {
			return Types.VARCHAR;
		}
	}

	// decimal, numberic, datetime, BIGINT
	public static final int getObjectType(String typeName) {
		// int type = Types.VARCHAR;
		if (typeName.equalsIgnoreCase("int")
				|| typeName.equalsIgnoreCase("integer")) {
			return Types.INTEGER;
		} else if (typeName.equalsIgnoreCase("tinyint")) {
			return Types.TINYINT;
		} else if (typeName.equalsIgnoreCase("smallint")) {
			return Types.SMALLINT;
		} else if (typeName.equalsIgnoreCase("mediumint")) {
			return Types.INTEGER;
		} else if (typeName.equalsIgnoreCase("decimal")
				|| typeName.equalsIgnoreCase("numberic")) {
			return Types.DOUBLE;
		} else if (typeName.equalsIgnoreCase("float")
				|| typeName.equalsIgnoreCase("real")) {
			return Types.FLOAT;
		} else if (typeName.equalsIgnoreCase("bigint")) {
			return Types.BIGINT;
		} else if (typeName.equalsIgnoreCase("varchar")) {
			return Types.VARCHAR;
		} else if (typeName.equalsIgnoreCase("char")) {
			return Types.CHAR;
			// } else if (param instanceof Long) {
			// return Types.BIGINT;
		} else if (typeName.equalsIgnoreCase("bit")) {
			return Types.BOOLEAN;
		} else if (typeName.equalsIgnoreCase("datetime")) {
			return Types.DATE;
		} else if (typeName.equalsIgnoreCase("date")) {
			return Types.DATE;
		} else if (typeName.equalsIgnoreCase("time")) {
			return Types.TIME;
		} else if (typeName.equalsIgnoreCase("timestamp")) {
			return Types.TIMESTAMP;
			// } else if (param instanceof BigDecimal) {
			// return Types.NUMERIC;
		} else {
			return Types.VARCHAR;
		}
	}

	public static Object getDefaultOfType(int type, Object o) {
		if (o == null) {
			return null;
		} else {
			if (type == Types.VARCHAR || type == Types.CHAR) {
				return ObjectComm.objectToString(o);
			} else if (type == Types.BIT || type == Types.INTEGER
					|| type == Types.TINYINT || type == Types.SMALLINT
					|| type == Types.INTEGER) {
				Integer i1 = ObjectComm.objectToInteger(o);
				return i1;
				// } else if (type == Types.SMALLINT) {
				// Long v1 = ObjectComm.objectToLong(o);
				// return v1;
			} else if (type == Types.FLOAT || type == Types.DOUBLE) {
				Double v1 = ObjectComm.objectToDouble(o);
				return v1;
			} else if (type == Types.BIGINT) {
				BigInteger v1 = ObjectComm.objectToBigInteger(o);
				return v1;
				// } else if (type == Types.DATE) {
				// if (((String)o).equalsIgnoreCase("now()")) {
				//
				// } else {
				//
				// }
				// BigInteger v1 = ObjectComm.objectToBigInteger(o);
				// return v1;
				// } else if (o instanceof BigDecimal) {
				// BigDecimal v1 = ObjectComm.objectToBigDecimal(o);
				// return v1;
				// } else if (o instanceof BigDecimal) {
				// BigDecimal v1 = ObjectComm.objectToBigDecimal(o);
				// return v1;
			} else {
				return o;
			}
		}
	}

	public static boolean isNumberic(int type) {
		if (type == Types.INTEGER || type == Types.TINYINT
				|| type == Types.SMALLINT || type == Types.DOUBLE
				|| type == Types.FLOAT || type == Types.BIGINT) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDateTime(int type) {
		if (type == Types.DATE) {
			return true;
		} else {
			return false;
		}
	}

}
