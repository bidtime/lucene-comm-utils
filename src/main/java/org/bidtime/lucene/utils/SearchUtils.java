package org.bidtime.lucene.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.utils.basic.ObjectComm;
import org.bidtime.utils.comm.CaseInsensitiveHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(SearchUtils.class);

	@SuppressWarnings("rawtypes")
	private static ResultDTO scoreDocToDTO(IndexSearcher searcher,
			String words, int totalHits, ScoreDoc[] score, Set<String> mapDataTime, String[] head)
			throws Exception {
		StringBuilder sb = null;
		if (logger.isDebugEnabled()) {
			sb = new StringBuilder();
			sb.append("query:");
			sb.append(words);
			sb.append(" hit ");
			sb.append(totalHits);
		}

		List<Object> list = new ArrayList<Object>();
		if (head != null && head.length > 0) {
			for (int i = 0; i < score.length; i++) {
				CaseInsensitiveHashMap<Object> map = 
						new CaseInsensitiveHashMap<Object>();
				Document doc = searcher.doc(score[i].doc);
				if (logger.isDebugEnabled()) {
					sb.append("\r");
					sb.append(i);
					sb.append(":");
					sb.append(doc.toString());
				}
				for (int n = 0; n < head.length; n++) {
					String keyField = head[n];
					Object v = doc.get(keyField);
					if (mapDataTime.contains(keyField)) {
						map.put(head[n], new Date((Long)v));
					} else {
						map.put(head[n], v);
					}
				}
				list.add(map);
			}
		} else {
			for (int i = 0; i < score.length; i++) {
				Document doc = searcher.doc(score[i].doc);
				if (logger.isDebugEnabled()) {
					sb.append("\r");
					sb.append(i);
					sb.append(":");
					sb.append(doc.toString());
				}
				Map<String, Object> map = new HashMap<String, Object>();
				List<IndexableField> listFld = doc.getFields();
				for (int j = 0; j < listFld.size(); j++) {
					String keyField = listFld.get(j).name();
					Object v = doc.get(keyField);
					if (mapDataTime.contains(keyField)) {
						map.put(keyField, new Date(ObjectComm.objectToLong(v)));						
					} else {
						map.put(keyField, v);
					}
				}
				list.add(map);
			}
		}
		ResultDTO dto = ResultDTO.success(list);
		dto.setLen(totalHits);
		if (logger.isDebugEnabled()) {
			sb.append("\r");
			sb.append("rst:");
			sb.append(dto.toString());
			logger.debug(sb.toString());
		}
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public static ResultDTO topDocsToDTO(IndexSearcher searcher, String words,
			TopDocs topDocs, Set<String> mapDataTime, String[] head) throws Exception {
		if (topDocs.totalHits == 0) {
			ResultDTO dto = ResultDTO.error("没有搜索到相关内容");
			// GsonEbRst rst = GsonEbUtils.toGsonEbRstSuccess("");
			if (logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("query:");
				sb.append(words);
				sb.append(" hit ");
				sb.append(topDocs.totalHits);
				sb.append("\r");
				sb.append("rst:");
				sb.append(dto.toString());
				logger.debug(sb.toString());
			}
			return dto;
		} else {
			ResultDTO rst = SearchUtils.scoreDocToDTO(searcher, words,
					topDocs.totalHits, topDocs.scoreDocs, mapDataTime, head);
			return rst;
		}
	}

    private static ScoreDoc getLastScoreDoc(Integer pageIdx, Integer pageSize, Query query,
            IndexSearcher searcher) throws Exception {
        if (pageIdx == 0) {
            return null;
        }
        int num = pageSize * pageIdx;
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num];
    }
    
	@SuppressWarnings({ "rawtypes" })
	public static ResultDTO search(IndexSearcher searcher, Query query,
			String words, Integer pageIdx, Integer pageSize, Sort sort, Set<String> mapDataTime, String[] head)
			throws Exception {
		/*
		 * TopScoreDocCollector topCollector = TopScoreDocCollector.create( 100,
		 * false); searcher.search(query, topCollector);
		 * System.out.println("命中：" + topCollector.getTotalHits()); // 查询当页的记录
		 * ScoreDoc[] docs = topCollector.topDocs((pageNO - 1) * pageSize,
		 * pageSize).scoreDocs;
		 */
		TopDocs topDocs = null;
		if (pageSize != null) {
			//topDocs = searcher.search(query, pageSize);
			ScoreDoc scoreDoc = null;
			getLastScoreDoc(pageIdx, pageSize, query, searcher);
			if (sort != null) {
				topDocs = searcher.searchAfter(scoreDoc, query, pageSize, sort);
			} else {
				topDocs = searcher.searchAfter(scoreDoc, query, pageSize);
			}
		} else {
			if (sort != null) {
				topDocs = searcher.search(query, Integer.MAX_VALUE, sort);
			} else {
				topDocs = searcher.search(query, Integer.MAX_VALUE);				
			}
		}
		return SearchUtils.topDocsToDTO(searcher, words, topDocs, mapDataTime, head);
	}
    
	@SuppressWarnings({ "rawtypes" })
	public static ResultDTO search(IndexSearcher searcher, Query query,
			String words, Integer pageIdx, Integer pageSize, Set<String> mapDataTime, String[] head)
			throws Exception {
		TopDocs topDocs = null;
		if (pageSize != null) {
			//topDocs = searcher.search(query, pageSize);
			ScoreDoc scoreDoc = getLastScoreDoc(pageIdx, pageSize, query, searcher);
			topDocs = searcher.searchAfter(scoreDoc, query, pageSize);
		} else {
			topDocs = searcher.search(query, Integer.MAX_VALUE);
		}
		return SearchUtils.topDocsToDTO(searcher, words, topDocs, mapDataTime, head);
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
			String field, String words, Integer pageIdx, Integer pageSize,
			Set<String> mapDataTime, String[] head) throws Exception {
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, field,
				analyzer);
		Query query = parser.parse(words);
		return search(searcher, query, words, pageIdx, pageSize, mapDataTime, head);
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
			String field, String words, Integer pageIdx, Integer pageSize, 
			Sort sort, Set<String> mapDataTime, String[] head) throws Exception {
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, field,
				analyzer);
		Query query = parser.parse(words);
		return search(searcher, query, words, pageIdx, pageSize, sort, mapDataTime, head);
	}

	@SuppressWarnings("rawtypes")
	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
			String words, Integer pageIdx, Integer pageSize, Set<String> mapDataTime, String[] head)
			throws Exception {
		return search(searcher, analyzer, null, words, pageIdx, pageSize, mapDataTime, head);
	}

	@SuppressWarnings("rawtypes")
	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
			String words, Integer pageIdx, Integer pageSize, Set<String> mapDataTime) throws Exception {
		return search(searcher, analyzer, null, words, pageIdx, pageSize, mapDataTime, null);
	}

	@SuppressWarnings("rawtypes")
	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
			String words, Integer pageIdx, Integer pageSize, Sort sort,
			Set<String> mapDataTime, String[] head)
			throws Exception {
		return search(searcher, analyzer, null, words, pageIdx, pageSize, sort, mapDataTime, head);
	}

	@SuppressWarnings("rawtypes")
	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
			String words, Integer pageIdx, Integer pageSize, Sort sort, Set<String> mapDataTime) throws Exception {
		return search(searcher, analyzer, null, words, pageIdx, pageSize, sort, mapDataTime, null);
	}

