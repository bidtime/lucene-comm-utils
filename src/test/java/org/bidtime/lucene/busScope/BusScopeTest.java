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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.BasicTest;
import org.bidtime.lucene.busScope.bean.BusScope;
import org.bidtime.lucene.busScope.dao.BusScopeDAO;
import org.bidtime.lucene.ldbc.rs.handler.BeanLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.BeanListLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.ColumnLSetHandler;
import org.bidtime.lucene.utils.FileCommon;
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
		String words = "name:灯";
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
		for (int i=1; i<listCtx.size(); i++) {
			String s = listCtx.get(i);
			String[] tmp = s.split("\t");
			//
			BusScope sc = new BusScope();
			sc.setId(i);
			sc.setPid(Integer.parseInt(tmp[0]));
			sc.setPname(tmp[1]);
			sc.setBsid(Integer.parseInt(tmp[2]));
			sc.setName(tmp[3]);
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
			String words = "name:" + p.getName();
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
	
//	@Test
//	public void test_insert() throws Exception {
//		for (int i=0; i<50; i++) {
//			BusScope b = new BusScope();
//			b.setId(i);
//			b.setName("江少山_" + i);
//			b.setCode("jss_" + i);
//			dao.insert(b);
//		}
//		print("ok");
//	}
	
	private static final String INDEX_PATH = "D:/data/lucene/index/cargoods/";
	
	//Lucene Document的域名
		private static final String name = "name";
		private static final String bsid = "bsid";
		private static final String pid = "pid";
		private static final String quanpin = "pinyin";
		private static final String shouzimu = "shouzimu";
		
	@Test
	public void test_create(){
		 //检索内容
		//String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。中国人会中文";
		String text = "灯具";
		
		//实例化IKAnalyzer分词器
        //使用PerFieldAnalyzerWrapper可以对不同的field使用不同的分词器
        Map<String, Analyzer> analyzerMap = new HashMap<String, Analyzer>();
        analyzerMap.put(quanpin, new IKAnalyzer4PinYin(false, IKAnalyzer4PinYin.PINYIN));
        analyzerMap.put(shouzimu, new IKAnalyzer4PinYin(false, IKAnalyzer4PinYin.PINYIN_SHOUZIMU));
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
				text = u.getName();
				Document doc = new Document();
				doc.add(new StringField("id", String.valueOf(i), Field.Store.YES));
	            doc.add(new TextField(name, text, Field.Store.YES));
	            doc.add(new TextField(quanpin,  text, Field.Store.YES));
	            doc.add(new TextField(shouzimu, text, Field.Store.YES));
	
	            doc.add(new TextField(pid, u.getPid().toString(), Field.Store.YES));
	            doc.add(new TextField(bsid, u.getBsid().toString(), Field.Store.YES));

	            iwriter.addDocument(doc);
				i ++;
			}
			iwriter.close();
			System.out.println("create ok.");
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
	public void test_search(){
		
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
			
			String keyword = "灯";			
			//使用QueryParser查询分析器构造Query对象
			Analyzer analyzer = new IKAnalyzer4PinYin(true);
	        QueryParser qp = new QueryParser(name,  analyzer);
	      
	        Query query = qp.parse(keyword);
	//        
	        BooleanQuery bq=new BooleanQuery();
	        BooleanQuery innerbq=new BooleanQuery();
	//        
	        bq.add(query, BooleanClause.Occur.SHOULD);
	        innerbq.add(bq, BooleanClause.Occur.MUST);
	
			//搜索相似度最高的5条记录
			TopDocs topDocs = isearcher.search(innerbq, 5);
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
