package org.bidtime.lucene.duty.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Duty implements Serializable {
	private Integer id;
	
	private String name;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
//	public String getName_pinyin() {
//		return name_pinyin;
//	}
//	public void setName_pinyin(String name_pinyin) {
//		this.name_pinyin = name_pinyin;
//	}
//	public String getName_shouzimu() {
//		return name_shouzimu;
//	}
//	public void setName_shouzimu(String name_shouzimu) {
//		this.name_shouzimu = name_shouzimu;
//	}
	private String code;
//	private String name_pinyin;
//	private String name_shouzimu;
}
