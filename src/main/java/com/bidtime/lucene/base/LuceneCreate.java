package com.bidtime.lucene.base;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bidtime.lucene.base.utils.FieldHeadMagnt;

public class LuceneCreate {
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneCreate.class);

	Directory indexDir;
	
	IndexWriter indexWriter;
	Analyzer analyzer;
	IndexWriterConfig iwConfig;
	Boolean openMode = false;

	public LuceneCreate(Directory dir, Analyzer analyzer, Boolean openMode) {
		this.indexDir = dir;
		this.analyzer = analyzer;
		this.openMode = openMode;
	}
	
	public void initial() throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	private void initConfig(PerFieldAnalyzerWrapper wrapper) throws IOException {
		//配置IndexWriterConfig
		if ( wrapper == null ) {
			iwConfig = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
		} else {
			iwConfig = new IndexWriterConfig(Version.LUCENE_CURRENT, wrapper);			
		}
		if (openMode) {
			iwConfig.setOpenMode(OpenMode.CREATE);
		} else {
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		//setMaxBufferedDocs
		//iwConfig.setMaxBufferedDocs(-1);
		indexWriter = new IndexWriter(indexDir, iwConfig);
		//auto unlock
		//IndexWriter.unlock(indexDir);
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

	@SuppressWarnings("rawtypes")
	public void createIndexMap(String filePath, Integer marginLines, Map map)
			throws Exception {
		Long startTime = System.currentTimeMillis();
		//indexWriter.(Integer.MAX_VALUE);
		//int n = IndexWriter.MAX_TERM_LENGTH;
		String[] arHeads = null;
		String[] arDataTypes = null;
		String[] arIndexs = null;
		String[] arTokenized = null;
		String[] arStoreTypes = null;
		PerFieldAnalyzerWrapper wrapper = null;
		
		FieldHeadMagnt headMngt = null;
		
		logger.info("文件:" + filePath + "加入索引中...");
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
				}
				n++;
				if (n % 5000 == 0) {
					logger.info("reading lines: " + n);
					//indexCommit();
				}
			}
			if (headMngt == null) {
				headMngt = new FieldHeadMagnt(arHeads, arDataTypes,
					arIndexs, arTokenized, arStoreTypes);
				wrapper = headMngt.getPinYinAnalyzer(analyzer);
				initConfig(wrapper);
			}
			Document doc = headMngt.newRows(map);
			indexWriter.addDocument(doc);
			//indexWriter.optimize(); //优化
			indexCommit();
			//indexWriter.close();
			logger.info("readlines: " + n);
			logger.info(getFmtNow(startTime) + " ms create index files.");
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}
	
	public void createIndexPath(String filePath, Integer marginLines)
			throws Exception {
		Long startTime = System.currentTimeMillis();
		//indexWriter.(Integer.MAX_VALUE);
		//int n = IndexWriter.MAX_TERM_LENGTH;
		String[] arHeads = null;
		String[] arDataTypes = null;
		String[] arIndexs = null;
		String[] arTokenized = null;
		String[] arStoreTypes = null;
		PerFieldAnalyzerWrapper wrapper = null;
		FieldHeadMagnt headMngt = null;
		
		logger.info("文件:" + filePath + "加入索引中...");
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
				} else {
					if (headMngt == null) {
						headMngt = new FieldHeadMagnt(arHeads, arDataTypes,
							arIndexs, arTokenized, arStoreTypes);
						wrapper = headMngt.getPinYinAnalyzer(analyzer);
						initConfig(wrapper);
					}
					String[] arConts = str.split("\t");
					Document doc = headMngt.newRows(arHeads, arConts);
					indexWriter.addDocument(doc);
					//indexWriter.optimize(); //优化
				}
				n++;
				if (n % 5000 == 0) {
					logger.info("reading lines: " + n);
					//indexCommit();
				}
			}
			indexCommit();
			//indexWriter.close();
			logger.info("readlines: " + n);
			logger.info(getFmtNow(startTime) + " ms create index files.");
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}
	
	private String getFmtNow(Long startTime) {
		Long endTime = System.currentTimeMillis();
		return "span: " + (endTime - startTime);
	}

	private void indexCommit() throws IOException {
		indexWriter.prepareCommit();
		indexWriter.commit();
	}

}
