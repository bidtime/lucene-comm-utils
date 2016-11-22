package org.bidtime.lucene.ldbc.rs;

import java.sql.SQLException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public interface LuceneSetHandler<T> {

    /**
     * Turn the <code>ResultSet</code> into an Object.
     *
     * @param rs The <code>ResultSet</code> to handle.  It has not been touched
     * before being passed to this method.
     *
     * @return An Object initialized with <code>ResultSet</code> data. It is
     * legal for implementations to return <code>null</code> if the
     * <code>ResultSet</code> contained 0 rows.
     *
     * @throws SQLException if a database access error occurs
     */
    T handle(IndexSearcher searcher, TopDocs topDocs) throws Exception;

}