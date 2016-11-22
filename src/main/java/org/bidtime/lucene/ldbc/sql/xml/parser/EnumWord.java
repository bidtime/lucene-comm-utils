package org.bidtime.lucene.ldbc.sql.xml.parser;

/**
 * Created by jss on 2016/03/28.
 */
public enum EnumWord {
	
	HANZI("hanzi", 0), PINYIN("pinyin", 1), SHOUZIMU("shouzimu", 2);

	// 构造方法
	private EnumWord(String name, Integer idx) {
		this.name = name;
		this.idx = idx.shortValue();
	}
	
	// 普通方法
	public static String getName(Integer idx) {
		if (idx == null) {
			return null;
		}
		for (EnumWord c : EnumWord.values()) {
			if (c.getIdx() == idx) {
				return c.name;
			}
		}
		return null;
	}
	
	public static EnumWord getEnum(String name) {
		if (name == null) {
			return null;
		}
		for (EnumWord c : EnumWord.values()) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public static EnumWord getEnum(String name, EnumWord eDef) {
		EnumWord ret = getEnum(name);
		if (ret != null) {
			return ret;
		} else {
			return eDef;
		}
	}
	
	public static Short getIdx(String name) {
		if (name == null) {
			return null;
		}
		for (EnumWord c : EnumWord.values()) {
			if (c.getName().equals(name)) {
				return c.idx;
			}
		}
		return null;
	}

	public static Short getIdx(String idx, Short val) {
		Short ret = getIdx(idx);
		if (ret != null) {
			return ret;
		} else {
			return val;
		}
	}

	public static String getName(Integer idx, String val) {
		String ret = getName(idx);
		if (ret != null) {
			return ret;
		} else {
			return val;
		}
	}
	
	// 普通方法
	public static boolean exists(String name) {
		if (name == null) {
			return false;
		}
		for (EnumWord c : EnumWord.values()) {
			if (c.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPYType() {
		return this.idx > 0 ? true : false;
	}
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getIdx() {
		return idx;
	}

	public void setIdx(short idx) {
		this.idx = idx;
	}

	private short idx;

}
