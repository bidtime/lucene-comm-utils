/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bidtime.lucene.ldbc.rs;

import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.ldbc.rs.handler.LuceneSetHandler;

/**
 * @author jss
 *
 *	这个类从QueryRunnerEx中复制来,主要增加StmtParams中的参数
 *
 *@date 2014-08-17
 *
 */

/**
 * Executes SQL queries with pluggable strategies for handling
 * <code>ResultSet</code>s.  This class is thread safe.
 *
 * @see ResultSetHandler
 */
public class QueryRunnerEx {

//	private static final Logger logger = Logger
//			.getLogger(QueryRunnerEx.class);

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunnerEx() {
        super();
    }
    
	public static TopDocs getTopDocs(IndexSearcher searcher, Query query,
			Integer pageIdx, Integer pageSize, Sort sort) throws Exception {
		/*
		 * TopScoreDocCollector topCollector = TopScoreDocCollector.create( 100,
		 * false); searcher.search(query, topCollector);
		 * System.out.println("命中：" + topCollector.getTotalHits()); // 查询当页的记录
		 * ScoreDoc[] docs = topCollector.topDocs((pageNO - 1) * pageSize,
		 * pageSize).scoreDocs;
		 */
		TopDocs topDocs = null;
		if (pageSize != null) {
			if (pageIdx == 0) {
				if (sort != null) {
					topDocs = searcher.search(query, Integer.MAX_VALUE, sort);
				} else {
					topDocs = searcher.search(query, Integer.MAX_VALUE);					
				}
			} else {
		        int rows = pageSize * pageIdx;
		        TopDocs tds = searcher.search(query, rows);
		        if (tds.totalHits > rows) {
			        ScoreDoc scoreDoc = tds.totalHits < rows ? tds.scoreDocs[tds.totalHits] : tds.scoreDocs[rows];
					if (sort != null) {
						topDocs = searcher.searchAfter(scoreDoc, query, pageSize, sort);
					} else {
						topDocs = searcher.searchAfter(scoreDoc, query, pageSize);
					}
		        } else {
		        	topDocs = tds;
		        }
			}
		} else {
			if (sort != null) {
				topDocs = searcher.search(query, Integer.MAX_VALUE, sort);
			} else {
				topDocs = searcher.search(query, Integer.MAX_VALUE);
			}
		}
		return topDocs;
	}
	 
    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * @param <T> The type of object that the handler returns
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @param params The replacement parameters.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     * 
     * 
     */
    @SuppressWarnings("rawtypes")
	public <T> T query(IndexSearcher searcher, Analyzer analyzer, String field, String words, LuceneSetHandler<T> rsh,
    		Integer pageIdx, Integer pageSize, Sort sort) throws Exception {
   		QueryParser parser = new QueryParser(field,	analyzer);
   		Query query = parser.parse(words);
   		//
   		TopDocs topDocs = getTopDocs(searcher, query, pageIdx, pageSize, sort);
   		T t = null;
   		t = rsh.handle(searcher, topDocs);
		if (t != null) {
			if (t instanceof ResultDTO) {
				((ResultDTO) t).setLen(topDocs.totalHits);
			}
		}
   		return t;
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * @param <T> The type of object that the handler returns
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @param params The replacement parameters.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     * 
     * 
     */
    @SuppressWarnings("rawtypes")
	public <T> T query(IndexSearcher searcher, Analyzer analyzer, String words, LuceneSetHandler<T> rsh,
    		Integer pageIdx, Integer pageSize, Sort sort) throws Exception {
   		QueryParser parser = new QueryParser(null, analyzer);
   		Query query = parser.parse(words);
   		//
   		TopDocs topDocs = getTopDocs(searcher, query, pageIdx, pageSize, sort);
   		T t = null;
   		t = rsh.handle(searcher, topDocs);
		if (t != null) {
			if (t instanceof ResultDTO) {
				((ResultDTO) t).setLen(topDocs.totalHits);
			}
		}
   		return t;
    }
    
    public <T> T query(IndexSearcher searcher, Analyzer analyzer, String words, LuceneSetHandler<T> rsh,
    		Integer pageIdx, Integer pageSize) throws Exception {
    	return this.query(searcher, analyzer, words, rsh, pageIdx, pageSize, null);
    }
    
    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     * @param <T> The type of object that the handler returns
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException if a database access error occurs
     */
    public <T> T query(IndexSearcher searcher, Analyzer analyzer, LuceneSetHandler<T> rsh, Sort sort) throws Exception {
    	String field="*";
    	String words = "";
        return this.<T>query(searcher, analyzer, field, words, rsh, null, null, sort);
    }

}
