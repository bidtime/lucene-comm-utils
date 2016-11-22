package org.bidtime.lucene.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexTest {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexTest.class);
	
	public static final void main(String[] args) {
		try {
			//doIt();
//			Object lock = new Object();
//			synchronized (lock) {
//				try {
//					lock.wait();
//				} catch (InterruptedException e) {
//					logger.error("lock", e);
//				}
//			}
		} catch (Exception e) {
			logger.error("main", e);
		}
	}
	
//	private static String getSearchTerm(String key, String cityId) {
//		StringBuilder sb = new StringBuilder();
//		String searchKey = KeyWordsUtils.bracketEscWords(
//				"brandName;carSysName;carTypeName;brandName_py;carSysName_py;carTypeName_py;brandName_shouzimu;carSysName_shouzimu;carTypeName_shouzimu",
//				key, KeyWordsUtils.OR);
//		if (StringUtils.isNotEmpty(cityId)) {
//			sb.append(searchKey);
//			sb.append(KeyWordsUtils.AND);
//			String srhCityId = KeyWordsUtils.bracketWords("cityId", cityId);
//			sb.append(srhCityId);
//			return sb.toString();
//		} else {
//			return searchKey;
//		}
//	}

//	public static void doIt() throws Exception {
//		boolean createIdx = false;
//		if (createIdx) {
//			testCreateIndex();
//		} else {
//			//String key = "name:中国 OR name_shouzimu:eg";//"id:" + 5;
//			logger.info("begin...");
//			//String key = QueryParserUtil.escape("brandName:" + "奥迪");//"id:" + 5;
//			//String key = "brandName:" + "奥迪";//"cityId:26";//"id:" + 5;
//			String key = "name:中国";//getSearchTerm("bk", null);
//			searchIt(key);
//			//getTipCarGoods(key, 0, 10);
//			logger.info("end.");
//		}
//	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static void addToDoc(LuceneCreate m) throws Exception {
//		Map map = new SimpleHashMap();
//		map.put("id", 5);
//		map.put("code", "05");
//		map.put("name", "中国");
//		map.put("tmCreate", new Date());
//		m.createIndex(map);
//	}
	
	private static final String SOURCE_PATH = "D:/TDDownload/DATA/lucene/source/parts/";
	private static final String TARGET_PATH = "D:/TDDownload/DATA/lucene/index/parts/";

//	public static void testCreateIndex() throws Exception {
//		logger.info("testCreateIndex start...");
//		LuceneCreate m = new LuceneCreate(
//			new FieldsMagnt(SOURCE_PATH + "raw.txt"),
//			new IKAnalyzer4PinYin(false), TARGET_PATH);
//		try {
//			addToDoc(m);
//		} finally {
//			m.closeIndex();
//			m = null;
//		}
//		logger.info("testCreateIndex end.");
//	}

//	@SuppressWarnings({ "rawtypes" })
//	public static void searchIt(String keyName) throws Exception {
//		LuceneSearch m = new LuceneSearch(
//				new FieldsMagnt(SOURCE_PATH + "raw.txt"),
//				new IKAnalyzer4PinYin(false), TARGET_PATH);
//		ResultDTO dto = m.search(keyName, 0, 10, "createTime desc");
//		if (dto != null && dto.isSuccess()) {
////			List<CarGoods> list = JSONHelper.listMapToClazz((List<Map>)dto.getData(), CarGoods.class);
////			ResultDTO dd = ResultDTO.success(list);
//			logger.info(dto.toString());
//		} else {
//			logger.info(dto.toString());
//		}
//	}
	
//	@SuppressWarnings({ "rawtypes" })
//	private static void addMergeNameToList(List<Map> listMap, String key, Integer nPageSize, Set<String> set) throws Exception {
//		for (Map m: listMap) {
//			String v = (String)m.get(key);
//			if (v != null) {
//				set.add(v);
//			}
//			if (set.size() >= nPageSize) {
//				break;
//			}
//		}
//	}
//	
//	@SuppressWarnings({ "rawtypes" })
//	private static List<Map<String, Object>> toMergeNameIt(List<Map> listMap, Integer nPageSize) throws Exception {		
//		Set<String> set = new LinkedHashSet<String>();
//		if (set.size()<nPageSize) {
//			addMergeNameToList(listMap, "brandName", nPageSize, set);
//		}
//		if (set.size()<nPageSize) {
//			addMergeNameToList(listMap, "carSysName", nPageSize, set);
//		}
//		if (set.size()<nPageSize) {
//			addMergeNameToList(listMap, "carTypeName", nPageSize, set);
//		}
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		for (String s: set) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("k", s);
//			list.add(map);
//		}
//		return list;
//	}
	
