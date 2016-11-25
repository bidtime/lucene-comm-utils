package org.bidtime.lucene.base.create;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.bidtime.lucene.ldbc.sql.xml.parser.EnumWord;
import org.bidtime.lucene.utils.LogTimeUtil;
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
	
	Directory dir;

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

	public LuceneCreate(Analyzer analyzer, String idxPath) throws Exception {
		setProp(analyzer, FSDirectory.open(Paths.get(idxPath)), false);
	}

	public LuceneCreate(Analyzer analyzer, String idxPath, Boolean openMode) throws Exception {
		setProp(analyzer, FSDirectory.open(Paths.get(idxPath)), openMode);
	}

//	public LuceneCreate(String idxPath) throws Exception {
//		setProp(new IKAnalyzer4PinYin(false), 
//				FSDirectory.open(Paths.get(idxPath)), false);
//	}
//
//	public LuceneCreate(Analyzer analyzer, 
//			Directory dir, Boolean openMode) throws Exception {
//		setProp(analyzer, dir, openMode);
//	}

	public LuceneCreate(Analyzer analyzer) throws Exception {
		setProp(analyzer, new RAMDirectory(), false);
	}

	public LuceneCreate(Analyzer analyzer, Directory dir) throws Exception {
		setProp(analyzer, dir, false);
	}
	
	protected void setProp(Analyzer analyzer, 
			Directory dir, Boolean openMode) throws Exception {
		this.analyzer = analyzer;
		this.dir = dir;
		this.openMode = openMode;
	}

	public PerFieldAnalyzerWrapper getPinYinAnalyzer(Map<String, EnumWord> mapEnumWord) throws Exception {
		Map<String, Analyzer> analyzerMap = null;
		for (Entry<String, EnumWord> entry : mapEnumWord.entrySet()) {
			String head = entry.getKey();
			EnumWord p = entry.getValue();
			if (p == EnumWord.PINYIN) {					//IKAnalyzer4PinYin.PINYIN:					//1
				if (analyzerMap == null) {
					analyzerMap = new HashMap<>();
				}
				analyzerMap.put(head, new IKAnalyzer4PinYin(false,
						IKAnalyzer4PinYin.PINYIN));
			} else if (p == EnumWord.SHOUZIMU) {		//IKAnalyzer4PinYin.PINYIN_SHOUZIMU:		//2
				if (analyzerMap == null) {
					analyzerMap = new HashMap<>();
				}
				analyzerMap.put(head, new IKAnalyzer4PinYin(false,
						IKAnalyzer4PinYin.PINYIN_SHOUZIMU));
			}
		}
		if (analyzerMap != null && !analyzerMap.isEmpty()) {
			return new PerFieldAnalyzerWrapper(
					new IKAnalyzer4PinYin(false), analyzerMap);
		} else {
			return null;
		}
	}
	
	public void initConfig(Map<String, EnumWord> mapEnumWord) throws Exception {
		initConfig(this.dir, mapEnumWord);
	}
	
	@SuppressWarnings("deprecation")
	private void initConfig(Directory dir, Map<String, EnumWord> mapEnumWord) throws Exception {
		if (iwConfig != null) {
			return;
		}
		if (IndexWriter.isLocked(dir)) {
			//IndexWriter.unlock(dir);		//auto unlock
			logger.warn("{} is locked", dir.toString());
		}
		
		//Map<String, EnumWord> mapEnumWord = tp.getHeadWordType();
		PerFieldAnalyzerWrapper wrapper = getPinYinAnalyzer(mapEnumWord);
		//配置IndexWriterConfig
		iwConfig = new IndexWriterConfig(wrapper != null ? wrapper : analyzer);
		iwConfig.setOpenMode(openMode ? 
				OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
		//setMaxBufferedDocs
		//iwConfig.setMaxBufferedDocs(-1);
		indexWriter = new IndexWriter(dir, iwConfig);
		//init index dir
		//DirectoryReader.open(dir);
	}

	public void insert(Document doc) throws Exception {
		insert(doc, true);
	}
	
	public void insert(Document doc, boolean commit)
			throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		if (doc != null) {
			indexWriter.addDocument(doc);
			//indexWriter.optimize(); //优化
			if (commit) {
				commit();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("createIndexMap:" + doc + ", span ", start));
		}
	}

	public void insert(List<Document> list) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		for (int i=0; i<list.size(); i++) {
			insert(list.get(i),
				(i == list.size() - 1 ) ? true : false);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("createIndexMap:" + list + ", span ", start));
		}		
	}

	public void commit() throws IOException {
		indexWriter.prepareCommit();
		indexWriter.commit();
	}
	
	public void update(Document doc, Term term) throws Exception {
		update(doc, term, true);
	}
	
	public void update(Document doc, Term term, boolean commit)
			throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		if (term == null) {
			throw new Exception("term error: term is not null.");
		}
		if (doc == null) {
			throw new Exception("doc error: doc is not null.");
		}
		indexWriter.updateDocument(term, doc);
		//indexWriter.optimize(); //优化
		if (commit) {
			commit();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateIndexMap:" + doc + ", span ", start));
		}
	}
	
//	public void delete(boolean commit, Term... terms) throws Exception {
//		delete(terms);
//	}
	
	public void delete(Term... terms) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		if (terms == null || terms.length == 0) {
			throw new Exception("term error: term is not null.");
		}
		indexWriter.deleteDocuments(terms);
		//indexWriter.optimize(); //优化
		//if (commit) {
		commit();
		//}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("updateIndexMap:" + terms + ", span ", start));
		}
	}
	
	public void closeIndex() throws Exception {
		this.indexWriter.close();
	}

}
