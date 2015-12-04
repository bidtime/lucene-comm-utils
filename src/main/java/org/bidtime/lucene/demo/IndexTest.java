package org.bidtime.lucene.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.base.create.LuceneCreate;
import org.bidtime.lucene.base.search.LuceneSearch;
import org.bidtime.lucene.base.utils.FieldsMagnt;
import org.bidtime.utils.comm.SimpleHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

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
			logger.info("begin...");
			String key = "brandName:马 OR brandName_shouzimu:bm";//"id:" + 5;
			searchIt(key);
			//getTipCarGoods(key, 0, 10);
			logger.info("end.");
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

	@SuppressWarnings({ "rawtypes" })
	public static void searchIt(String keyName) throws Exception {
		LuceneSearch m = new LuceneSearch(
				new FieldsMagnt("D:/DATA/lucene/source/cargoods/raw.txt"),
				new IKAnalyzer4PinYin(false),
				"D:/DATA/lucene/index/cargoods/");
		ResultDTO dto = m.search(keyName, 0, 10, "createTime desc");
		if (dto != null && dto.isSuccess()) {
//			List<CarGoods> list = JSONHelper.listMapToClazz((List<Map>)dto.getData(), CarGoods.class);
//			ResultDTO dd = ResultDTO.success(list);
			logger.info(dto.toJson().toString());
		} else {
			logger.info(dto.toJson().toString());
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static void addMergeNameToList(List<Map> listMap, String key, Integer nPageSize, Set<String> set) throws Exception {
		for (Map m: listMap) {
			String v = (String)m.get(key);
			if (v != null) {
				set.add(v);
			}
			if (set.size() >= nPageSize) {
				break;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static List<Map<String, Object>> toMergeNameIt(List<Map> listMap, Integer nPageSize) throws Exception {		
		Set<String> set = new LinkedHashSet<String>();
		if (set.size()<nPageSize) {
			addMergeNameToList(listMap, "brandName", nPageSize, set);
		}
		if (set.size()<nPageSize) {
			addMergeNameToList(listMap, "carSysName", nPageSize, set);
		}
		if (set.size()<nPageSize) {
			addMergeNameToList(listMap, "carTypeName", nPageSize, set);
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String s: set) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("k", s);
			list.add(map);
		}
		return list;
	}
	
	public static void getTipCarGoods(String key, Integer nPageIndex, Integer nPageSize) throws Exception {
		LuceneSearch m = new LuceneSearch(
				new FieldsMagnt("D:/DATA/lucene/source/cargoods/raw.txt"),
				new IKAnalyzer4PinYin(false),
				"D:/DATA/lucene/index/cargoods/");
		for (int i=0; i<10; i++) {
			logger.info(String.valueOf(i));
			getTipCarGoods(m, key, nPageIndex, nPageSize);
		}
	}
	
	// 查询
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getTipCarGoods(LuceneSearch m, String key, Integer nPageIndex, Integer nPageSize) throws Exception {
		String[] head = new String[]{"brandName", "carSysName", "carTypeName"};
		logger.info("start search...");
		ResultDTO dto = m.search(key, nPageIndex, nPageSize * 3, head);
		if (dto != null && dto.isSuccess()) {
			List<Map<String, Object>> list = toMergeNameIt((List<Map>)dto.getData(), nPageSize);
			ResultDTO d = ResultDTO.success(list);
			logger.info("end search...");
			return (T)d;
		} else {
			return (T) dto;
		}
	}

}
