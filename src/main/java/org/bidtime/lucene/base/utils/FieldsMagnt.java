package org.bidtime.lucene.base.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Sort;
import org.bidtime.utils.basic.ObjectComm;
import org.bidtime.utils.comm.CaseInsensitiveHashMap;
import org.bidtime.utils.comm.CaseInsensitiveHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

public class FieldsMagnt {
	
	private static final Logger logger = LoggerFactory
			.getLogger(FieldsMagnt.class);

	private Map<String, FieldProp> mapProps = new CaseInsensitiveHashMap<FieldProp>();
	private Set<String> mapDateTime = new CaseInsensitiveHashSet();
	private Set<String> mapPk = new CaseInsensitiveHashSet();
	protected String fileSource;
	protected Integer marginLines = 6;

	public FieldsMagnt(String fileSource) throws Exception {
		this.fileSource = fileSource;
		setIndexPath(fileSource, marginLines);
	}

	public FieldsMagnt(String fileSource, Integer marginLines) throws Exception {
		this.fileSource = fileSource;
		this.marginLines = marginLines;
		setIndexPath(fileSource, marginLines);
	}
	
	public Integer getMarginLines() {
		return marginLines;
	}

	public void setMarginLines(Integer marginLines) {
		this.marginLines = marginLines;
	}

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public Set<String> getMapPk() {
		return mapPk;
	}

	public void setMapPk(Set<String> mapPk) {
		this.mapPk = mapPk;
	}

	public Set<String> getMapDateTime() {
		return mapDateTime;
	}

	public void setMapDateTime(Set<String> mapDateTime) {
		this.mapDateTime = mapDateTime;
	}

	public FieldProp get(Object key) {
		return mapProps.get(key);
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
		//Long startTime = System.currentTimeMillis();
		//indexWriter.(Integer.MAX_VALUE);
		//int n = IndexWriter.MAX_TERM_LENGTH;
		String[] arHeads = null;
		String[] arDataTypes = null;
		String[] arIndexs = null;
		String[] arTokenized = null;
		String[] arStoreTypes = null;
		String[] arPks = null;

		logger.info("文件:" + filePath +", read index format file...");
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
			setProps(arHeads, arDataTypes,
					arIndexs, arTokenized, arStoreTypes, arPks);
			//wrapper = getPinYinAnalyzer(analyzer);
			//initConfig(wrapper);
			logger.info("readlines: " + n);
			logger.info("read index format file.");
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}

	private void setProps(String[] arHeads, String[] arDataTypes, 
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
			FieldProp p = new FieldProp(i, head, dataType, index,
					tokenized, storeType, pk);
			if (logger.isDebugEnabled()) {
				logger.debug(p.toString());
			}
			mapProps.put(head, p);
			if (p.isDateTime()) {
				mapDateTime.add(head);
			}
			if (p.isPk()) {
				mapPk.add(head);
			}
		}
	}
	
	public PerFieldAnalyzerWrapper getPinYinAnalyzer(Analyzer analyzer) throws Exception {
		Map<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();		
		for (Entry<String, FieldProp> entry : mapProps.entrySet()) {
			FieldProp p = entry.getValue();
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
			FieldProp prop = get(head);
			if ( prop == null ) {
				continue;
			}
			Field field = null;
			if (prop.isPYType()) {
				FieldProp pyProp = get(prop.getPYHead());	//获取拼音的头
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
		for (Map.Entry<String, FieldProp> entry : mapProps.entrySet()) {
			String head = entry.getKey();
			FieldProp prop = entry.getValue();
			Field field = null;
			String headReturn = null;
			if (prop.isPYType()) {		//获取拼音的头
				FieldProp pyProp = get(prop.getPYHead());
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
		FieldProp o = get(fld);
		if (o == null) {
			throw new Exception(fld + "field is null.");
		}
		return o.getSort(reverse);
	}
	
	public Term getTermOfMap(Object val) throws Exception {
		if (val == null) {
			throw new Exception("get term error: pk value is not null.");
		}
		Set<String> setPk = this.getMapPk();
		if (setPk == null || setPk.isEmpty()) {
			throw new Exception("get term error: field not set pk.");
		} else if (setPk.size() >= 2) {
			throw new Exception("get term error: field pk large 2 - " + setPk.size());			
		}
		String pk = setPk.iterator().next();
		return new Term(pk, ObjectComm.objectToString(val));
	}
	
	@SuppressWarnings("rawtypes")
	public Term getTermOfMap(Map map) throws Exception {
		Set<String> setPk = this.getMapPk();
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

}
