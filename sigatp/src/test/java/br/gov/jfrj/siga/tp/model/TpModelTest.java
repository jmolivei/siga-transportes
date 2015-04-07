package br.gov.jfrj.siga.tp.model;

import java.util.GregorianCalendar;
import org.junit.Assert;
import org.junit.Test;

public class TpModelTest {
	
	@Test
	public void formatDateDDMMYYYY_happyDay() {
		TpModel tp = new TpModel();
		Assert.assertEquals("01/01/2015", tp.formatDateDDMMYYYY(new GregorianCalendar(2015, 0, 1)));
	}
	
	@Test
	public void formatDateDDMMYYYYHHMM_happyDay() {
		TpModel tp = new TpModel();
		Assert.assertEquals("01/01/2015 10:10", tp.formatDateDDMMYYYYHHMM(new GregorianCalendar(2015, 0, 1, 10, 10)));
	}

}
