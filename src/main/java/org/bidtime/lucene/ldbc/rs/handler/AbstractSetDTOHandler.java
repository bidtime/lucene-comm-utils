package org.bidtime.lucene.ldbc.rs.handler;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.bidtime.dbutils.jdbc.rs.handle.cb.SetCallback;
import org.bidtime.lucene.ldbc.rs.handler.ext.LuceneSetDTOHandler;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承ResultSetDTOHandler类
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractSetDTOHandler<T> extends
	LuceneSetDTOHandler<Set<T>> {
	
	protected SetCallback<T> ccb;

	protected Set<T> newCollect() {
		return new HashSet<T>();
	}

	@Override
	public Set<T> doDTO(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		if (topDocs.totalHits == 0) {
			return null;
		} else {
			Set<T> collect = null;
			if (ccb != null) {
				collect = ccb.callback();
			} else {
				collect = this.newCollect();
			}
			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
				collect.add(handleRow(searcher, topDocs.scoreDocs[i]));
			}
			return collect;
		}
	}

	/**
	 * Row handler. Method converts current row into some Java object.
	 * 
	 * @param rs
	 *            <code>ResultSet</code> to process.
	 * @return row processing result
	 * @throws Exception
	 *             error occurs
	 */
	protected abstract T handleRow(IndexSearcher searcher, ScoreDoc topDoc) throws Exception;

}
