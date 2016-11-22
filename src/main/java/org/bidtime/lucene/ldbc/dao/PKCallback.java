package org.bidtime.lucene.ldbc.dao;

import java.sql.SQLException;

/*
 * author: jss
 * 	is to get pk after, to do this.
 * example:
 * 
 * Long l = dao.insertForPK(duty, 
		new PKCallback<Map<String, Object>, Long>(){  
            @Override  
            public Map<String, Object> getIt(Long l) throws SQLException {
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("code", String.valueOf(l+1000) + "4");
	        	map.put("id", l);
	        	return map;
	        }  
		});
 */
public abstract class PKCallback<R, T> {
	
	private int result;
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public PKCallback() {
	}

	public abstract R getIt(T t) throws SQLException;
	
}
