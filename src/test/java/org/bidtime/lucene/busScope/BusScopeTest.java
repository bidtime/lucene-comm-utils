package org.bidtime.lucene.busScope;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.BasicTest;
import org.bidtime.lucene.busScope.bean.BusScope;
import org.bidtime.lucene.busScope.dao.BusScopeDAO;
import org.bidtime.lucene.ldbc.rs.handler.BeanLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.BeanListLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.ColumnLSetHandler;
import org.bidtime.lucene.utils.FileCommon;
import org.bidtime.lucene.utils.KeyWordsUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wltea4pinyin.analyzer.lucene.IKAnalyzer4PinYin;

/**
 * Created by bidtim on 2015/9/23.
 */
public class BusScopeTest extends BasicTest {

	@Autowired
	protected BusScopeDAO dao;

	@Test
	public void test_search_bean() throws Exception {
		BeanLDTOHandler<BusScope> h = new BeanLDTOHandler<>(BusScope.class);
//		String words = "code:jss_0";
		String words = "name:灯 刹 中关村";
		ResultDTO<BusScope> dto = dao.query(words, h);
		System.out.println("search_bean: " + dto.getLen() + " -> " + dto.toString());
	}

	@Test
	public void test_search_set() throws Exception {
		ColumnLSetHandler<Integer> h = new ColumnLSetHandler<Integer>(Integer.class, "pid");
//		String words = "code:jss_0";
		String words = "name:空滤";
		Set<Integer> dto = dao.query(words, 0, 10, h);
		System.out.println("search_set: " + dto.size() + " -> " + dto.toString());
	}

	@Test
	public void test_search_list() throws Exception {
		BeanListLDTOHandler<BusScope> h = new BeanListLDTOHandler<>(BusScope.class);
		String words = "name:江";
		ResultDTO<List<BusScope>> dto = dao.query(words, 0, 10, h);
		System.out.println("search_list: " + dto.getLen() + " -> " + dto.toString());
	}
	
	private static List<BusScope> readIt() throws Exception {
		List<BusScope> list = new ArrayList<>();
		//
		List<String> listCtx = FileCommon.getFileCtxList("D:/data/lucene/index/raw/business.txt", "UTF-8");
		for (int i=0; i<listCtx.size(); i++) {
			String s = listCtx.get(i);
			String[] tmp = s.split("\t");
			//
			BusScope sc = new BusScope();
			sc.setId(i);
			sc.setpId(Integer.parseInt(tmp[0].trim()));
			sc.setpName(tmp[1]);
			sc.setBsId(Integer.parseInt(tmp[2].trim()));
			sc.setBsName(tmp[3]);
			//print(sc);
			//
			list.add(sc);
		}
		return list;
	}
	
	@Test
	public void test_insert_file() throws Exception {
		List<BusScope> list = readIt();
		for (BusScope p : list) {
			dao.insert(p);
		}
		print("ok：" + list.size());
	}
	
	@Test
	public void test_search_file() throws Exception {
		List<BusScope> list = readIt();
		int n=0;
		BeanLDTOHandler<BusScope> h = new BeanLDTOHandler<>(BusScope.class);
		for (BusScope p : list) {
			String words = "name:" + p.getBsName();
			ResultDTO<BusScope> dto = dao.query(words, 0, 10, h);
			if (dto != null && dto.getData() != null) {
				//System.out.println("search hit: " + dto.getLen() + " -> " + words);
			} else {
				n ++;
				System.out.println("search hit: none -> " + words);			
			}
		}
		print("fail：" + n + "/" + list.size());
	}
	
	@Test
	public void test_search_file_word() throws Exception {
		int n=0;
		int j=0;
		BeanLDTOHandler<BusScope> h = new BeanLDTOHandler<>(BusScope.class);
		List<String> list = FileCommon.getFileCtxList("D:/data/lucene/index/raw/word.txt", "UTF-8");
		for (int i=1; i<list.size(); i++) {
			String words = "name:" + list.get(i);
			ResultDTO<BusScope> dto = dao.query(words, 0, 10, h);
			if (dto != null && dto.getData() != null) {
				n ++;
				System.out.println("search hit: " + dto.getLen() + " -> " + words);
			} else {
				j ++;
				//System.out.println("search hit: none -> " + words);			
			}
		}
		print("ok：" + n + "/" + list.size());
		print("fail：" + j + "/" + list.size());
	}
	
