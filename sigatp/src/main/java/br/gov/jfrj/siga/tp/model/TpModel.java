package br.gov.jfrj.siga.tp.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.gov.jfrj.siga.model.Objeto;

public class TpModel extends Objeto {

	private static final long serialVersionUID = -3265658962532346951L;

	public static final Long VAZIO = 0L;

	@Override
	public void save() {
		TpDao.getInstance().gravar(this);
	}
	
	public String formatDateDDMMYYYY(Calendar cal) {
		return formatDate(cal, "dd/MM/yyyy");
	}
	
	public String formatDateDDMMYYYYHHMM(Calendar cal) {
		return formatDate(cal, "dd/MM/yyyy HH:mm");
	}

	private String formatDate(Calendar cal, String formato) {
		return new SimpleDateFormat(formato).format(cal.getTime());
	}
	
}
