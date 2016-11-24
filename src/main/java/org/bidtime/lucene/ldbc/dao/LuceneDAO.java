package org.bidtime.lucene.ldbc.dao;

import java.util.Map;

import org.bidtime.lucene.ldbc.connection.SqlLoadUtils;
import org.bidtime.lucene.ldbc.rs.handler.LuceneSetHandler;


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

	public void insert(Object o) throws Exception {
		SqlLoadUtils.insert(this.getClass(), o);
	}

	public void insert(Map<String, Object> map) throws Exception {
		SqlLoadUtils.insert(this.getClass(), map);
	}

	public void delete(Map<String, Object> map) throws Exception {
		SqlLoadUtils.delete(this.getClass(), map);
	}

	public void update(Map<String, Object> map) throws Exception {
		SqlLoadUtils.update(this.getClass(), map);
	}

	public void update(Object o) throws Exception {
		SqlLoadUtils.update(this.getClass(),	o);
	}
	
	// query
	public <T> T query(String words, Integer nPageIdx, Integer nPageSize,
			LuceneSetHandler<T> rsh) throws Exception {
		return SqlLoadUtils.query(this.getClass(), words, nPageIdx, nPageSize, rsh);		
	}
	
	public <T> T query(String words, LuceneSetHandler<T> rsh) throws Exception {
		return SqlLoadUtils.query(this.getClass(), words, rsh);		
	}
	
}
