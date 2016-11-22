package org.bidtime.lucene.ldbc.rs.handler;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.bidtime.lucene.ldbc.rs.BeanAdapt;
import org.bidtime.lucene.ldbc.rs.BeanProcessorEx;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承AbstractListDTOHandler类
 *
 */

@SuppressWarnings("serial")
public class BeanListDTOHandler<T> extends AbstractListDTOHandler<T> {

	public BeanListDTOHandler(Class<T> type) {
		this(type, false);
	}

	public BeanListDTOHandler(Class<T> type, boolean countSql) {
		this(type, countSql, BeanAdapt.AUTO);
	}

	public BeanListDTOHandler(Class<T> type, BeanAdapt beanAdapt) {
		this(type, false, beanAdapt);
	}

//	public BeanListDTOHandler(Class<T> type, BeanProcessorEx convert,
//			boolean countSql) {
//		this(type, convert, countSql, BeanAdapt.AUTO);
//	}

	public BeanListDTOHandler(Class<T> type, boolean countSql, BeanAdapt beanAdapt) {
		this(type, new BeanProcessorEx(), countSql, beanAdapt);
	}

	public BeanListDTOHandler(Class<T> type, BeanAdapt beanAdapt, boolean countSql) {
		this(type, new BeanProcessorEx(), countSql, beanAdapt);
	}

	public BeanListDTOHandler(Class<T> type, BeanProcessorEx convert,
			boolean countSql, BeanAdapt beanAdapt) {
		super.setProp(type, convert, countSql, beanAdapt);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T handleRow(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		return (T) this.convert.toBean(searcher, topDocs, this.type, this.mapBeanPropColumns);
	}

}
