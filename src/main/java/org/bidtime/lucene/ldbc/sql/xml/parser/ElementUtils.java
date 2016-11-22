package org.bidtime.lucene.ldbc.sql.xml.parser;

import org.bidtime.utils.basic.ObjectComm;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class ElementUtils {

	@SuppressWarnings("unchecked")
	public static <T> T strToVal(String value, Class<T> clz) {
		if (value != null) {
			if (clz.equals(Integer.class)) {
				return (T)ObjectComm.objectToInteger(value);
			} else if (clz.equals(Boolean.class)) {
				return (T)ObjectComm.objectToBoolean(value);
			} else if (clz.equals(Double.class)) {
				return (T)ObjectComm.objectToDouble(value);
			} else if (clz.equals(Short.class)) {
				return (T)ObjectComm.objectToShort(value);
			} else {
				return (T)value;
			}
		} else {
			return null;
		}
	}

	public static <T> T getValue(Element e, String attr, Class<T> clz) {
		Attribute a = e.attribute(attr);
		if (a != null) {
			String value = a.getValue();
			return strToVal(value, clz);
		} else
			return null;
	}

	public static <T> T getValue(Element e, String attr, Class<T> clz, T t) {
		T ret = getValue(e, attr, clz);
		if (ret == null) {
			return t;
		} else {
			return ret;
		}
	}
	
	public static void main(String[] args) {
		testIt(Integer.class);
	}

	private static <T> T testIt(Class<T> clz) {
		if (clz.equals(Integer.class)) {
			System.out.println(Integer.class);
		} else if (clz.equals(Boolean.class)) {
			System.out.println(Boolean.class);
		} else if (clz.equals(Double.class)) {
			System.out.println(Double.class);
		}
		return null;
	}

//	public static Boolean eleAttrToBoolean(Element e, String attr) {
//		Attribute a = e.attribute(attr);
//		if (a != null) {
//			return Boolean.parseBoolean(a.getValue());
//		} else {
//			return false;
//		}
//	}
//
//	public static Boolean eleAttrToBoolean(Element e, String attr, Boolean bDef) {
//		Boolean b = eleAttrToBoolean(e, attr);
//		if (b == null) {
//			return bDef;
//		} else {
//			return b;
//		}
//	}
//
//	public static String eleAttrToString(Element e, String attr) {
//		Attribute a = e.attribute(attr);
//		if (a != null) {
//			return a.getValue();
//		} else {
//			return null;
//		}
//	}
//
//	public static String eleAttrToString(Element e, String attr, String sDef) {
//		String s = eleAttrToString(e, attr);
//		if (s == null) {
//			return sDef;
//		} else {
//			return s;
//		}
//	}

}
