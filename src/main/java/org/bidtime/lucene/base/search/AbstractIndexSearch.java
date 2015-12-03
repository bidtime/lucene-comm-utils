package org.bidtime.lucene.base.search;

import java.io.File;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.base.utils.FieldsMagnt;
import org.bidtime.lucene.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

public abstract class AbstractIndexSearch {
	
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractIndexSearch.class);

	protected Analyzer analyzer;
	protected Directory indexDir;

	protected IndexReader reader;
	protected FieldsMagnt headMagt;
	
//	AbstractIndexSearch() throws Exception {
//		
//	}

	public AbstractIndexSearch(FieldsMagnt headMagt, Analyzer analyzer, Directory indexDir) throws Exception {
		setProp(headMagt, analyzer,	indexDir);
	}
	
	protected void setProp(FieldsMagnt headMagt, Analyzer analyzer,
			Directory idxDir) throws Exception {
		this.indexDir = idxDir;
		this.analyzer = analyzer;
		this.headMagt = headMagt;
		try {
			this.reader = DirectoryReader.open(indexDir);
		//} catch (IndexNotFoundException e) {
		} catch (Exception e) {
			logger.error("setProp", e);
		}
	}
	
	protected void setProp(FieldsMagnt headMagt, Analyzer analyzer,
			String idxPath) throws Exception {
		setProp(headMagt, analyzer, FSDirectory.open(new File(idxPath)));
	}

	public AbstractIndexSearch(FieldsMagnt headMagt, Analyzer analyzer,
			String idxPath) throws Exception {
		setProp(headMagt, analyzer, FSDirectory.open(new File(idxPath)));
	}

	public AbstractIndexSearch(String fileSource, String idxPath) throws Exception {
		setProp(new FieldsMagnt(fileSource), new IKAnalyzer4PinYin(false),
				FSDirectory.open(new File(idxPath)));
	}
	
	public AbstractIndexSearch(FieldsMagnt headMagt, String idxPath) throws Exception {
		setProp(headMagt, new IKAnalyzer4PinYin(false), idxPath);
	}

	public AbstractIndexSearch(FieldsMagnt headMagt, Directory indexDir) throws Exception {
		this(headMagt, new IKAnalyzer4PinYin(false), indexDir);
	}
	
	public AbstractIndexSearch(String sourceFile, Directory indexDir) throws Exception {
		this(new FieldsMagnt(sourceFile),
				new IKAnalyzer4PinYin(false), indexDir);
	}
	
	protected abstract IndexSearcher getSearch() throws Exception;
	
	protected abstract void closeSearch(IndexSearcher srh) throws Exception;

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String[] head) throws Exception {
		Set<String> setDateTime = headMagt.getMapDateTime();
		return searchIt(words, pageIdx, pageSize,
				setDateTime, head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort, String[] head) throws Exception {
		Set<String> setDateTime = headMagt.getMapDateTime();
		return searchIt(words, pageIdx, pageSize,
				sort, setDateTime, head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort) throws Exception {
		Set<String> setDateTime = headMagt.getMapDateTime();
		return searchIt(words, pageIdx, pageSize,
				sort, setDateTime);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize)
			throws Exception {
		Set<String> setDateTime = headMagt.getMapDateTime();
		return searchIt(words, pageIdx, pageSize, setDateTime);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String sortFld, boolean reverse) throws Exception {
		Sort sort = headMagt.getSortOfField(sortFld, reverse);
		Set<String> setDateTime = headMagt.getMapDateTime();
		return searchIt(words, pageIdx, pageSize,	sort, 
				setDateTime);
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Set<String> mapDataTime, String[] head) throws Exception {
		ResultDTO dto = null;
		IndexSearcher is = this.getSearch();
		try {
			dto = SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
					mapDataTime, head);
		} finally {
			this.closeSearch(is);
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Sort sort, Set<String> mapDataTime, String[] head) throws Exception {
		ResultDTO dto = null;
		IndexSearcher is = this.getSearch();
		try {
			dto = SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
					sort, mapDataTime, head);
		} finally {
			this.closeSearch(is);
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Sort sort, Set<String> mapDataTime) throws Exception {
		ResultDTO dto = null;
		IndexSearcher is = this.getSearch();
		try {
			dto = SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
					sort, mapDataTime);
		} finally {
			this.closeSearch(is);
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Set<String> mapDataTime) throws Exception {
		ResultDTO dto = null;
		IndexSearcher is = this.getSearch();
		try {
			dto = SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
				mapDataTime);
		} finally {
			this.closeSearch(is);
		}
		return dto;
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
