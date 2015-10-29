package com.bidtime.lucene.base;

import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.bidtime.dbutils.gson.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneIndexRoot {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneIndexRoot.class);

	//Directory
	protected Analyzer analyzer;

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	protected Directory indexDir;
	protected String fileSource;
	protected String extName;
	public String getExtName() {
		return extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}

	protected Integer marginLines = 6;
	protected Boolean openMode = false;
	
	public Boolean getOpenMode() {
		return openMode;
	}

	public void setOpenMode(Boolean openMode) {
		this.openMode = openMode;
	}

	public Integer getMarginLines() {
		return marginLines;
	}

	public void setMarginLines(Integer marginLines) {
		this.marginLines = marginLines;
	}

	protected LuceneCreate indexCreate;
	protected LuceneSearch indexSearch;

	public LuceneCreate getIndexCreate() {
		return indexCreate;
	}

	public void setIndexCreate(LuceneCreate indexCreate) {
		this.indexCreate = indexCreate;
	}

	public LuceneSearch getIndexSearch() {
		return indexSearch;
	}

	public void setIndexSearch(LuceneSearch indexSearch) {
		this.indexSearch = indexSearch;
	}
	
	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}
	
	public LuceneIndexRoot() {
	}
	
	protected void initialIndexDir() throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public void initial() throws Exception {
		initialIndexDir();
		if (analyzer == null) {
			analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		}
		indexCreate = new LuceneCreate(indexDir, analyzer, openMode);
		//
		indexCreate.initial(fileSource, marginLines);
		indexSearch = new LuceneSearch(indexDir, analyzer);
		indexSearch.initial();
	}
	
//	private boolean isHitExtName(String s) {
//		if (StringUtils.isEmpty(this.extName)) {
//			return true;
//		} else {
//			return s.endsWith(this.extName);
//		}
//	}
	
	protected void doFileNotExists(String s) throws Exception {
		logger.info("createDirsIndex: " 
				+ s + " file not exists." );
	}
	
	public void createIndex(Object d) throws Exception {
		indexCreate.createIndex(d);
	}
	
	public void updateIndex(Object d) throws Exception {
		indexCreate.updateIndex(d);
	}
	
	public void updateNumericDocValue(Object pkVal, Object fld,
			Object val) throws Exception {
		indexCreate.updateNumericDocValue(pkVal, fld, val);
	}
	
	public void updateNumericDocValue(Object o) throws Exception {
		indexCreate.updateNumericDocValue(o);
	}
	
	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String[] head) throws Exception {
		Set<String> setDateTime = indexCreate.getMapDateTime();
		return indexSearch.search(words, pageIdx, pageSize,
				setDateTime, head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort, String[] head) throws Exception {
		Set<String> setDateTime = indexCreate.getMapDateTime();
		return indexSearch.search(words, pageIdx, pageSize,
				sort, setDateTime, head);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			Sort sort) throws Exception {
		Set<String> setDateTime = indexCreate.getMapDateTime();
		return indexSearch.search(words, pageIdx, pageSize,
				sort, setDateTime);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize)
			throws Exception {
		Set<String> setDateTime = indexCreate.getMapDateTime();
		return indexSearch.search(words, pageIdx, pageSize, setDateTime);
	}

	@SuppressWarnings("rawtypes")
	public ResultDTO search(String words, Integer pageIdx, Integer pageSize,
			String sortFld, boolean reverse) throws Exception {
		Sort sort = indexCreate.getSortOfField(sortFld, reverse);
		Set<String> setDateTime = indexCreate.getMapDateTime();
		return indexSearch.search(words, pageIdx, pageSize,	sort, 
				setDateTime);
	}
	
	public void deleteIndex(Object pkVal) throws Exception {
		indexCreate.deleteIndex(pkVal);
	}
	
	public void deleteIndex(Object[] pkVal) throws Exception {
		indexCreate.deleteIndex(pkVal);
	}
	
//	protected void createDirsIndex(String s) throws Exception {
//		File file = new File(s);
//		if (!file.exists()) {
//			doFileNotExists(s);
//		} if (file.isDirectory()) {
//			List<String> fileList = new ArrayList<String>();
//			FileCommon.listFile(new File(s), fileList);
//			for (String filePath : fileList) {
//				logger.info("file:"+filePath);
//				if (isHitExtName(filePath)) {
//					indexCreate.createIndexPath(filePath, marginLines);
//				}
//			}
//		} else {
//			if (isHitExtName(s)) {
//				indexCreate.createIndexPath(s, marginLines);
//			}
//		}
//	}

}
