package com.bidtime.lucene.memory;

import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bidtime.lucene.base.LuceneIndexRoot;

public class LuceneMemory extends LuceneIndexRoot {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneMemory.class);

	@Override
	protected void initialIndexDir() throws Exception {
		indexDir = new RAMDirectory();
	}

	public static void main(String[] args) {
		testIndex();
	}
	
	public static void testIndex() {
		LuceneMemory m = new LuceneMemory();
		try {
			m.setFileSource("D:/DATA/source4/partsCarType");
			m.initial();
			m.testSearch();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
