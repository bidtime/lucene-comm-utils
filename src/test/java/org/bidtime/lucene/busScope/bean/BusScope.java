package org.bidtime.lucene.busScope.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BusScope implements Serializable {

	private Integer id;
	private String bsName;
	private Integer bsId;
	private Integer pId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBsName() {
		return bsName;
	}

	public void setBsName(String bsName) {
		this.bsName = bsName;
	}

	public Integer getBsId() {
		return bsId;
	}

	public void setBsId(Integer bsId) {
		this.bsId = bsId;
	}

	public Integer getpId() {
		return pId;
	}

	public void setpId(Integer pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getBsNameFull() {
		return bsNameFull;
	}

	public void setBsNameFull(String bsNameFull) {
		this.bsNameFull = bsNameFull;
	}

	public String getBsNameFirst() {
		return bsNameFirst;
	}

	public void setBsNameFirst(String bsNameFirst) {
		this.bsNameFirst = bsNameFirst;
	}

	private String pName;
	private String bsNameFull;
	private String bsNameFirst;

}
