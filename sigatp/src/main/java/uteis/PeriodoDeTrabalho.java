package uteis;

import java.util.Calendar;

import models.DiaDaSemana;

public class PeriodoDeTrabalho {
	public Long id;
	public DiaDaSemana dia;
	public Calendar hora;
	
	public PeriodoDeTrabalho(Long id, DiaDaSemana dia, Calendar hora) {
		this.id = id;
		this.dia = dia;
		this.hora = hora;
	}
	
}
