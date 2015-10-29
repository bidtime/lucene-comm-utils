package com.bidtime.lucene.base.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Sort;
import org.bidtime.utils.comm.CaseInsensitiveHashMap;
import org.bidtime.utils.comm.CaseInsensitiveHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

public class FieldHeadMagnt {
	
	private static final Logger logger = LoggerFactory
			.getLogger(FieldHeadMagnt.class);

	private Map<String, FieldHeadProp> mapProps = new CaseInsensitiveHashMap<FieldHeadProp>();
	private Set<String> mapDataTime = new CaseInsensitiveHashSet();
	private Set<String> mapPk = new CaseInsensitiveHashSet();
	
	public Set<String> getMapPk() {
		return mapPk;
	}

	public void setMapPk(Set<String> mapPk) {
		this.mapPk = mapPk;
	}

	public Set<String> getMapDataTime() {
		return mapDataTime;
	}

	public void setMapDataTime(Set<String> mapDataTime) {
		this.mapDataTime = mapDataTime;
	}

	public FieldHeadProp get(Object key) {
		return mapProps.get(key);
	}

	public FieldHeadMagnt() {
	}

	public void setProps(String[] arHeads, String[] arDataTypes, 
			String[] arIndexs, String[] arTokenized, 
			String[] arStoreTypes, String[] arPks) {
		for (int i=0; i<arHeads.length; i++) {
			String head = arHeads[i];
			String dataType = arDataTypes[i];
			String index = (arIndexs != null && arIndexs.length > i) ? arIndexs[i]: null;
			String tokenized = (arTokenized != null && arTokenized.length > i) ? arTokenized[i]: null;
			String storeType = (arStoreTypes != null && arStoreTypes.length > i) ? arStoreTypes[i]: null;
			String pk = (arPks != null && arPks.length > i) ? arPks[i]: null;
			//
			FieldHeadProp p = new FieldHeadProp(i, head, dataType, index,
					tokenized, storeType, pk);
			if (logger.isDebugEnabled()) {
				logger.debug(p.toString());
			}
			mapProps.put(head, p);
			if (p.isDateTime()) {
				mapDataTime.add(head);
			}
			if (p.isPk()) {
				mapPk.add(head);
			}
		}
	}
	
	public PerFieldAnalyzerWrapper getPinYinAnalyzer(Analyzer analyzer) throws Exception {
		Map<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();		
		for (Entry<String, FieldHeadProp> entry : mapProps.entrySet()) {
			FieldHeadProp p = entry.getValue();	//entry.getKey();
			switch (p.getPYType()) {
			case IKAnalyzer4PinYin.PINYIN:				//1
				analyzerMap.put(p.getHead(), new IKAnalyzer4PinYin(false,
						IKAnalyzer4PinYin.PINYIN));
				break;
			case IKAnalyzer4PinYin.PINYIN_SHOUZIMU:		//2
				analyzerMap.put(p.getHead(), new IKAnalyzer4PinYin(false,
						IKAnalyzer4PinYin.PINYIN_SHOUZIMU));
				break;
			default:
				//analyzerMap.put(p.getHead(), analyzer);
				break;
			}
		}
		if (!analyzerMap.isEmpty()) {
			return new PerFieldAnalyzerWrapper(
					new IKAnalyzer4PinYin(false), analyzerMap);
		} else {
			return null;
		}
	}
	
	public Document newRows(String[] arHeads, String[] arContents) throws Exception {
		Document doc = new Document();		//row
		List<Field> listField = strsToFields(arHeads, arContents);
		for (Field field: listField) {
			doc.add(field);
		}
		return doc;
	}
	
	@SuppressWarnings("rawtypes")
	public Document newRows(Map map) throws Exception {
		Document doc = null;		//row
		List<Field> listField = mapToFields(map);
		if (listField != null && !listField.isEmpty()) {
			doc = new Document();
			for (Field field: listField) {
				doc.add(field);
			}
		}
		return doc;
	}

	private List<Field> strsToFields(String[] arHeads, String[] arContents) throws Exception {
		List<Field> list = new ArrayList<Field>();
		for (int i=0; i<arHeads.length; i++) {
			String head = arHeads[i];
			FieldHeadProp prop = get(head);
			if ( prop == null ) {
				continue;
			}
			Field field = null;
			if (prop.isPYType()) {
				//获取拼音的头
				FieldHeadProp pyProp = get(prop.getPYHead());
				Integer recNo = pyProp.getRecNo();
				if (recNo < 0 ) {
					throw new Exception("column not match:" + head);
				} else {
					String content = null;
					if (arContents != null && arContents.length>recNo) {
						content = arContents[recNo];
					}
					field = prop.getFieldOfDataType(content);
				}
			} else {
				String content = null;
				if (arContents != null && arContents.length>i) {
					content = arContents[i];
				}
				field = prop.getFieldOfDataType(content);
			}
			list.add(field);
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes" })
	public List<Field> mapToFields(Map map) throws Exception{
		List<Field> list = new ArrayList<Field>();
		for (Map.Entry<String, FieldHeadProp> entry : mapProps.entrySet()) {
			String head = (String)entry.getKey();
			FieldHeadProp prop = entry.getValue();
			Field field = null;
			String headReturn = null;
			if (prop.isPYType()) {		//获取拼音的头
				FieldHeadProp pyProp = get(prop.getPYHead());
				if ( pyProp == null ) {
					throw new Exception("column not match:" + head);
				} else {
					headReturn = pyProp.getHead();
				}
			} else {
				headReturn = head;
			}
			Object objContent = map.get(headReturn);
			if (objContent != null) {
				field = prop.getFieldOfDataType(objContent);
				if (field != null) {
					list.add(field);
				}
			}
		}
		return list;
	}
	
	public Sort getSortOfField(String fld, boolean reverse) throws Exception {
		FieldHeadProp o = get(fld);
		if (o == null) {
			throw new Exception(fld + "field is null.");
		}
		return o.getSort(reverse);
	}

}