	private static final String INDEX_PATH = "D:/data/lucene/index/parts/";
	
	//Lucene Document的域名
	private static final String BSNAME = "bsName";
	private static final String BSID = "bsId";
	private static final String PID = "pId";
	private static final String PNAME = "pName";
	private static final String BSNAME_FULL = "bsNameFull";
	private static final String BSNAME_FIRST = "bsNameFirst";
		
	@Test
	public void test_lucene_create(){		
		//实例化IKAnalyzer分词器
        //使用PerFieldAnalyzerWrapper可以对不同的field使用不同的分词器
        Map<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();
        analyzerMap.put(BSNAME_FULL, new IKAnalyzer4PinYin(false, IKAnalyzer4PinYin.PINYIN));
        analyzerMap.put(BSNAME_FIRST, new IKAnalyzer4PinYin(false, IKAnalyzer4PinYin.PINYIN_SHOUZIMU));
        PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new IKAnalyzer4PinYin(false), analyzerMap);
        
		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		try {
			//建立内存索引对象
			directory = FSDirectory.open(Paths.get(INDEX_PATH));
			//配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(wrapper);
			iwConfig.setOpenMode(OpenMode.CREATE);
			iwriter = new IndexWriter(directory , iwConfig);
			//写入索引
			List<BusScope> list = readIt();
			int i=0;
			for (BusScope u : list) {
				Document doc = new Document();
				doc.add(new StringField("id", String.valueOf(i), Field.Store.YES));
				//
	            doc.add(new IntField(BSID, u.getBsId(), Field.Store.YES));
	            doc.add(new TextField(BSNAME, u.getBsName(), Field.Store.YES));
	            //pinyin
	            doc.add(new TextField(BSNAME_FULL,  u.getBsName(), Field.Store.YES));
	            doc.add(new TextField(BSNAME_FIRST, u.getBsName(), Field.Store.YES));
	            //pid
	            doc.add(new IntField(PID, u.getpId(), Field.Store.YES));
	            doc.add(new TextField(PNAME, u.getpName(), Field.Store.YES));
	            // doc
	            iwriter.addDocument(doc);
				i ++;
			}
			iwriter.close();
			System.out.println("create " + list.size() + ", ok.");
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(ireader != null){
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(directory != null){
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public void test_lucene_search(){
		//实例化IKAnalyzer分词器
	    //使用PerFieldAnalyzerWrapper可以对不同的field使用不同的分词器
		Directory directory = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			//建立内存索引对象
			directory = FSDirectory.open(Paths.get(INDEX_PATH));	 
			//搜索过程**********************************
		    //实例化搜索器   
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);
			
			//String word = "bsName:芯 灯 刹 中关村";
			String[] fields = new String[]{BSNAME, BSNAME_FULL, BSNAME_FIRST};
			String word = KeyWordsUtils.bracketEscWords(fields, "芯 灯 刹 中关村", KeyWordsUtils.OR);
			
			//String word = "bsName:灯 OR bsNameFirst:h";			
			//String word = "bsNameFirst:d";			
			//使用QueryParser查询分析器构造Query对象
			Analyzer analyzer = new IKAnalyzer4PinYin(true);
	        QueryParser qp = new QueryParser(null,  analyzer);
	      
	        Query query = qp.parse(word);
//    
//	        BooleanQuery bq=new BooleanQuery();
//	        BooleanQuery innerbq=new BooleanQuery();
//
//	        bq.add(query, BooleanClause.Occur.SHOULD);
//	        innerbq.add(bq, BooleanClause.Occur.MUST);
	
			//搜索相似度最高的5条记录
			//TopDocs topDocs = isearcher.search(innerbq, 5);
	       // QueryParser parser = new QueryParser(null, analyzer);
	   		//
	   		TopDocs topDocs = isearcher.search(query, 5);
			System.out.println("命中：" + topDocs.totalHits);
			//输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < topDocs.totalHits; i++){
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				System.out.println("内容：" + targetDoc.toString());
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			if(ireader != null){
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(directory != null){
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
