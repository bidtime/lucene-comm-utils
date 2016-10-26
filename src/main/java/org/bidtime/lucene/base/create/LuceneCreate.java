package org.bidtime.lucene.base.create;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bidtime.lucene.base.utils.FieldsMagnt;
import org.bidtime.lucene.utils.LogTimeUtil;
import org.bidtime.utils.basic.ObjectComm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

public class LuceneCreate {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneCreate.class);
	
	IndexWriter indexWriter;
	Analyzer analyzer;
	IndexWriterConfig iwConfig;
	Boolean openMode;
	FieldsMagnt headMagt;

//	public LuceneCreate(String sourceFile, Analyzer analyzer, 
//			Directory dir, Boolean openMode) throws Exception {
//		this(new FieldsMagnt(sourceFile), analyzer, dir, openMode);
//	}
//
//	public LuceneCreate(String sourceFile, Analyzer analyzer, 
//			String idxPath, Boolean openMode) throws Exception {
//		this(new FieldsMagnt(sourceFile), analyzer, 
//			FSDirectory.open(new File(idxPath)), openMode);
//	}
//
//	public LuceneCreate(String sourceFile, Analyzer analyzer, 
//			String idxPath) throws Exception {
//		setProp(new FieldsMagnt(sourceFile), analyzer, 
//			FSDirectory.open(new File(idxPath)), false);
//	}

	public LuceneCreate(FieldsMagnt headMagt, Analyzer analyzer, 
			String idxPath) throws Exception {
		setProp(headMagt, analyzer, FSDirectory.open(Paths.get(idxPath)), false);
	}

	public LuceneCreate(String sourceFile, String idxPath) throws Exception {
		setProp(new FieldsMagnt(sourceFile), 
			new IKAnalyzer4PinYin(false), 
				FSDirectory.open(Paths.get(idxPath)), false);
	}

	public LuceneCreate(FieldsMagnt headMagt, String idxPath) throws Exception {
		setProp(headMagt, 
			new IKAnalyzer4PinYin(false), 
				FSDirectory.open(Paths.get(idxPath)), false);
	}

	public LuceneCreate(FieldsMagnt headMagt, Analyzer analyzer, 
			Directory dir, Boolean openMode) throws Exception {
		setProp(headMagt, analyzer, dir, openMode);
	}
	
	protected void setProp(FieldsMagnt headMagt, Analyzer analyzer, 
			Directory dir, Boolean openMode) throws Exception {
		this.analyzer = analyzer;
		this.openMode = openMode;
		this.headMagt = headMagt;
		initConfig(dir);
	}
	
	protected void setProp(FieldsMagnt headMagt, Analyzer analyzer,
			String idxPath, Boolean openMode) throws Exception {
		setProp(headMagt, analyzer, idxPath, openMode);
	}

	public LuceneCreate(FieldsMagnt headMagt, Analyzer analyzer, 
			Directory dir) throws Exception {
		this(headMagt, analyzer, dir, false);
	}

