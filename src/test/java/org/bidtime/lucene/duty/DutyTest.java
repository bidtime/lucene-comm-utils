package org.bidtime.lucene.duty;

import org.bidtime.lucene.BasicTest;
import org.bidtime.lucene.duty.bean.Duty;
import org.bidtime.lucene.duty.dao.DutyDAO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bidtim on 2015/9/23.
 */
public class DutyTest extends BasicTest {

//	@Autowired
//	protected IKAnalyzer4PinYin analyzer;

	@Autowired
	protected DutyDAO dao;

	@Test
	public void test_it() throws Exception {
//		Duty duty = new Duty();
//		duty.setName("销售部");
//		int n = service.insert(duty);
		System.out.println("test: " + dao);
	}
	
	@Test
	public void test_insert() throws Exception {
		for (int i=1; i<10; i++) {
			Duty b = new Duty();
			b.setId(i);
			b.setName("江少山"+i);
			b.setCode("000"+i);
			dao.insert(b);
		}
		print("ok");
	}
	
}
