package org.bidtime.lucene.ldbc.sql.xml.parser;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SqlHeadCountPro {
	
	private Map<String, ColProEx> mapCol;

	private static class ColProEx {
		
//		@SuppressWarnings("unused")
//		public static void main(String[] args) {
//			ColProEx p = new ColProEx("id,code,name,get", "");
//			for (int i=0; i<10; i++) {
//				String sql = "select id,code,name,memo,licnum FROM a_carsrc";
//				String s = p.replSql(sql);
//				System.out.print(i + "\t");
//				System.out.println(s);
//			}
//		}

		public ColProEx(String cols, String prop) {
			this.prop = prop;
			this.cols = cols;
		}

		private String cols;
		private String prop;
		
		private void spltToSet(String s, Set<String> set) {
			String[] ss = s.split(",");
			for (int i=0; i<ss.length; i++) {
				set.add(ss[i].trim());
			}
		}
		
		private String setToStr(Set<String> setFull, char c) {
			StringBuilder sb = null;
			try {
				for (String s : setFull) {
					if (sb == null) {
						sb = new StringBuilder(s);
					} else {
						sb.append(", ");
						sb.append(s);
					}
				}
				return sb.toString();
			} finally {
				sb.setLength(0);
				sb = null;
			}
		}
		
		private String mergeStr(String full, String mini, boolean plus) {
			Set<String> setFull = new LinkedHashSet<String>();
			Set<String> setMini = new LinkedHashSet<String>();
			spltToSet(full, setFull);
			spltToSet(mini, setMini);
			if (plus) {
				setFull.addAll(setMini);
			} else {
				setFull.removeAll(setMini);
			}
			return setToStr(setFull, ',');
		}
		
		private int getIt() {
			int pt;
			if (prop == null) {
				pt = 0;
			} else {
				if (prop.equals("+")) {
					pt = 1;
				} else if (prop.equals("-")) {
					pt = 2;
				} else {			// "*"
					pt = 3;
				}
			}
			return pt;
		}

		public String replSql(String sql) {
			Boolean propPlus = null;
			Boolean propType = null;
			int pt = getIt();
			if (pt == 1) {			//"+"
				propType = false;
				propPlus = true;
			} else if (pt == 2) {	//"-"
				propType = false;
				propPlus = false;
			} else {				//"*"
				propType = true;
				propPlus = null;
			}
			
			if (!propType) {	// -
				int fromPos = ignoreCaseIndexOf(sql, "from");
				if (fromPos > 0) {
					String preCols = null;
					int selPos = ignoreCaseIndexOf(sql, "select");		//len = 6
					if (selPos >= 0) {
						preCols = sql.substring(selPos + 6, fromPos);
					}
					if ( preCols == null || preCols.length() == 0 ) {
						return sql;
					}
					StringBuilder sb = new StringBuilder();
					try {
						sb.append(sql.substring(0, selPos + 6));
						sb.append(" ");
						sb.append( mergeStr(preCols, cols, propPlus) );
						sb.append(" ");
						sb.append(sql.substring(fromPos));
						return sb.toString();
					} finally {
						sb.setLength(0);
						sb = null;
					}
				} else {
					return sql;
				}
			} else {
				int pos = ignoreCaseIndexOf(sql, "from");
				if (pos > 0) {
					StringBuilder sb = new StringBuilder();
					try {
						int selPos = ignoreCaseIndexOf(sql, "select");		//len = 6
						if (selPos >= 0) {
							sb.append(sql.substring(0, selPos + 6));
						} else {
							sb.append(" select");
						}
						sb.append(" ");
						sb.append(cols);
						sb.append(" ");
						sb.append(sql.substring(pos));
						return sb.toString();
					} finally {
						sb.setLength(0);
						sb = null;
					}
				} else {
					return sql;
				}
			}
		}
	}

	private String id;
	private String type; // "insert", "delete", "update", "select", "call"

	private String sql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public SqlHeadCountPro() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCol(String colId, String cols, String oper) {
		if (mapCol == null) {
			mapCol = new HashMap<String, ColProEx>();
		}
		mapCol.put(colId, new ColProEx(cols, oper));
	}

	public String getSql(String colId) {
		if (mapCol != null) {
			ColProEx c = mapCol.get(colId);
			if (c == null) {
				return null;
			} else {
				return c.replSql(sql);
			}
		} else {
			return null;
		}
	}

	/** 
	 * 返回指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始，不区分大小。 
	 *  
	 * @param subject 被查找字符串。 
	 * @param search 要查找的子字符串。 
	 * @return 指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始。 
	 */  
	public static int ignoreCaseIndexOf(String subject, String search) {  
		return ignoreCaseIndexOf(subject, search,-1);  
	}
    
	public static int ignoreCaseIndexOf(String subject, String search,
			int fromIndex) {
		// 当被查找字符串或查找子字符串为空时，抛出空指针异常。
		if (subject == null || search == null) {
			throw new NullPointerException("输入的参数为空");
		}
		fromIndex = fromIndex < 0 ? 0 : fromIndex;
		if (search.equals("")) {
			return fromIndex >= subject.length() ? subject.length() : fromIndex;
		}

		int index1 = fromIndex;
		int index2 = 0;
		char c1;
		char c2;
		loop1: while (true) {
			if (index1 < subject.length()) {
				c1 = subject.charAt(index1);
				c2 = search.charAt(index2);
			} else {
				break loop1;
			}
			while (true) {
				if (isEqual(c1, c2)) {
					if (index1 < subject.length() - 1
							&& index2 < search.length() - 1) {
						c1 = subject.charAt(++index1);
						c2 = search.charAt(++index2);
					} else if (index2 == search.length() - 1) {
						return fromIndex;
					} else {
						break loop1;
					}
				} else {
					index2 = 0;
					break;
				}
			}
			// 重新查找子字符串的位置
			index1 = ++fromIndex;
		}
		return -1;
	}
	
	/** 
	 * 判断两个字符是否相等。 
	 * @param c1 字符1 
	 * @param c2 字符2 
	 * @return 若是英文字母，不区分大小写，相等true，不等返回false； 
	 *          若不是则区分，相等返回true，不等返回false。 
	 */  
	private static boolean isEqual(char c1,char c2){  
	        //  字母小写                   字母大写  
	    if(((97<=c1 && c1<=122) || (65<=c1 && c1<=90))  
	            && ((97<=c2 && c2<=122) || (65<=c2 && c2<=90))  
	            && ((c1-c2==32) || (c2-c1==32))){
	        return true;  
	    }  
	    else if(c1==c2){  
	        return true;  
	    }  
	      
	    return false;  
	} 
}
