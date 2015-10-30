package com.bidtime.lucene.file;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.SimpleFSDirectory;
import org.bidtime.utils.comm.SimpleHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bidtime.lucene.base.LuceneCreate;
import com.bidtime.lucene.base.utils.FieldsMagnt;

public class LuceneFile extends LuceneCreate {

	private static final Logger logger = LoggerFactory
			.getLogger(LuceneFile.class);

	public LuceneFile(Analyzer analyzer, Boolean openMode, 
			FieldsMagnt headMagt, String idxFileDir) throws Exception {
		super(analyzer, openMode, headMagt,
				new SimpleFSDirectory(new File(idxFileDir)));
	}
	
//	@Override
//	protected void initialIndexDir() throws Exception {
		//indexDir = FSDirectory.open(new File(indexFileDir));
		//logger.info("initialIndexDir:" + indexFileDir);
//	}

//	@Override
//	protected void doFileNotExists(String s) throws Exception {
//		logger.error("createDirsIndex: " + s + " file not exists.");
//		throw new Exception("file not exists");
//	}

//	public static void main(String[] args) throws Exception {
//		testCreateIndex(true);
//		//String key = "*:*";//"name_shouzimu:";//"id:" + 5;
//		//searchIt(key);
//	}

//	public static void testIndexCarType(String key, boolean bCreateIndex) {
//		LuceneFile m = new LuceneFile();
//		try {
//			// IKAnalyzer中文分词
//			m.setOpenMode(bCreateIndex);
//			Analyzer analyzer = new IKAnalyzer4PinYin(true);
//			m.setAnalyzer(analyzer);
//			//m.setFileSource("D:/DATA/source7/partsCarType");
//			//m.setIndexFileDir("D:/DATA/index7/partsCarType");
//			//m.initial();
//			//String head = "names";
//			// String[] heads = new String[]{"allAliasName", "allAliasNamePY",
//			// "allAliasNamePYShouZiMu"};
//			//String showHead[] = new String[]{"name","nameId"};
////			m.getIndexSearch()
////					.search(" carTypeId:6286 "
////							+ KeyWordsUtils.AND
////							+ KeyWordsUtils.bracketWords(
////									head, key), 
////									showHead, 1000);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addToDoc() {
		Map map = new SimpleHashMap();
		map.put("id", 6);
		map.put("code", "06");
		map.put("tmCreate", new Date());
		map.put("name", "你是俄国人");
//		try {
//			this.createIndex(map);
//		} catch (Exception e) {
//			logger.error("addToDoc", e);
//		}
	}

//	@SuppressWarnings("rawtypes")
//	public static void testCreateIndex(boolean create) {
//		LuceneFile m = new LuceneFile();
//		try {
//			// IKAnalyzer中文分词
//			Analyzer analyzer = null;
//			analyzer = new IKAnalyzer4PinYin(false);
//			m.setOpenMode(create);
//			m.setAnalyzer(analyzer);
//			//m.setFileSource("D:/DATA/lucene/source/raw.txt");
//			//m.setIndexFileDir("D:/DATA/lucene/index/");
//			//m.initial();
//			if (create) {
//				m.addToDoc();
//			}
//			//ResultDTO dto = m.search("*:*", 0, 10, "id", false);
//			//System.out.println(dto.toString());
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//	}
	
//	@SuppressWarnings("rawtypes")
//	public static void searchIt(String keyName) throws Exception {
//		Analyzer analyzer = null;
//		analyzer = new IKAnalyzer4PinYin(false);
//		String s = "D:/DATA/lucene/index/";
//		Directory indexDir = null;
//		try {
//			indexDir = FSDirectory.open(new File(s));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		LuceneSearch ls = new LuceneSearch(indexDir, analyzer);
//		//String[] resultFields = null;	//new String[]{"name", "allNameIds"};
//		//String head = "name";
//		ResultDTO dto = ls.search(keyName, 0, 10);
//		System.out.println(dto.toString());
//	}
}
