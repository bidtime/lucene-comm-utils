package com.bidtime.lucene.base;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.bidtime.dbutils.gson.ResultDTO;

import com.bidtime.lucene.utils.SearchUtils;

public class LuceneSearch {

	Analyzer analyzer;
	Directory indexDir;

	IndexReader reader;
	IndexSearcher searcher;

	public LuceneSearch(Directory dir, Analyzer analyzer) throws Exception {
		this.indexDir = dir;
		this.analyzer = analyzer;
		initial();
	}

	public void initial() throws Exception {
		reader = DirectoryReader.open(indexDir);
		searcher = new IndexSearcher(reader);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String[] head) throws Exception {
		return SearchUtils.search(searcher, analyzer, words, pageIdx, pageSize,
				head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort, String[] head) throws Exception {
		return SearchUtils.search(searcher, analyzer, words, pageIdx, pageSize,
				sort, head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort) throws Exception {
		return SearchUtils.search(searcher, analyzer, words, pageIdx, pageSize,
				sort);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize)
			throws Exception {
		return SearchUtils.search(searcher, analyzer, words, pageIdx, pageSize);
	}

//	@SuppressWarnings("rawtypes")
//	public ResultDTO search(String words, Integer pageSize) throws Exception {
//		return search(words, 0, pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO searchTerm(String field, String words, Integer pageSize)
//			throws Exception {
//		return SearchUtils.searchTerm(searcher, analyzer, field, words, null,
//				pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO searchTerm(String field, String words, String[] head,
//			Integer pageSize) throws Exception {
//		return SearchUtils.searchTerm(searcher, analyzer, field, words, head,
//				pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO search(Query query, String words, Integer pageSize)
//			throws Exception {
//		return SearchUtils.search(searcher, query, words, pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO search(Query query, String words, String[] head,
//			Integer pageSize) throws Exception {
//		return SearchUtils.search(searcher, query, words, head, pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO search(String words, Integer pageSize) throws Exception {
//		return SearchUtils.search(searcher, analyzer, words, pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO search(String field, String words, Integer pageSize)
//			throws Exception {
//		return SearchUtils.search(searcher, analyzer, field, words, pageSize);
//	}
//
//	@SuppressWarnings("rawtypes")
//	public ResultDTO search(String field, String words, String[] head,
//			Integer pageSize) throws Exception {
//		return SearchUtils.search(searcher, analyzer, field, words, head,
//				pageSize);
//	}
//
//	@SuppressWarnings({ "deprecation", "rawtypes" })
//	public ResultDTO searchPY(String words, String[] fields, String[] head,
//			Integer pageSize) throws Exception {
//		// 使用QueryParser查询分析器构造Query对象
//		BooleanQuery fieldBoolQuery = new BooleanQuery();
//		for (int i = 0; i < fields.length; i++) {
//			QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, fields[i],
//					analyzer);
//			Query queryWords = qp.parse(words);
//			fieldBoolQuery.add(queryWords, BooleanClause.Occur.SHOULD);
//		}
//		BooleanQuery innerBoolQuery = new BooleanQuery();
//		innerBoolQuery.add(fieldBoolQuery, BooleanClause.Occur.MUST);
//		//
//		return search(innerBoolQuery, words, head, pageSize);
//	}

}