//	@SuppressWarnings("rawtypes")
//	private static void getNewMapInt(Map mV, String key, String keyId, char c,
//			Map<Integer, Map<String, Object>> mapBrand,
//			String brandIdKey, String carSysIdKey) {
//		String name = (String)mV.get(key);
//		if (StringUtils.isEmpty(name)) {
//			return;
//		}
//		Integer id = ObjectComm.objectToInteger(mV.get(keyId));
//		Map<String, Object> m = new LinkedHashMap<>();
//		m.put("n", name);
//		m.put("i", id);
//		m.put("t", c);
//		//
//		if (!StringUtils.isEmpty(brandIdKey)) {
//			m.put("bId", ObjectComm.objectToInteger(mV.get(brandIdKey)));
//		}
//		
//		if (!StringUtils.isEmpty(carSysIdKey)) {
//			m.put("sId", ObjectComm.objectToInteger(mV.get(carSysIdKey)));
//		}
//		
//		mapBrand.put(id, m);
//	}
	
//	@SuppressWarnings({ "rawtypes" })
//	private static List<Map<String, Object>> toMergeNameIt(List<Map> listMap, Integer nPageSize) throws Exception {				
//		Map<Integer, Map<String, Object>> mapBrand = new LinkedHashMap<>();
//		Map<Integer, Map<String, Object>> mapCarSys = new LinkedHashMap<>();
//		Map<Integer, Map<String, Object>> mapCarType = new LinkedHashMap<>();
//		for (Map m: listMap) {
//			getNewMapInt(m, "brandName", "brandId", 'B', mapBrand, null, null);						//brand
//			getNewMapInt(m, "carSysName", "carSysId", 'S', mapCarSys, "brandId", null);				//carSys
//			getNewMapInt(m, "carTypeName", "carTypeId", 'C', mapCarType, "brandId", "carSysId");	//carType
//		}
//		//
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		for (Map.Entry<Integer, Map<String, Object>> et : mapBrand.entrySet()) {
//			if (list.size() > nPageSize) { break; }
//			list.add(et.getValue());
//		}
//		for (Map.Entry<Integer, Map<String, Object>> et : mapCarSys.entrySet()) {
//			if (list.size() > nPageSize) { break; }
//			list.add(et.getValue());
//		}
//		for (Map.Entry<Integer, Map<String, Object>> et : mapCarType.entrySet()) {
//			if (list.size() > nPageSize) { break; }
//			list.add(et.getValue());
//		}
//		return list;
//	}

//	public static void getTipCarGoods(String key, Integer nPageIndex, Integer nPageSize) throws Exception {
//		LuceneSearch m = new LuceneSearch(
//				new FieldsMagnt("D:/DATA/lucene/source/cargoods/raw.txt"),
//				new IKAnalyzer4PinYin(false),
//				"D:/DATA/lucene/index/cargoods/");
//		for (int i=0; i<1; i++) {
//			logger.info(String.valueOf(i));
//			getTipCarGoods(m, key, nPageIndex, nPageSize);
//		}
//	}
	
	// 查询
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static <T> T getTipCarGoods(LuceneSearch m, String key, Integer nPageIndex, Integer nPageSize) throws Exception {
//		String[] head = new String[]{"brandName", "carSysName", "carTypeName", "brandId", "carSysId", "carTypeId"};
//		logger.info("start search...");
//		ResultDTO dto = m.search(key, nPageIndex, nPageSize * 3, head);
//		if (dto != null && dto.isSuccess()) {
//			List<Map<String, Object>> list = toMergeNameIt((List<Map>)dto.getData(), nPageSize);
//			ResultDTO d = ResultDTO.success(list);
//			logger.info("end search..." + d.toString());
//			return (T)d;
//		} else {
//			return (T) dto;
//		}
//	}

}
