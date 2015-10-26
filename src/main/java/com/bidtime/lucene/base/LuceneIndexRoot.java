package com.bidtime.lucene.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bidtime.lucene.utils.FileCommon;

public class LuceneIndexRoot {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneIndexRoot.class);

	//Directory
	protected Analyzer analyzer;

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	protected Directory indexDir;
	protected String fileSource;
	protected String extName;
	public String getExtName() {
		return extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}

	protected Integer marginLines = 5;
	protected Boolean openMode = false;
	
	public Boolean getOpenMode() {
		return openMode;
	}

	public void setOpenMode(Boolean openMode) {
		this.openMode = openMode;
	}

	public Integer getMarginLines() {
		return marginLines;
	}

	public void setMarginLines(Integer marginLines) {
		this.marginLines = marginLines;
	}

	protected LuceneCreate indexCreate;
	//protected LuceneSearch indexSearch;

	public LuceneCreate getIndexCreate() {
		return indexCreate;
	}

	public void setIndexCreate(LuceneCreate indexCreate) {
		this.indexCreate = indexCreate;
	}

//	public LuceneSearch getIndexSearch() {
//		return indexSearch;
//	}
//
//	public void setIndexSearch(LuceneSearch indexSearch) {
//		this.indexSearch = indexSearch;
//	}
	
	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}
	
	public LuceneIndexRoot() {
	}
	
	protected void initialIndexDir() throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public void initial() throws Exception {
		initialIndexDir();
		if (analyzer == null) {
			analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		}
		indexCreate = new LuceneCreate(indexDir, analyzer, openMode);
		//indexSearch = new LuceneSearch(indexDir, analyzer);
		//
		indexCreate.initial();
//		if (createIndex != null && createIndex) {
//			createDirsIndex(fileSource);
//		}
		//indexSearch.initial();
	}
	
	private boolean isHitExtName(String s) {
		if (StringUtils.isEmpty(this.extName)) {
			return true;
		} else {
			return s.endsWith(this.extName);
		}
	}
	
	protected void doFileNotExists(String s) throws Exception {
		logger.info("createDirsIndex: " 
				+ s + " file not exists." );
	}
	
	@SuppressWarnings("rawtypes")
	public void createIndex(Map map) throws Exception {
		indexCreate.createIndexMap(fileSource, marginLines, map);
	}
		
	protected void createDirsIndex(String s) throws Exception {
		File file = new File(s);
		if (!file.exists()) {
			doFileNotExists(s);
		} if (file.isDirectory()) {
			List<String> fileList = new ArrayList<String>();
			FileCommon.listFile(new File(s), fileList);
			for (String filePath : fileList) {
				logger.info("file:"+filePath);
				if (isHitExtName(filePath)) {
					indexCreate.createIndexPath(filePath, marginLines);
				}
			}
		} else {
			if (isHitExtName(s)) {
				indexCreate.createIndexPath(s, marginLines);
			}
		}
	}
	
	public void testSearch() throws Exception {
		//GsonEbRst rst = null;
		//String head = "name";
		//String[] headReturn = new String[]{"name", "typeName"};
		//rst = getIndexSearch().searchKeyWords("name:发动机", (short)20);
		//rst = getIndexSearch().search("name:发动机", (short)20);
		//rst = getIndexSearch().searchWord("发动机", "name", (short)20);
		//rst = getIndexSearch().searchWord("name:发动机", (short)20);
		//getIndexSearch().search("carTypeId:573 AND allAliasName:d", 10);
		//getIndexSearch().searchTerm("allAliasName", "刹车片", (short)500);
		//getIndexSearch().search("name:大修包 AND carTypeId:1664", (short)20);
		//rst = getIndexSearch().searchKeyWords("typeName","开关",null,(short)20);		
//		rst = getIndexSearch().searchxx("中", (short)20);
		
//		String keys[] = new String[] {"typeName"};
//		String params[] = new String[] { "jss" };
//		BooleanClause.Occur[] occur = new BooleanClause.Occur[] { BooleanClause.Occur.SHOULD };
//		rst = getIndexSearch().searchKeyWords(keys, params, occur, (short)20);
		
//		String keys[] = new String[] {"id", "typeName"};
//		String params[] = new String[] { "3", "jss"};
//		BooleanClause.Occur[] occur = new BooleanClause.Occur[] { 
//				BooleanClause.Occur.MUST, BooleanClause.Occur.SHOULD };
//		rst = getIndexSearch().searchKeyWords(keys, params, occur, (short)20);

//		String keys[] = new String[] {"typeName"};
//		String params[] = new String[] { "mike"};
//		BooleanClause.Occur[] occur = new BooleanClause.Occur[] { BooleanClause.Occur.MUST};
//		rst = getIndexSearch().searchKeyWords(keys, params, occur, (short)20);
	}

}
