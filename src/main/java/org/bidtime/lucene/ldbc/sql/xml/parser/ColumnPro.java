package org.bidtime.lucene.ldbc.sql.xml.parser;

import java.util.Date;

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
import org.apache.lucene.index.IndexOptions;
import org.bidtime.utils.basic.ObjectComm;

public class ColumnPro {
	private String name;
	private String column;
	private Integer length;
	private String generator;
	
	private Boolean omitNorms;
	
	public Boolean getOmitNorms() {
		return omitNorms;
	}

	public void setOmitNorms(Boolean omitNorms) {
		this.omitNorms = omitNorms;
	}

	private Boolean notNull;
	public Boolean getNotNull() {
		return notNull;
	}

	public void setNotNull(Boolean notNull) {
		this.notNull = notNull;
	}

	private Boolean pk;
	private Object defaultValue;
	private Boolean identity;
	
	//
	private String dataType;
	
	private FieldType fieldType;
	
	@SuppressWarnings("rawtypes")
	private Class fieldClz;
	
	private Boolean store;
	public Boolean getStore() {
		return store;
	}

	public void setStore(Boolean store) {
		this.store = store;
	}

	public Boolean getTokenized() {
		return tokenized;
	}

	public void setTokenized(Boolean tokenized) {
		this.tokenized = tokenized;
	}

	private String index;
	
	public void setIndex(String s) {
		this.index = s;
	}
	
	public String getIndex() {
		return index;
	}
	
	private Boolean tokenized;
	private EnumWord wordType;		//word:0/1/2 ->	HANZI/PINYIN/SHOUZIMU
	private String wordName;

	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}
	
	public boolean isPYType() {
		return wordType.isPYType();
	}

//	public void setWordType(EnumWord wordType) {
//		this.wordType = wordType;
//	}

	public ColumnPro getWordRef() {
		return wordRef;
	}

	public void setWordRef(ColumnPro wordRef) {
		this.wordRef = wordRef;
	}

	private ColumnPro wordRef;
	
	public EnumWord getWordType() {
		return wordType;
	}
	
	public void setWordType(String wordType) {
		this.wordType = EnumWord.getEnum(wordType, EnumWord.HANZI);
	}

//	public void seteWord(EnumWord eWord) {
//		this.eWord = eWord;
//	}
	
	public boolean isPinyin() {
		return (wordType != EnumWord.HANZI) ? true : false;
	}

	public String getDataType() {
		return dataType;
	}