//	public LuceneCreate(FieldsMagnt headMagt, Analyzer analyzer) throws Exception {
//		this(headMagt, analyzer, 
//				new RAMDirectory(), false);
//	}
//	
//	public LuceneCreate(FieldsMagnt headMagt, 
//			Analyzer analyzer, String dir) throws Exception {
//		this(headMagt, analyzer, 
//				FSDirectory.open(new File(dir)), false);
//	}
	
	@SuppressWarnings("deprecation")
	private void initConfig(Directory dir) throws Exception {
		if (IndexWriter.isLocked(dir)) {
			//IndexWriter.unlock(dir);		//auto unlock
			logger.warn("{} is locked", dir.toString());
		}
		PerFieldAnalyzerWrapper wrapper =
				headMagt.getPinYinAnalyzer(analyzer);
		//配置IndexWriterConfig
		iwConfig = new IndexWriterConfig(wrapper != null ? wrapper : analyzer);
		iwConfig.setOpenMode(openMode ? 
				OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
		//setMaxBufferedDocs
		//iwConfig.setMaxBufferedDocs(-1);
		indexWriter = new IndexWriter(dir, iwConfig);
		initIndexIt(dir);		//init index dir
	}
	
	private void initIndexIt(Directory dir) throws Exception {
		try {
			DirectoryReader.open(dir);
		} catch (IndexNotFoundException e) {
			logger.error("IndexNotFoundException:" + dir.toString());
			indexWriter.commit();
		}
	}

	@SuppressWarnings("rawtypes")
	private void createIndexMap(Map map) throws Exception {
		createIndexMap(map, true);
	}
	
	@SuppressWarnings("rawtypes")
	private void createIndexMap(Map map, boolean commit)
			throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Document doc = headMagt.newRows(map);
		if (doc != null) {
			indexWriter.addDocument(doc);
			//indexWriter.optimize(); //优化
			if (commit) {
				indexCommit();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("createIndexMap:" + map + ", span ", start));
		}
	}

	@SuppressWarnings("rawtypes")
	private void createIndexMap(List<Map> list) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		for (int i=0; i<list.size(); i++) {
			createIndexMap(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("createIndexMap:" + list + ", span ", start));
		}		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createIndex(Object d) throws Exception {
		if (d == null) {
			return;
		}
		if (d instanceof Map) {
			createIndexMap((Map)d);
		} else if (d instanceof List) {
			if (((List) d).isEmpty()) {
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
//		if (o instanceof ResultDTO) {
//			map = JSONHelper.clazzToMap(((ResultDTO)o).dataToMap());
//		} else {
//			map = JSONHelper.clazzToMap(o);			
//		}
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

	public void indexCommit() throws IOException {
		indexWriter.prepareCommit();
		indexWriter.commit();
	}
	
	@SuppressWarnings("rawtypes")
	public void updateIndexMap(Map map) throws Exception {
		updateIndexMap(map, true);
	}
	
	@SuppressWarnings("rawtypes")
	public void updateIndexMap(Map map, boolean commit)
			throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Term term = headMagt.getTermOfMap(map);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		Document doc = headMagt.newRows(map);
		if (doc != null) {
			indexWriter.updateDocument(term, doc);
			//indexWriter.optimize(); //优化
			if (commit) {
				indexCommit();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateIndexMap:" + map + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void updateIndexMap(List<Map> list) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		for (int i=0; i<list.size(); i++) {
			updateIndexMap(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateIndexMap:" + list + ", span ", start));
		}		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateIndex(Object d) throws Exception {
		if (d == null) {
			return;
		}
		if (d instanceof Map) {
			updateIndexMap((Map)d);
		} else if (d instanceof List) {
			if ( ((List) d).isEmpty() ) {
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
//		if (o instanceof ResultDTO) {
//			map = JSONHelper.clazzToMap(((ResultDTO)o).dataToMap());
//		} else {
//			map = GsonEbUtils.clazzToMap(o, PropAdapt.NOTNULL);
//		}
		updateIndexMap(map, true);
	}
	
	public void updateIndex(List<?> list) throws Exception {
		for (int i=0; i<list.size(); i++) {
			updateIndex(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
	}
	
	public void updateNumericDocValue(Term term, Object fld,
			Object val,	boolean commit)	throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
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
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateNumericDocValue:" + term + ", span ", start));
		}		
	}
	
	public void updateNumericDocValue(Object pkVal, Object fld,
			Object val) throws Exception {
		Term term = headMagt.getTermOfValue(pkVal);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		updateNumericDocValue(term, fld, val, true);
	}
	
	@SuppressWarnings("rawtypes")
	public void updateNumericDocValue(Map map) throws Exception {
		updateNumericDocValue(map, true);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateNumericDocValue(Map map, boolean commit)
			throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Term term = headMagt.getTermOfMap(map);
		if (term == null) {
			throw new Exception("get term error: term is not null.");
		}
		
		Iterator<Map.Entry> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
		//for (Entry<String, ?> entry: map.entrySet()) {
			Map.Entry entry = entries.next();
			if (!this.headMagt.getMapPk().contains(entry.getKey())) {
				updateNumericDocValue(term, entry.getKey(),
					entry.getValue(), false);
			}
		}
		if (commit) {
			indexCommit();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateNumericDocValue:" + term + ", span ", start));
		}		
	}

	@SuppressWarnings({ "rawtypes" })
	public void updateNumericDocValue(List<Map> list) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		for (int i=0; i<list.size(); i++) {
			updateNumericDocValue(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateNumericDocValue:" + list + ", span ", start));
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateNumericDocValue(Object d) throws Exception {
		if (d == null) {
			return;
		}
		if (d instanceof Map) {
			updateNumericDocValue((Map)d);
		} else if (d instanceof List) {
			if ( ((List) d).isEmpty() ) {
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
		Term term = headMagt.getTermOfValue(pkVal);
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
	
	public void deleteIndex(Long pkVal) throws Exception {
		deleteIndex(pkVal, true, true);
	}
	
	public void deleteIndex(Object pkVal, boolean force) throws Exception {
		deleteIndex(pkVal, true, force);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteIndex(Object pkVal) throws Exception {
		if (pkVal instanceof List) {
			deleteIndex((List)pkVal);
		} else {
			deleteIndex(pkVal, true, true);
		}
	}
	
	public void deleteIndex(Object[] pkVal) throws Exception {
		for (int i = 0; i < pkVal.length; i++) {
			boolean commit = (i == pkVal.length - 1 ) ? true : false;
			boolean force = commit;
			deleteIndex(pkVal[i], commit, force);
		}
	}
	
	public void deleteIndex(List<Object> listPk) throws Exception {
		if (listPk != null && !listPk.isEmpty()) {
			deleteIndex(listPk.toArray());
		}
	}
	
	public void closeIndex() throws Exception {
		this.indexWriter.close();
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
