package br.gov.jfrj.siga.tp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.i18n.Messages;

@Entity
@Audited
@Table(schema = "SIGATP")
public class DiaDeTrabalho extends GenericModel implements Comparable<DiaDeTrabalho> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	public Long id;
	
	@Enumerated(EnumType.STRING)
	public DiaDaSemana diaEntrada;
	
	@Required
//	@As(binder=HourMinuteBinder.class)
	public Calendar horaEntrada;
	
	@Enumerated(EnumType.STRING)
	public DiaDaSemana diaSaida;
	
	@Required
//	@As(binder=HourMinuteBinder.class)
	public Calendar horaSaida;
	
	@NotNull
	@ManyToOne
	public EscalaDeTrabalho escalaDeTrabalho;
	
	public DiaDeTrabalho() {
		Inicializar();
	}
	
	private void Inicializar() {
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		this.id = new Long(0);
		this.diaEntrada = DiaDaSemana.SEGUNDA;
		this.diaSaida = DiaDaSemana.SEGUNDA;
		this.horaEntrada = Calendar.getInstance();
		this.horaSaida = Calendar.getInstance();
		try {
			this.horaEntrada.setTime(formatar.parse("01/01/1900 11:00"));
			this.horaSaida.setTime(formatar.parse("01/01/1900 19:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(Messages.get("diaDeTrabalho.inicializar.exception"),e);
		}
		escalaDeTrabalho = null;
		
	}

	public DiaDeTrabalho(EscalaDeTrabalho escala) {
		Inicializar();
		escalaDeTrabalho = escala;
	}
	
	public String getHoraEntradaFormatada() {
		SimpleDateFormat formatar = new SimpleDateFormat("HH:mm");
		return formatar.format(horaEntrada.getTime());
	}
	
	public String getHoraSaidaFormatada() {
		SimpleDateFormat formatar = new SimpleDateFormat("HH:mm");
		return formatar.format(horaSaida.getTime());
	}
	
	public String getHoraEntradaFormatada(String formato) {
		SimpleDateFormat formatar = new SimpleDateFormat(formato);
		return formatar.format(horaEntrada.getTime());
	}
	
	
	
	public String getHoraSaidaFormatada(String formato) {
		SimpleDateFormat formatar = new SimpleDateFormat(formato);
		return formatar.format(horaSaida.getTime());
	}
	
	@Override
	public int compareTo(DiaDeTrabalho o) {
		return (this.diaEntrada.getOrdem() + this.getHoraEntradaFormatada("HHmm")).compareTo((o.diaEntrada.getOrdem() +  o.getHoraSaidaFormatada("HHmm")));
	}
	
	@Override
	public String toString() {
		StringBuffer saida = new StringBuffer();
		saida.append(diaEntrada.getNomeAbreviado() + " " + getHoraEntradaFormatada() + " / ");
		if(!diaEntrada.equals(diaSaida))
		{
			saida.append(diaSaida.getNomeAbreviado() + " ");
		}
		
		saida.append(getHoraSaidaFormatada());
		return saida.toString();
	}
	
}
