package org.bidtime.lucene.ldbc.rs.handler.ext;

import java.util.Map;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.bidtime.dbutils.gson.ResultDTO;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承自dbutils的ResultSetExHandler类
 *
 */
@SuppressWarnings("serial")
public class LuceneSetDTOHandler<T> extends LuceneSetExHandler<ResultDTO<T>> {

	protected Map<String, String> mapBeanPropColumns = null;

	@Override
	public ResultDTO<T> handle(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		ResultDTO<T> t = new ResultDTO<T>();
		t.setData(doDTO(searcher, topDocs));
		return t;
	}

	@SuppressWarnings("unchecked")
	public T doDTO(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		return (topDocs.totalHits == 0) ? null : (T)convert.toBean(searcher, topDocs, type, mapBeanPropColumns);
	}

}
