package org.bidtime.lucene.ldbc.rs.handler.ext;

import java.io.Serializable;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.bidtime.lucene.ldbc.rs.BeanAdapt;
import org.bidtime.lucene.ldbc.rs.BeanProcessorEx;
import org.bidtime.lucene.ldbc.rs.handler.LuceneSetHandler;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承自dbutils的ResultSetHandler类
 *
 */
@SuppressWarnings("serial")
public class LuceneSetExHandler<T> implements LuceneSetHandler<T>, Serializable {

	protected Class<T> type;

	protected BeanProcessorEx convert = null;

	//protected BeanProcessorEx ROW_PROCESSOR = new BeanProcessorEx();

	protected boolean countSql = false;

	public boolean isCountSql() {
		return countSql;
	}

	public void setCountSql(boolean countSql) {
		this.countSql = countSql;
	}

//	@SuppressWarnings({ "rawtypes" })
//	public void setProp(Class type, BeanProcessorEx convert, boolean countSql) {
//		setProp(type, convert, countSql, false);
//	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setProp(Class type, BeanProcessorEx convert, boolean countSql,
			BeanAdapt beanAdapt) {
		this.type = type;
		this.convert = convert;
		this.countSql = countSql;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setProp(Class type, BeanProcessorEx convert, boolean countSql) {
		this.type = type;
		this.convert = convert;
		this.countSql = countSql;
	}

//	@SuppressWarnings("rawtypes")
//	public void setProp(Class type, boolean countSql) {
//		setProp(type, new BeanProcessorEx(), countSql);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public void setProp(Class type) {
//		setProp(type, false);
//	}

	@Override
	public T handle(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		return (topDocs.totalHits == 0) ? null : this.convert.toBean(searcher, topDocs, this.type);
	}

}
