package org.bidtime.lucene.ldbc.dao;

import java.util.Map;

import org.bidtime.lucene.ldbc.connection.SqlLoadUtils;


/**
 * @author jss
 * 
 *         提供对从dao.xml中取出sql,并执行sql
 * 
 */
public class LuceneDAO {

	protected String dsName;

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

//	public int update(Object o) throws Exception {
//		return SqlLoadUtils.update(this.getClass(),	o);
//	}

	public void insert(Map<String, Object> map) throws Exception {
		SqlLoadUtils.insert(this.getClass(), map);
	}

	public void delete(Map<String, Object> map) throws Exception {
		SqlLoadUtils.delete(this.getClass(), map);
	}

	public void update(Map<String, Object> map) throws Exception {
		SqlLoadUtils.update(this.getClass(), map);
	}
	
}
