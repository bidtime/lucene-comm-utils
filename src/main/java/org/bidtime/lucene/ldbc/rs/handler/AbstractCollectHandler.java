package org.bidtime.lucene.ldbc.rs.handler;

import java.util.Collection;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.bidtime.dbutils.jdbc.rs.handle.cb.CollectionCallback;

/**
 * @author jss
 * 
 *         提供对从ResultSet进行预处理的功能,继承ResultSetDTOHandler类
 *
 */
public abstract class AbstractCollectHandler<T> implements LuceneSetHandler<Collection<T>> {
	
	protected CollectionCallback<T> ccb;
	
	protected abstract Collection<T> newCollect();
	
    /**
     * Whole <code>ResultSet</code> handler. It produce <code>List</code> as
     * result. To convert individual rows into Java objects it uses
     * <code>handleRow(ResultSet)</code> method.
     *
     * @see #handleRow(ResultSet)
     * @param rs <code>ResultSet</code> to process.
     * @return a list of all rows in the result set
     * @throws Exception error occurs
     */
    @Override
    public Collection<T> handle(IndexSearcher searcher, TopDocs topDocs) throws Exception {
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
     * @param rs <code>ResultSet</code> to process.
     * @return row processing result
     * @throws Exception error occurs
     */
    protected abstract T handleRow(IndexSearcher searcher, ScoreDoc scoreDoc) throws Exception;

}
