package org.bidtime.lucene.ldbc.rs.handler;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.bidtime.lucene.ldbc.rs.BeanAdapt;
import org.bidtime.lucene.ldbc.rs.BeanProcessorEx;
import org.bidtime.lucene.ldbc.rs.handler.ext.LuceneSetDTOHandler;

/**
 * @author jss
 * 
 * 提供对从ResultSet进行预处理的功能,继承自dbutils的ResultSetDTOHandler类
 *
 */
@SuppressWarnings("serial")
public class BeanDTOHandler<T> extends LuceneSetDTOHandler<T> {

    public BeanDTOHandler(Class<T> type) {
    	this(type, false);
    }

    public BeanDTOHandler(Class<T> type, boolean countSql) {
    	this(type, countSql, BeanAdapt.AUTO);
    }
    
    public BeanDTOHandler(Class<T> type, BeanAdapt beanAdpat) {
    	this(type, false, BeanAdapt.AUTO);
    }

    public BeanDTOHandler(Class<T> type, BeanAdapt beanAdpat, boolean countSql) {
    	this(type, new BeanProcessorEx(), countSql, beanAdpat);
    }
    
    public BeanDTOHandler(Class<T> type, boolean countSql, BeanAdapt beanAdpat) {
    	this(type, new BeanProcessorEx(), countSql, beanAdpat);
    }
    
    public BeanDTOHandler(Class<T> type, BeanProcessorEx convert, boolean countSql) {
    	this(type, convert, countSql, BeanAdapt.AUTO);
    }
    
    public BeanDTOHandler(Class<T> type, BeanProcessorEx convert, boolean countSql,
    		BeanAdapt beanAdapt) {
    	super.setProp(type, convert, countSql, beanAdapt);
    }
    
	@Override
	public T doDTO(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		return super.doDTO(searcher, topDocs);
	}

}
