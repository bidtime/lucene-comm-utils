package com.bidtime.lucene.demo;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.lucene.store.FSDirectory;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.utils.comm.SimpleHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

import com.bidtime.lucene.base.create.LuceneCreate;
import com.bidtime.lucene.base.search.LuceneSearch;
import com.bidtime.lucene.base.utils.FieldsMagnt;

public class IndexTest {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexTest.class);

	public static void main(String[] args) throws Exception {
		boolean createIdx = false;
		if (createIdx) {
			testCreateIndex();
		} else {
			String key = "name:中国 OR name_shouzimu:eg";//"id:" + 5;
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
			"D:/DATA/lucene/source/demo/raw.txt",
			new IKAnalyzer4PinYin(false),
			"D:/data/lucene/index/demo");
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
		LuceneSearch m = new LuceneSearch("D:/DATA/lucene/source/demo/raw.txt",
			FSDirectory.open(new File("D:/DATA/lucene/index/demo/")));
		ResultDTO dto = m.search(keyName, 0, 10);
		logger.info(dto.toJson().toString());
	}
}
