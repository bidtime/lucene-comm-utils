package org.bidtime.lucene.busScope.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BusScope implements Serializable {
	private Integer id;
	private Integer pid;
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
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
	private String pname;
//	private String name_pinyin;
//	private String name_shouzimu;
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
}