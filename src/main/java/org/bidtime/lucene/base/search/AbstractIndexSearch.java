package org.bidtime.lucene.base.search;

import java.nio.file.Paths;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.utils.LogTimeUtil;
import org.bidtime.lucene.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

public abstract class AbstractIndexSearch {
	
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractIndexSearch.class);

	protected Analyzer analyzer;
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	protected Directory indexDir;

	protected IndexReader reader;
	
	protected void setProp(Analyzer analyzer,
			Directory idxDir) throws Exception {
		this.indexDir = idxDir;
		this.analyzer = analyzer;
		try {
			this.reader = DirectoryReader.open(indexDir);
		//} catch (IndexNotFoundException e) {
		} catch (Exception e) {
			logger.error("setProp", e);
		}
	}
	
	protected void setProp(Analyzer analyzer, String idxPath) throws Exception {
		setProp(analyzer, FSDirectory.open(Paths.get(idxPath)));
	}

	public AbstractIndexSearch(Analyzer analyzer, String idxPath) throws Exception {
		setProp(analyzer, FSDirectory.open(Paths.get(idxPath)));
	}

	public AbstractIndexSearch(Analyzer analyzer, Directory indexDir) throws Exception {
		setProp(analyzer, indexDir);
	}

	public AbstractIndexSearch(String idxPath) throws Exception {
		setProp(new IKAnalyzer4PinYin(false),
				FSDirectory.open(Paths.get(idxPath)));
	}

	public AbstractIndexSearch(Directory indexDir) throws Exception {
		this(new IKAnalyzer4PinYin(false), indexDir);
	}
	
	public abstract IndexSearcher getSearch() throws Exception;
	
	protected abstract void closeSearch(IndexSearcher srh) throws Exception;

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String[] head) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Set<String> setDateTime = null;		//headMagt.getMapDateTime();
		ResultDTO dto = searchIt(words, pageIdx, pageSize, setDateTime, head);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("search:" + words + ", span ", start));
		}		
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort, String[] head) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Set<String> setDateTime = null;	//headMagt.getMapDateTime();
		ResultDTO dto = searchIt(words, pageIdx, pageSize, sort, setDateTime, head);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("search:" + words + ", span ", start));
		}		
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Set<String> setDateTime = null;	//headMagt.getMapDateTime();
		ResultDTO dto = searchIt(words, pageIdx, pageSize, sort, setDateTime);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("search:" + words + ", span ", start));
		}		
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String fldSort, String[] head) throws Exception {
		Sort sort = null;		//=headMagt.getSortOfField(fldSort);
		return search(words, pageIdx, pageSize, sort, head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String fldSort) throws Exception {
		Sort sort = null;		//=headMagt.getSortOfField(fldSort);
		return search(words, pageIdx, pageSize, sort);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize)
			throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Set<String> setDateTime = null;		//headMagt.getMapDateTime();
		ResultDTO dto = searchIt(words, pageIdx, pageSize, setDateTime);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("search:" + words + ", span ", start));
		}		
		return dto;
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String sortFld, boolean reverse) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Sort sort = null;		//headMagt.getSortOfField(sortFld, reverse);
		Set<String> setDateTime = null;		//headMagt.getMapDateTime();
		ResultDTO dto = searchIt(words, pageIdx, pageSize, sort, setDateTime);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("search:" + words + ", span ", start));
		}		
		return dto;
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Set<String> mapDataTime, String[] head) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		IndexSearcher is = this.getSearch();
		try {
			return SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
					mapDataTime, head);
		} finally {
			this.closeSearch(is);
			if (logger.isDebugEnabled()) {
				logger.debug(LogTimeUtil.getFmtDiffNowMs("searchIt:" + words + ", span ", start));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Sort sort, Set<String> mapDataTime, String[] head) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		IndexSearcher is = this.getSearch();
		try {
			return SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
					sort, mapDataTime, head);
		} finally {
			this.closeSearch(is);
			if (logger.isDebugEnabled()) {
				logger.debug(LogTimeUtil.getFmtDiffNowMs("searchIt:" + words + ", span ", start));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Sort sort, Set<String> mapDataTime) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		IndexSearcher is = this.getSearch();
		try {
			return SearchUtils.search(is, analyzer, words, pageIdx, pageSize,
					sort, mapDataTime);
		} finally {
			this.closeSearch(is);
			if (logger.isDebugEnabled()) {
				logger.debug(LogTimeUtil.getFmtDiffNowMs("searchIt:" + words + ", span ", start));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected ResultDTO searchIt(String words, Integer pageIdx, Integer pageSize,
			Set<String> mapDataTime) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		IndexSearcher is = this.getSearch();
		try {
			return SearchUtils.search(is, analyzer, words, pageIdx, pageSize, mapDataTime);
		} finally {
			this.closeSearch(is);
			if (logger.isDebugEnabled()) {
				logger.debug(LogTimeUtil.getFmtDiffNowMs("searchIt:" + words + ", span ", start));
			}
		}
	}

}
