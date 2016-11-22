package org.bidtime.lucene.ldbc.connection;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.bidtime.lucene.base.create.LuceneCreate;
import org.bidtime.lucene.base.search.AbstractIndexSearch;
import org.bidtime.lucene.ldbc.connection.log.LogSelectSql;
import org.bidtime.lucene.ldbc.rs.LuceneSetHandler;
import org.bidtime.lucene.ldbc.rs.QueryRunnerEx;
import org.bidtime.lucene.ldbc.sql.xml.JsonFieldXmlsLoader;
import org.bidtime.lucene.ldbc.sql.xml.parser.EnumWord;

/**
 * @author jss
 *
 */
public class DbConnection {
	
//	private static final Logger log = LoggerFactory
//			.getLogger(DbConnection.class);

	// 使用 ThreadLocal 保存 DataSourceTransactionHolder 变量
	//private volatile static ThreadLocal<DataSourceTransactionHolder> dataSourceTransThreadLocal = new ThreadLocal<DataSourceTransactionHolder>();
	
//	private static Connection getConnOfSpringCtx(DataSource ds) throws Exception {
//		ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager
//				.getResource(ds);
//		if (conHolder == null) {
//			return null;
//		} else {
//			return conHolder.getConnection();
//		}
//	}
//	
//	public static void beginTrans(DataSource ds) {
//		DataSourceTransactionHolder h = dataSourceTransThreadLocal.get();
//		if (h == null) {
//			h = new DataSourceTransactionHolder(ds);
//			dataSourceTransThreadLocal.set(h);
//		} else {
//			h.getPut(ds);
//		}
//	}
//	
//	public static void beginTrans(DataSource ds, int level) {
//		DataSourceTransactionHolder h = dataSourceTransThreadLocal.get();
//		if (h == null) {
//			h = new DataSourceTransactionHolder(ds, level);
//			dataSourceTransThreadLocal.set(h);
//		} else {
//			h.getPut(ds, level);
//		}
//	}
//	
//	public static void beginTrans(DataSource ds, DefaultTransactionDefinition def) {
//		DataSourceTransactionHolder h = dataSourceTransThreadLocal.get();
//		if (h == null) {
//			h = new DataSourceTransactionHolder(ds, def);
//			dataSourceTransThreadLocal.set(h);
//		} else {
//			h.getPut(ds, def);
//		}
//	}
//	
//	// 提交事务
//	public static void commit(DataSource ds) {
//		DataSourceTransactionHolder h = dataSourceTransThreadLocal.get();
//		if (h != null) {
//			if (h.commit(ds, true)) {
//				dataSourceTransThreadLocal.remove();
//			}
//		}
//	}
//
//	// 回滚事务
//	public static void rollback(DataSource ds) {
//		DataSourceTransactionHolder h = dataSourceTransThreadLocal.get();
//		if (h != null) {
//			if (h.rollback(ds, true)) {
//				dataSourceTransThreadLocal.remove();
//			}
//		}
//	}
	
	protected static LuceneCreate getIndexWriter(Map<String, EnumWord> mapEnumWord) throws Exception {
		LuceneCreate idx = (LuceneCreate)JsonFieldXmlsLoader.getBean("luceneCreateTx");
		idx.initConfig(mapEnumWord);
		return idx;
	}
	
	protected static AbstractIndexSearch getIndexReader() throws Exception {
		AbstractIndexSearch idx = (AbstractIndexSearch)JsonFieldXmlsLoader.getBean("luceneReaderTx");
		return idx;
	}
	
	public static void commit(Map<String, EnumWord> mapEnumWord) throws Exception {
		LuceneCreate idx = getIndexWriter(mapEnumWord);
		idx.commit();
	}
	
	// insert
	
	public static void insert(Document doc, Map<String, EnumWord> mapEnumWord) throws Exception {
		insert(doc, mapEnumWord, true);
	}
	
	public static void insert(Document doc, Map<String, EnumWord> mapEnumWord, boolean commit) throws Exception {
		if (doc != null) {
			LuceneCreate idx = getIndexWriter(mapEnumWord);
			idx.insert(doc, commit);
		}
	}
	
	// update
	public static void update(Document doc, Term termPK, Map<String, EnumWord> mapEnumWord) throws Exception {
		update(doc, termPK, mapEnumWord, true);
	}

	public static void update(Document doc, Term termPK, Map<String, EnumWord> mapEnumWord, boolean commit) throws Exception {
		if (doc != null) {
			LuceneCreate idx = getIndexWriter(mapEnumWord);
			idx.update(doc, termPK, commit);
		}
	}
	
	public static void delete(Map<String, EnumWord> mapEnumWord, Term... terms) throws Exception {
		LuceneCreate idx = getIndexWriter(mapEnumWord);
		idx.delete(terms);
	}

//	public static void delete(Term termPK, boolean commit) throws Exception {
//		if (termPK != null) {
//			LuceneCreate idx = getIndexWriter(mapEnumWord);
//			idx.delete(termPK, commit);
//		}
//	}
	
	public static <T> T query(String words, Integer nPageIdx, Integer nPageSize,
			LuceneSetHandler<T> rsh) throws Exception {
		AbstractIndexSearch search = getIndexReader();
		return queryConn(search, words, nPageIdx, nPageSize, rsh);
	}
	
	/**
	 * @param conn
	 * @param ha
	 * @param params
	 * @param nPageIdx
	 * @param nPageSize
	 * @param rsh
	 * @return
	 * @throws Exception
	 */
	private static <T> T queryConn(AbstractIndexSearch search, String words,
			Integer pageIdx, Integer pageSize, LuceneSetHandler<T> rsh) throws Exception {
		T t = null;
//		Long startTime_count = null;
		//long startTime = 0;
		//long endTime = 0;
		QueryRunnerEx qr = new QueryRunnerEx();
		try {
			//startTime = System.currentTimeMillis();
			t = qr.query(search.getSearch(), search.getAnalyzer(), words, rsh, pageIdx, pageSize);
			//endTime = System.currentTimeMillis();
		} finally {
			qr = null;
			if (LogSelectSql.logInfoOrDebug()) {
				//LogSelectSql.logFormatEndTimeNow(startTime, endTime, words);
			}
//			if (t != null && bCountSql) {
//				if (t instanceof ResultDTO) {
//					((ResultDTO) t).setLen(nTotalRows);
//				}
//			}
		}
		return t;
	}

}