//	@SuppressWarnings("rawtypes")
//	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
//			String words, Integer pageSize) throws Exception {
//		return search(searcher, analyzer, words, null, pageSize);
//	}
//
//	@SuppressWarnings({ "rawtypes" })
//	public static ResultDTO searchTerm(IndexSearcher searcher,
//			Analyzer analyzer, String field, String words, String[] head,
//			Integer pageSize) throws Exception {
//		Term term = new Term(field, words);
//		Query query = new TermQuery(term);
//		return search(searcher, query, words, head, pageSize);
//	}
//
//	@SuppressWarnings({ "rawtypes" })
//	public static ResultDTO search(IndexSearcher searcher, Query query,
//			String words, Integer pageSize) throws Exception {
//		return search(searcher, query, words, (String[]) null, pageSize);
//	}
//
//	@SuppressWarnings({ "rawtypes" })
//	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
//			String field, String words, Integer pageSize) throws Exception {
//		return search(searcher, analyzer, field, words, null, pageSize);
//	}
//
//	@SuppressWarnings({ "rawtypes" })
//	public static ResultDTO search(IndexSearcher searcher, Analyzer analyzer,
//			String words, Integer pageSize) throws Exception {
//		return search(searcher, analyzer, null, words, null, pageSize);
//	}

}
