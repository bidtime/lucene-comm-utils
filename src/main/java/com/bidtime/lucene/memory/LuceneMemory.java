package com.bidtime.lucene.memory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.RAMDirectory;

import com.bidtime.lucene.base.LuceneCreate;
import com.bidtime.lucene.base.utils.FieldsMagnt;

public class LuceneMemory extends LuceneCreate {
	
//	private static final Logger logger = LoggerFactory
//			.getLogger(LuceneMemory.class);
	
	public LuceneMemory(Analyzer analyzer, Boolean openMode, 
			FieldsMagnt headMagt) throws Exception {
		super(analyzer, openMode, headMagt,
				new RAMDirectory());
	}

//	public static void main(String[] args) {
//		testIndex();
//	}
	
//	public static void testIndex() {
//		LuceneMemory m = new LuceneMemory();
//		try {
//			m.setFileSource("D:/DATA/source4/partsCarType");
//			m.initial();
//			m.testSearch();
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//	}
}
