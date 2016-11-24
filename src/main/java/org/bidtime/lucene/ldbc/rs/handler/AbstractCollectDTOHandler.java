package org.bidtime.lucene.ldbc.rs.handler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.bidtime.dbutils.jdbc.rs.handle.cb.CollectionCallback;
import org.bidtime.lucene.ldbc.rs.handler.ext.LuceneSetDTOHandler;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承ResultSetDTOHandler类
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractCollectDTOHandler<T> extends
	LuceneSetDTOHandler<Collection<T>> {
	
	protected CollectionCallback<T> ccb;
	
	protected Collection<T> newCollect() {
		return new ArrayList<>();
	}

	@Override
	public Collection<T> doDTO(IndexSearcher searcher, TopDocs topDocs) throws Exception {
		if (topDocs.totalHits == 0) {
			return null;
		} else {
			Collection<T> collect = null;
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
	 * @throws SQLException
	 *             error occurs
	 */
	protected abstract T handleRow(IndexSearcher searcher, ScoreDoc scoreDoc) throws Exception;

}
