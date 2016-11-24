package org.bidtime.lucene.ldbc.rs.handler;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.bidtime.lucene.ldbc.rs.BeanAdapt;
import org.bidtime.lucene.ldbc.rs.BeanProcessorEx;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承AbstractListDTOHandler类
 *
 */

@SuppressWarnings("serial")
public class BeanListLDTOHandler<T> extends AbstractListDTOHandler<T> {

	public BeanListLDTOHandler(Class<T> type) {
		this(type, false);
	}

	public BeanListLDTOHandler(Class<T> type, boolean countSql) {
		this(type, countSql, BeanAdapt.AUTO);
	}

	public BeanListLDTOHandler(Class<T> type, BeanAdapt beanAdapt) {
		this(type, false, beanAdapt);
	}

//	public BeanListDTOHandler(Class<T> type, BeanProcessorEx convert,
//			boolean countSql) {
//		this(type, convert, countSql, BeanAdapt.AUTO);
//	}

	public BeanListLDTOHandler(Class<T> type, boolean countSql, BeanAdapt beanAdapt) {
		this(type, new BeanProcessorEx(), countSql, beanAdapt);
	}

	public BeanListLDTOHandler(Class<T> type, BeanAdapt beanAdapt, boolean countSql) {
		this(type, new BeanProcessorEx(), countSql, beanAdapt);
	}

	public BeanListLDTOHandler(Class<T> type, BeanProcessorEx convert,
			boolean countSql, BeanAdapt beanAdapt) {
		super.setProp(type, convert, countSql, beanAdapt);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T handleRow(IndexSearcher searcher, ScoreDoc scoreDoc) throws Exception {
		return (T) this.convert.toBean(searcher, scoreDoc, this.type, this.mapBeanPropColumns);
	}

}
