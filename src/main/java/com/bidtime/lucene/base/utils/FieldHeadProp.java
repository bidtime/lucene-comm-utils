package com.bidtime.lucene.base.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class FieldHeadProp {
	
	Integer recNo;

	public Integer getRecNo() {
		return recNo;
	}

	public void setRecNo(Integer recNo) {
		this.recNo = recNo;
	}

	String head;
	String dataType;
	Integer pyType;		//pinyinType:0/1/2 ->	HANZI/PINYIN/SHOUZIMU
	String pyHead;		//>=1, 为拼音对应的中文字段名
	
	private boolean leftEqualIgnoreCase(String s1, String s2) {
		s1 = s1.trim();
		s2 = s2.trim();
		if (s1.length() == s2.length()) {
			return StringUtils.equalsIgnoreCase(s1, s2);
		} else if (s1.length() > s2.length()) {
			return StringUtils.equalsIgnoreCase(s1.substring(0, s2.length()), s2);
		} else {
			return StringUtils.equalsIgnoreCase(s1, s2.substring(0, s1.length()));
		}
	}
	
	public Field getFieldOfDataType(String value) {
		return getFieldOfDataType(head, value);
	}
	
	private Field getFieldOfDataType(String head, String value) {
		Field field = null;
		if (leftEqualIgnoreCase("int", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.INT);
			field = new IntField(head, Integer.parseInt(value), fieldType);
		} else if (leftEqualIgnoreCase("double", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.DOUBLE);
			field = new DoubleField(head, Double.parseDouble(value), fieldType);
		} else if (leftEqualIgnoreCase("float", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.FLOAT);
			field = new FloatField(head, Float.parseFloat(value), fieldType);
		} else if (leftEqualIgnoreCase("long", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.LONG);
			field = new LongField(head, Long.parseLong(value), fieldType);
		} else if (leftEqualIgnoreCase("stored", dataType)) {
			field = new StoredField(head, value);
		} else if (leftEqualIgnoreCase("string", dataType)) {
			field = new StringField(head, value, fieldType.stored() ? Field.Store.YES : Field.Store.NO);
		} else if (leftEqualIgnoreCase("text", dataType)) {
			field = new TextField(head, value, fieldType.stored() ? Field.Store.YES : Field.Store.NO);
		} else {
			field = new Field(head, value, fieldType);
		}
		return field;
	}
	
	public String getPYHead() {
		return pyHead;
	}
	
	public Integer getPYType() {
		return pyType;
	}
	
	public boolean isPYType() {
		return pyType > 0 ? true : false;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getDataType() {
		return dataType;
	}
	
	FieldType fieldType;
	
	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public FieldHeadProp(Integer recNo, String head, String dataType, 
			String index, String tokenized, String storeType) {
		this.recNo = recNo;
		this.head = head;
		this.fieldType = arrayToFieldType(index, tokenized, storeType);
		doDataType(dataType);
	}
	
	private FieldType arrayToFieldType(String index, String tokenized, String storeType) {
		FieldType ft = new FieldType();
		//Index.YES/Index.NO
		boolean bIndex = (index != null && 
				!index.equalsIgnoreCase("Index.YES")) ? false : true;
		if (bIndex) {
			ft.setIndexed(true);
			//ft.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		} else {
			ft.setIndexed(false);
		}
		
		//Tokenized.YES/Tokenized.NO
		boolean bTokenized = (tokenized != null && 
				!tokenized.equalsIgnoreCase("Tokenized.YES")) ? false : true;
		ft.setTokenized(bTokenized);

		//Stored.YES/Stored.NO
		boolean bStoreType = (storeType != null && 
				!storeType.equalsIgnoreCase("Store.YES")) ? false : true;
		ft.setStored(bStoreType);
		//return
		return ft;
	}
	
	/*
	 * string/PINYIN/carTypeName
	 */
	public void doDataType(String dataType) {
		String[] dataTypes = dataType.split("/");
		this.dataType = dataTypes[0];
		if ( dataTypes.length > 1 ) {
			//pyType = Integer.parseInt(dataTypes[1]);
			String pyTInput = dataTypes[1];
			if (StringUtils.equalsIgnoreCase(pyTInput, "PINYIN")) {
				pyType = 1;
			} else if (StringUtils.equalsIgnoreCase(pyTInput, "SHOUZIMU")) {
				pyType = 2;
			} else {
				pyType = 0;				
			}
			pyHead = dataTypes[2];
		} else {
			pyType = 0;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("recNo:");
		sb.append(recNo);
		sb.append(",");
		sb.append("head:");
		sb.append(head);
		sb.append(",");
		sb.append("dataType:");
		sb.append(dataType);
		sb.append(",");
		sb.append("pinyinType:");
		sb.append(pyType);
		sb.append(",");
		sb.append("pinyinHead:");
		sb.append(pyHead);
		sb.append(",");
		sb.append("fieldType:");
		sb.append(fieldType);
		return sb.toString();
	}

}
