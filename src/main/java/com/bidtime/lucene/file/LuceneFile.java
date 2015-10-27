package com.bidtime.lucene.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.utils.comm.SimpleHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

import com.bidtime.lucene.base.LuceneIndexRoot;
import com.bidtime.lucene.base.LuceneSearch;

public class LuceneFile extends LuceneIndexRoot {

	private static final Logger logger = LoggerFactory
			.getLogger(LuceneFile.class);

	String indexFileDir;

	public LuceneFile() {
		super();
	}

	@Override
	protected void initialIndexDir() throws Exception {
		indexDir = FSDirectory.open(new File(indexFileDir));
		logger.info("initialIndexDir:" + indexFileDir);
	}

	public String getIndexFileDir() {
		return indexFileDir;
	}

	public void setIndexFileDir(String indexFileDir) {
		this.indexFileDir = indexFileDir;
	}

	@Override
	protected void doFileNotExists(String s) throws Exception {
		logger.error("createDirsIndex: " + s + " file not exists.");
		throw new Exception("file not exists");
	}

	public static void main(String[] args) throws Exception {
		testCreateIndex();
		//String key = "*:*";//"name_shouzimu:";//"id:" + 5;
		//searchIt(key);
	}

	public static void testIndexCarType(String key, boolean bCreateIndex) {
		LuceneFile m = new LuceneFile();
		try {
			// IKAnalyzer中文分词
			m.setOpenMode(bCreateIndex);
			Analyzer analyzer = new IKAnalyzer4PinYin(true);
			m.setAnalyzer(analyzer);
			m.setFileSource("D:/DATA/source7/partsCarType");
			m.setIndexFileDir("D:/DATA/index7/partsCarType");
			m.initial();
			//String head = "names";
			// String[] heads = new String[]{"allAliasName", "allAliasNamePY",
			// "allAliasNamePYShouZiMu"};
			//String showHead[] = new String[]{"name","nameId"};
//			m.getIndexSearch()
//					.search(" carTypeId:6286 "
//							+ KeyWordsUtils.AND
//							+ KeyWordsUtils.bracketWords(
//									head, key), 
//									showHead, 1000);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addToDoc() {
		Map map = new SimpleHashMap();
		map.put("id", 6);
		map.put("code", "06");
		map.put("name", "你是俄国人");
		try {
			this.createIndex(map);
		} catch (Exception e) {
			logger.error("addToDoc", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void testCreateIndex() {
		LuceneFile m = new LuceneFile();
		try {
			// IKAnalyzer中文分词
			Analyzer analyzer = null;
			analyzer = new IKAnalyzer4PinYin(false);
			m.setOpenMode(false);
			m.setAnalyzer(analyzer);
			m.setFileSource("D:/DATA/lucene/source/raw.txt");
			m.setIndexFileDir("D:/DATA/lucene/index/");
			m.initial();
			ResultDTO dto = m.search("*:*", 0, 10, "id", false);
			System.out.println(dto.toString());
			//m.addToDoc();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void searchIt(String keyName) throws Exception {
		Analyzer analyzer = null;
		analyzer = new IKAnalyzer4PinYin(false);
		String s = "D:/DATA/lucene/index/";
		Directory indexDir = null;
		try {
			indexDir = FSDirectory.open(new File(s));
		} catch (IOException e) {
			e.printStackTrace();
		}
		LuceneSearch ls = new LuceneSearch(indexDir, analyzer);
		//String[] resultFields = null;	//new String[]{"name", "allNameIds"};
		//String head = "name";
		ResultDTO dto = ls.search(keyName, 0, 10);
//		ResultDTO dto = ls.search(KeyWordsUtils.bracketWords(
//				head, key), resultFields, 10);
		System.out.println(dto.toString());
	}
}
