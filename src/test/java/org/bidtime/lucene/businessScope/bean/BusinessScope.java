package org.bidtime.lucene.businessScope.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BusinessScope implements Serializable {
	private Integer id;

	private Integer pId;
	private String pName;

	public String getpName() {
		return pName;
	}
	public void setpName(String pName) {
		this.pName = pName;
	}
	private Integer bsId;
	private String bsName;
	// private String name_pinyin;
	// private String name_shouzimu;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getpId() {
		return pId;
	}
	public void setpId(Integer pId) {
		this.pId = pId;
	}

	public Integer getBsId() {
		return bsId;
	}
	public void setBsId(Integer bsId) {
		this.bsId = bsId;
	}
	public String getBsName() {
		return bsName;
	}
	public void setBsName(String bsName) {
		this.bsName = bsName;
	}
}
