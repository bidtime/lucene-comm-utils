package demo;

import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class SearchTester {

//	private static final Logger logger = LoggerFactory
//			.getLogger(SearchTester.class);

	private Directory dir;
	private Analyzer analyzer;
	private IndexSearcher searcher;
	private boolean create=true;

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//IKAnalyzer4PinYin py = new IKAnalyzer4PinYin(false);
		//SearchTester searchTester = new SearchTester();
	}

//	private static void demoTest(boolean bCreate) throws Exception {
//		SearchTester t = new SearchTester();
//		t.setCreate(bCreate);
//		t.buildIndex("D:/DATA/index_demo");
//		//t.testSearch("typeName", "字符串");
//		//t.search2("name", "大修包", (short)20);
//		//t.search2("carTypeId", "2", (short)20);
//		t.search3("name", "name:大修包 AND carTypeId:2", (short)20);
//		//t.testSearch("name", "delphi");
//		//t.testSearch("code", "04111");
//		//t.testSearch("name:delphi AND typeName:TkRoot");
//		//t.search("typeName:jss");
//		//t.search("id:3");
//		//t.search("typeName:jss AND id:3");
//		//t.search("name:上水管 AND carTypeId:52", (short)20);
//	}

//	private static void demoSearch() throws Exception {
//		SearchTester t = new SearchTester();
//		t.setCreate(false);
//		t.buildIndex("D:/DATA/index4");
//		//t.testSearch("typeName", "字符串");
//		//t.search1("name", "大修包", (short)20);
//		//t.search1("carTypeId", "2", (short)20);
//		t.search3("name", "name:大修包 AND carTypeId:1", (short)2);
//		//t.testSearch("name", "delphi");
//		//t.testSearch("code", "04111");
//		//t.testSearch("name:delphi AND typeName:TkRoot");
//		//t.search("typeName:jss");
//		//t.search("id:3");
//		//t.search("typeName:jss AND id:3");
//		//t.search("name:上水管 AND carTypeId:52", (short)20);
//	}
	
	void search1(String field, String word, short nPageSize) throws Exception {
		Query q = new TermQuery(new Term(field, word.toLowerCase()));
		TopDocs docs = searcher.search(q, null, nPageSize);
		System.out.println(field+":"+word );
		System.out.println("\t HITS:" + docs.totalHits );
		for (int i = 0; i < docs.totalHits; i++) {
			ScoreDoc s_doc = docs.scoreDocs[i];
			Document doc = searcher.doc(s_doc.doc);
			System.out.println(doc);
		}
	}

	@SuppressWarnings("deprecation")
	void buildIndex(String pathName) throws Exception {
		if (StringUtils.isEmpty(pathName)) {
			dir = new RAMDirectory();
		} else {
			dir = FSDirectory.open(Paths.get(pathName));
		}
		analyzer = new StandardAnalyzer();
		if (create) {
			//配置IndexWriterConfig    
			IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);   
			//iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);		
			iwConfig.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, iwConfig);		
			
			for (int i=0; i<3; i++) {
				Document doc = new Document();
				if (i==0) {
					doc.add(new Field("id", String.valueOf(i+1), Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("carTypeId", "11", Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("typeName", "Java,数组,TkRoot", Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("name", "这是java,大修包一个测试", Field.Store.YES,
							Field.Index.ANALYZED));
//					FieldType doctype = new FieldType();
//					doctype.setIndexed(true);
//					doctype.setStored(true);
				} else if (i==1) {
					doc = new Document();
					doc.add(new Field("id", String.valueOf(i+1), Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("carTypeId", "21", Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("typeName", "Delphi,字符串,string", Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("name", "这是delphi,大修包一个测试", Field.Store.YES,
							Field.Index.ANALYZED));
				} else if (i==2) {
					doc = new Document();
					doc.add(new Field("id", String.valueOf(i+1), Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("carTypeId", "12", Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("typeName", "大修包,发动机,", Field.Store.YES,
							Field.Index.ANALYZED));
					doc.add(new Field("name", "这是大修包一个测试", Field.Store.YES,
							Field.Index.ANALYZED));
				}
				writer.addDocument(doc);
			}
			writer.close();
		}
		IndexReader reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);		
	}

}
