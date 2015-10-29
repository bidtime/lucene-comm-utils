package com.bidtime.lucene.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.bidtime.dbutils.gson.JSONHelper;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.utils.basic.ObjectComm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bidtime.lucene.base.utils.FieldHeadMagnt;

public class LuceneCreate {
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneCreate.class);

	Directory indexDir;
	
	IndexWriter indexWriter;
	Analyzer analyzer;
	IndexWriterConfig iwConfig;
	Boolean openMode;
	FieldHeadMagnt headMngt;

	public LuceneCreate(Directory dir, Analyzer analyzer, Boolean openMode) {
		this.indexDir = dir;
		this.analyzer = analyzer;
		this.openMode = openMode;
		this.headMngt = new FieldHeadMagnt();
	}
	
	public void initial(String filePath, Integer marginLines) throws Exception {
		setIndexPath(filePath, marginLines);
	}
	
	private static OpenMode getOpenMode(Boolean s) {
		OpenMode om = null;
		if (s) {
			om = OpenMode.CREATE;
		} else {
			om = OpenMode.CREATE_OR_APPEND;
		}
		return om;
	}
	
	@SuppressWarnings("deprecation")
	private void initConfig(PerFieldAnalyzerWrapper wrapper) throws IOException {
		//配置IndexWriterConfig
		if ( wrapper == null ) {
			iwConfig = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
		} else {
			iwConfig = new IndexWriterConfig(Version.LUCENE_CURRENT, wrapper);			
		}
		iwConfig.setOpenMode(getOpenMode(openMode));
		//setMaxBufferedDocs
		//iwConfig.setMaxBufferedDocs(-1);
		indexWriter = new IndexWriter(indexDir, iwConfig);
		//auto unlock
		//IndexWriter.unlock(indexDir);
	}
	
	private boolean leftEqualIgnoreCase(String s1, String s2) {
		s1 = s1.trim();
		s2 = s2.trim();
		if (s1.length() == s2.length()) {
			return StringUtils.equalsIgnoreCase(s1, s2);
		} else if (s1.length() > s2.length()) {
			int n = s1.indexOf(s2);
			return n == 1 ? true : false;
		} else {
			int n = s2.indexOf(s1);
			return n == 1 ? true : false;
		}
	}

	private void setIndexPath(String filePath, Integer marginLines)
			throws Exception {
		if (!new File(filePath).exists()) {
			throw new Exception("file not found." + filePath);
		}
		Long startTime = System.currentTimeMillis();
		//indexWriter.(Integer.MAX_VALUE);
		//int n = IndexWriter.MAX_TERM_LENGTH;
		String[] arHeads = null;
		String[] arDataTypes = null;
		String[] arIndexs = null;
		String[] arTokenized = null;
		String[] arStoreTypes = null;
		String[] arPks = null;
		PerFieldAnalyzerWrapper wrapper = null;

		logger.info("文件:" + filePath +"read index format file...");
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
		try {
			long n = 0;
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				if (leftEqualIgnoreCase(str, "#")) {
					continue;
				}
				if (marginLines>0 && n==0) {
					arHeads = str.split("\t");
				} else if (marginLines>1 && n==1) {
					arDataTypes = str.split("\t");
				} else if (marginLines>2 && n==2) {
					arStoreTypes = str.split("\t");
				} else if (marginLines>3 && n==3) {
					arIndexs = str.split("\t");
				} else if (marginLines>4 && n==4) {
					arTokenized = str.split("\t");
				} else if (marginLines>5 && n==5) {
					arPks = str.split("\t");
				}
				n++;
				if (n % 5000 == 0) {
					logger.info("reading lines: " + n);
				}
			}
			headMngt.setProps(arHeads, arDataTypes,
					arIndexs, arTokenized, arStoreTypes, arPks);
			wrapper = headMngt.getPinYinAnalyzer(analyzer);
			initConfig(wrapper);
			logger.info("readlines: " + n);
			logger.info(getFmtNow(startTime) + " ms read index format file.");
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void createIndexMap(Map map)
			throws Exception {
		createIndexMap(map, true);
	}
	
	@SuppressWarnings("rawtypes")
	private void createIndexMap(Map map, boolean commit)
			throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("map:" + "create index...");
		Document doc = headMngt.newRows(map);
		if (doc != null) {
			indexWriter.addDocument(doc);
			//indexWriter.optimize(); //优化
			if (commit) {
				indexCommit();
			}
		}
		logger.debug(getFmtNow(startTime) + " ms create index.");
	}

	@SuppressWarnings("rawtypes")
	private void createIndexMap(List<Map> list) throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("list:" + "create index...");
		for (int i=0; i<list.size(); i++) {
			createIndexMap(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		logger.debug(getFmtNow(startTime) + " ms create index.");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createIndex(Object d) throws Exception {
		if (d == null) {
			return;
		}
		if (d instanceof Map) {
			createIndexMap((Map)d);
		} else if (d instanceof List) {
			if (!((List) d).isEmpty()) {
				return; 
			}
			Object o = ((List) d).get(0);
			boolean bMap = (o != null && o instanceof Map) ? true : false;
			if (bMap) {
				createIndexMap(((List<Map>)d));
			} else {
				createIndex((List)d);
			}
		} else {
			createIndex(d, true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void createIndex(Object o, boolean commit)
			throws Exception {
		Map map = null;
		if (o instanceof ResultDTO) {
			map = JSONHelper.clazzToMap(((ResultDTO)o).dataToMap());
		} else {
			map = JSONHelper.clazzToMap(o);			
		}
		updateIndexMap(map, true);
	}
	
	private void createIndex(List<?> list) throws Exception {
		for (int i=0; i<list.size(); i++) {
			createIndex(list.get(i),
					(i == list.size() - 1 ) ? true : false);
		}
	}

//	public void createIndexPath(String filePath, Integer marginLines)
//			throws Exception {
//		Long startTime = System.currentTimeMillis();
//		//indexWriter.(Integer.MAX_VALUE);
//		//int n = IndexWriter.MAX_TERM_LENGTH;
//		String[] arHeads = null;
//		String[] arDataTypes = null;
//		String[] arIndexs = null;
//		String[] arTokenized = null;
//		String[] arStoreTypes = null;
//		PerFieldAnalyzerWrapper wrapper = null;
//		FieldHeadMagnt headMngt = null;
//		
//		logger.info("文件:" + filePath + "加入索引中...");
//		BufferedReader bufferedReader = new BufferedReader(
//				new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
//		try {
//			long n = 0;
//			String str = null;
//			while ((str = bufferedReader.readLine()) != null) {
//				if (leftEqualIgnoreCase(str, "#")) {
//					continue;
//				}
//				if (marginLines>0 && n==0) {
//					arHeads = str.split("\t");
//				} else if (marginLines>1 && n==1) {
//					arDataTypes = str.split("\t");
//				} else if (marginLines>2 && n==2) {
//					arStoreTypes = str.split("\t");
//				} else if (marginLines>3 && n==3) {
//					arIndexs = str.split("\t");
//				} else if (marginLines>4 && n==4) {
//					arTokenized = str.split("\t");
//				} else {
//					if (headMngt == null) {
//						headMngt = new FieldHeadMagnt(arHeads, arDataTypes,
//							arIndexs, arTokenized, arStoreTypes);
//						wrapper = headMngt.getPinYinAnalyzer(analyzer);
//						initConfig(wrapper);
//					}
//					String[] arConts = str.split("\t");
//					Document doc = headMngt.newRows(arHeads, arConts);
//					indexWriter.addDocument(doc);
//					//indexWriter.optimize(); //优化
//				}
//				n++;
//				if (n % 5000 == 0) {
//					logger.info("reading lines: " + n);
//					//indexCommit();
//				}
//			}
//			indexCommit();
//			//indexWriter.close();
//			logger.info("readlines: " + n);
//			logger.info(getFmtNow(startTime) + " ms create index files.");
//		} finally {
//			if (bufferedReader != null) {
//				bufferedReader.close();
//			}
//		}
//	}
	
	private String getFmtNow(Long startTime) {
		Long endTime = System.currentTimeMillis();
		return "span: " + (endTime - startTime);
	}

	private void indexCommit() throws IOException {
		indexWriter.prepareCommit();
		indexWriter.commit();
	}
	
	public Sort getSortOfField(String fld, boolean reverse) throws Exception {
		return headMngt.getSortOfField(fld, reverse);
	}
	
	public Set<String> getMapDateTime() {
		return this.headMngt.getMapDataTime();
	}
	
	private Term getTermOfMap(Object val) throws Exception {
		if (val == null) {
			throw new Exception("get term error: pk value is not null.");
		}
		Set<String> setPk = this.headMngt.getMapPk();
		if (setPk == null || setPk.isEmpty()) {
			throw new Exception("get term error: field not set pk.");
		} else if (setPk.size() >= 2) {
			throw new Exception("get term error: field pk large 2 - " + setPk.size());			
		}
		String pk = setPk.iterator().next();
		return new Term(pk, ObjectComm.objectToString(val));
	}
	
	@SuppressWarnings("rawtypes")
	private Term getTermOfMap(Map map) throws Exception {
		Set<String> setPk = this.headMngt.getMapPk();
		if (setPk == null || setPk.isEmpty()) {
			throw new Exception("get term error: field not set pk.");
		} else if (setPk.size() >= 2) {
			throw new Exception("get term error: field pk large 2 - " + setPk.size());			
		}
		String pk = setPk.iterator().next();
		Object val = map.get(pk);
		if (val == null) {
			throw new Exception("get term error: pk value is not null.");
		}
		return new Term(pk, ObjectComm.objectToString(val));
	}
	
	@SuppressWarnings("rawtypes")
	private void updateIndexMap(Map map) throws Exception {
		updateIndexMap(map, true);
	}
	
	@SuppressWarnings("rawtypes")
	private void updateIndexMap(Map map, boolean commit)
			throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("map:" + "update index...");
		Term term = getTermOfMap(map);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		Document doc = headMngt.newRows(map);
		if (doc != null) {
			indexWriter.updateDocument(term, doc);
			//indexWriter.optimize(); //优化
			if (commit) {
				indexCommit();
			}
		}
		logger.debug(getFmtNow(startTime) + " ms update index.");
	}
	
	@SuppressWarnings("rawtypes")
	private void updateIndexMap(List<Map> list) throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("list:" + "update index...");
		for (int i=0; i<list.size(); i++) {
			updateIndexMap(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		logger.debug(getFmtNow(startTime) + " ms update index.");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateIndex(Object d) throws Exception {
		if (d == null) {
			return;
		}
		if (d instanceof Map) {
			updateIndexMap((Map)d);
		} else if (d instanceof List) {
			if (!((List) d).isEmpty()) {
				return; 
			}
			Object o = ((List) d).get(0);
			boolean bMap = (o != null && o instanceof Map) ? true : false;
			if (bMap) {
				updateIndexMap(((List<Map>)d));
			} else {
				updateIndex((List)d);
			}
		} else {
			updateIndex(d, true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void updateIndex(Object o, boolean commit)
			throws Exception {
		Map map = null;
		if (o instanceof ResultDTO) {
			map = JSONHelper.clazzToMap(((ResultDTO)o).dataToMap());
		} else {
			map = JSONHelper.clazzToMap(o);			
		}
		updateIndexMap(map, true);
	}
	
	private void updateIndex(List<?> list) throws Exception {
		for (int i=0; i<list.size(); i++) {
			updateIndex(list.get(i),
					(i == list.size() - 1 ) ? true : false);
		}
	}
	
	private void updateNumericDocValue(Term term, Object fld,
			Object val,	boolean commit)	throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("map:" + "update index...");
		String head = null;
		if (fld instanceof String) {
			head = (String)fld;
		} else {
			head = String.valueOf(fld);
		}
		indexWriter.updateNumericDocValue(term, 
				ObjectComm.objectToString(head),
				ObjectComm.objectToLong(val));
		//indexWriter.optimize(); //优化
		if (commit) {
			indexCommit();
		}
		logger.debug(getFmtNow(startTime) + " ms update index.");
	}
	
	public void updateNumericDocValue(Object pkVal, Object fld,
			Object val) throws Exception {
		Term term = getTermOfMap(pkVal);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		updateNumericDocValue(term, fld, val, true);
	}
	
	@SuppressWarnings("rawtypes")
	private void updateNumericDocValue(Map map) throws Exception {
		updateNumericDocValue(map, true);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateNumericDocValue(Map map, boolean commit)
			throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("map:" + "update index...");
		Term term = getTermOfMap(map);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		
		Iterator<Map.Entry> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
		//for (Entry<String, ?> entry: map.entrySet()) {
			Map.Entry entry = entries.next();
			if (!this.headMngt.getMapPk().contains(entry.getKey())) {
				updateNumericDocValue(term, entry.getKey(),
					entry.getValue(), false);
			}
		}
		if (commit) {
			indexCommit();
		}
		logger.debug(getFmtNow(startTime) + " ms update index.");
	}

	@SuppressWarnings({ "rawtypes" })
	private void updateNumericDocValue(List<Map> list) throws Exception {
		Long startTime = System.currentTimeMillis();
		logger.debug("list:" + "update index...");
		for (int i=0; i<list.size(); i++) {
			updateNumericDocValue(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		logger.debug(getFmtNow(startTime) + " ms update index.");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateNumericDocValue(Object d) throws Exception {
		if (d == null) {
			return;
		}
		if (d instanceof Map) {
			updateNumericDocValue((Map)d);
		} else if (d instanceof List) {
			if (!((List) d).isEmpty()) {
				return; 
			}
			Object o = ((List) d).get(0);
			boolean bMap = (o != null && o instanceof Map) ? true : false;
			if (bMap) {
				updateNumericDocValue(((List<Map>)d));
			} else {
				//updateIndexBean((List)d);
			}
		} else {
			//updateNumericDocValue(d, true);
		}
	}
	
	public void deleteIndex(Object pkVal, boolean commit, boolean force) throws Exception {
		Term term = getTermOfMap(pkVal);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		indexWriter.deleteDocuments(term);
		if ( commit ) {
			indexWriter.commit();
		}
		if ( force ) {
			indexWriter.forceMergeDeletes();
		}
	}
	
	public void deleteIndex(Object pkVal, boolean force) throws Exception {
		deleteIndex(pkVal, true, force);
	}
	
	public void deleteIndex(Object pkVal) throws Exception {
		deleteIndex(pkVal, true, true);
	}
	
	public void deleteIndex(Object[] pkVal) throws Exception {
		for (int i = 0; i < pkVal.length; i++) {
			boolean commit = (i == pkVal.length - 1 ) ? true : false;
			boolean force = commit;
			deleteIndex(pkVal[i], commit, force);
		}
	}
	
//	public Sort getSortOfField(String[] fld) throws Exception {
//		SortField[] fields = new SortField[fld.length];
//		for (int i=0; i<fld.length; i++) {
//			FieldHeadProp o = headMngt.get(fld[i]);
//			//fields[i] = 
//		}
//		Sort sort = new Sort(new SortField[] { new SortField( "date", Type.INT, true ),
//				new SortField("ename", Type.STRING, false ) } );
//		return sort;
//	}
}
