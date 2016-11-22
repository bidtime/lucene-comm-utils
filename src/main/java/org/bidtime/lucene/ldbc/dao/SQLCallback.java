package org.bidtime.lucene.ldbc.dao;

import java.sql.SQLException;

/*
 * author: jss
 * 	is to get pk after, to do this.
 * example:
 * 
 * Long l = dao.insertForPK(duty, 
		new SQLCallback<TTableProps, GsonRow>(){  
            @Override  
            public String getSql(TTableProps, GsonRow g) throws SQLException {
	        	return tp
	        }  
		});
 */
public abstract class SQLCallback<R, T, Z> {
	
	public SQLCallback() {
	}

	public abstract String getSql(R r, T t, Z z) throws SQLException;
	
}
