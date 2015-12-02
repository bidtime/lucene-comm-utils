package org.bidtime.lucene.demo;

import java.util.Date;
import java.util.Map;

import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.base.utils.FieldsMagnt;
import org.bidtime.utils.comm.SimpleHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

import org.bidtime.lucene.base.create.LuceneCreate;
import org.bidtime.lucene.base.search.LuceneSearch;

public class IndexTest {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexTest.class);
	
	public static final void main(String[] args) {
		try {
			doIt();
			Object lock = new Object();
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					logger.error("lock", e);
				}
			}
		} catch (Exception e) {
			logger.error("main", e);
		}
	}

	public static void doIt() throws Exception {
		boolean createIdx = false;
		if (createIdx) {
			testCreateIndex();
		} else {
			//String key = "name:中国 OR name_shouzimu:eg";//"id:" + 5;
			String key = "brandName:马 OR brandName_shouzimu:bm";//"id:" + 5;
			searchIt(key);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addToDoc(LuceneCreate m) throws Exception {
		Map map = new SimpleHashMap();
		map.put("id", 5);
		map.put("code", "05");
		map.put("tmCreate", new Date());
		map.put("name", "中国");
		m.createIndex(map);
	}

	public static void testCreateIndex() throws Exception {
		logger.info("testCreateIndex start...");
		LuceneCreate m = new LuceneCreate(
			new FieldsMagnt("D:/DATA/lucene/source/cargoods/raw.txt"),
			new IKAnalyzer4PinYin(false),
				"D:/data/lucene/index/cargoods");
		try {
			addToDoc(m);
		} finally {
			m.closeIndex();
			m = null;
		}
		logger.info("testCreateIndex end.");
	}

	@SuppressWarnings("rawtypes")
	public static void searchIt(String keyName) throws Exception {
//		LuceneSearch m = new LuceneSearch("D:/DATA/lucene/source/demo/raw.txt",
//			FSDirectory.open(new File("D:/DATA/lucene/index/demo/")));
		LuceneSearch m = new LuceneSearch(
				new FieldsMagnt("D:/DATA/lucene/source/cargoods/raw.txt"),
				"D:/DATA/lucene/index/cargoods/");
		ResultDTO dto = m.search(keyName, 0, 10);
		logger.info(dto.toJson().toString());
	}
}