//	public void setDataType(int type) {
//		this.type = type;
//	}

	public Boolean getIdentity() {
		return identity;
	}

	public void setIdentity(Boolean identity) {
		this.identity = identity;
	}

	public Object getDefaultValue() {
//		if (defaultValue != null && SqlUtils.isDateTime(this.type)) {
//			if ((String.valueOf(defaultValue)).equalsIgnoreCase("now()")) {
//				return new Date();
//			} else {
//				return defaultValue;
//			}
//		} else {
			return defaultValue;
//		}
	}

	public void setDefaultValue(Object defaultValue) {
//		if (SqlUtils.isDateTime(this.type)) {			
//			if ((String.valueOf(defaultValue)).equalsIgnoreCase("now()")) {
//				this.defaultValue = defaultValue;
//			} else {
//				//this.defaultValue = DateTimeComm.yyyyMMddHHmmssToDate(String.valueOf(defaultValue));
//			}
//		} else {
//			this.defaultValue = SqlUtils.getDefaultOfType(this.type, defaultValue);
//		}
	}

	public Boolean getPk() {
		return pk;
	}

	public void setPk(Boolean pk) {
		this.pk = pk;
	}

	public ColumnPro(Boolean pk) {
		this.pk=pk;
	}
	
	public void doFinished() {
		this.fieldType = propToFieldType();
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
		this.identity = StringUtils.equals(this.generator, "identity");
	}
	
	private Field.Store getStoreOfType() {
		return getStoreOfBoolean(fieldType.stored());
	}
	
	private Field.Store getStoreOfBoolean(Boolean b) {
		return b ? Field.Store.YES : Field.Store.NO;
	}
	
	public Field getFieldOfValue(Object value) throws Exception {
		return getFieldOfDataType(this.name, value);
	}
	
	private Field getFieldOfDataType(String head, Object value) throws Exception {
		Field field = null;
		try {
			if (fieldClz.equals(IntField.class)) {
				field = new IntField(head, 
						ObjectComm.objectToInteger(value), getStoreOfType());
			} else if (fieldClz.equals(DoubleField.class)) {
				field = new DoubleField(head, 
						Double.parseDouble(String.valueOf(value)), getStoreOfType());
			} else if (fieldClz.equals(FloatField.class)) {
				field = new FloatField(head, 
						Float.parseFloat(String.valueOf(value)), getStoreOfType());
			} else if (fieldClz.equals(LongField.class)) {
				field = new LongField(head,
					Long.parseLong(String.valueOf(value)), getStoreOfType());
			} else if (fieldClz.equals(DateTimeField.class)) {
				if (value instanceof Date)  {
					field = new DateTimeField(head, (Date)value, getStoreOfType());
				} else {
					field = new DateTimeField(head, 
						Long.parseLong(String.valueOf(value)), getStoreOfType());
				}
			} else if (fieldClz.equals(StoredField.class)) {
				field = new StoredField(head, String.valueOf(value));
			} else if (fieldClz.equals(StringField.class)) {
				field = new StringField(head, 
						String.valueOf(value), 
						getStoreOfType());
			} else if (fieldClz.equals(TextField.class)) {
				field = new TextField(head,
						String.valueOf(value), getStoreOfType());
			} else {
				field = new Field(head, String.valueOf(value), fieldType);
			}
		} catch (Exception e) {
			//log.error("getFieldOfDataType: head: " + head + " -> " + value , e);
		}
		return field;
	}
	
	private static boolean leftEqualIgnoreCase(String s1, String s2) {
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
	
	private IndexOptions getIndexOpt(String s) {
		if (StringUtils.isEmpty(s) || s.equals("NONE")) {
			return IndexOptions.NONE;
		} else if (s.equals("DOCS")) {
			return IndexOptions.DOCS;			
		} else if (s.equals("DOCS_AND_FREQS")) {
			return IndexOptions.DOCS_AND_FREQS;			
		} else if (s.equals("DOCS_AND_FREQS_AND_POSITIONS")) {
			return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;			
		} else if (s.equals("DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS")) {
			return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;			
		} else {
			return IndexOptions.NONE;
		}
	}
	
	private FieldType propToFieldType() {
		FieldType ft = new FieldType();
		ft.setIndexOptions(getIndexOpt(index));
		ft.setStored(store);
		ft.setTokenized(tokenized);
		ft.setOmitNorms(this.omitNorms);
		
		this.fieldClz = getNumericTypeOfDataType(this.dataType, ft);
		
		//sfType = getStoreFieldOfHead(this.dataType);
		return ft;
	}

	@SuppressWarnings("rawtypes")
	private Class getNumericTypeOfDataType(String dataType, FieldType fieldType) {
		Class nt = null;
		if (leftEqualIgnoreCase("int", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.INT);
			nt = IntField.class;
		} else if (leftEqualIgnoreCase("double", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.DOUBLE);
			nt = DoubleField.class;
		} else if (leftEqualIgnoreCase("float", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.FLOAT);
			nt = FloatField.class;
		} else if (leftEqualIgnoreCase("long", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.LONG);
			nt = LongField.class;
		} else if (leftEqualIgnoreCase("dateTime", dataType)) {
			fieldType.setNumericType(FieldType.NumericType.LONG);
			nt = DateTimeField.class;
		} else if (leftEqualIgnoreCase("stored", dataType)) {
			nt = StoredField.class;
		} else if (leftEqualIgnoreCase("string", dataType)) {
			nt = StringField.class;
		} else if (leftEqualIgnoreCase("text", dataType)) {
			nt = TextField.class;
		} else {
			nt = Field.class;
		}
		return nt;
	}

}
