package models;

import java.io.File;
import java.util.Calendar;
import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

import org.apache.commons.io.IOUtils;

import play.Play;
import play.data.binding.As;
import play.modules.br.jus.jfrj.siga.uteis.validadores.validarAnoData.ValidarAnoData;

/*import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.validator.NotNull;

import play.data.validation.Required;
import play.db.jpa.GenericModel;
*/

//@SuppressWarnings("serial")
//@Entity
//@Audited
//@Table(schema = "SIGATP")
public class Imagem { //extends GenericModel {
	//@Id
	//@GeneratedValue
	//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator") @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName="SIGATP.hibernate_sequence") 
	//public Long id;
	
	//@Required
	@As(lang={"*"}, value={"dd/MM/yyyy HH:mm"})
	@ValidarAnoData(descricaoCampo="Data/Hora")
	public Calendar dataHora;
	
	//@Required
	//@NotNull
	public byte[] blob;
	
	//@Required
	//@NotNull
	public String nomeArquivo;

	//@Required
	//@NotNull
	public String mime;
	
	public Imagem() {
		//this.id = new Long(0);
	}
	
	public Imagem(File file, Calendar dataHora) {
		try {
			//this.id = id;
			this.dataHora = dataHora;
			this.blob = IOUtils.toByteArray(new FileInputStream(file));
			this.nomeArquivo = file.getName();
			this.mime = new javax.activation.MimetypesFileTypeMap().getContentType(file);
		} catch (IOException ioe) {
		}
	}
	
	/* Garante que a Imagem não seja instanciada sem que tenha sido
	   selecionado um arquivo no form, para evitar um registro sem conteúdo no banco */
	public static Imagem newInstance(File file) {
		if (file != null) {
			return new Imagem(file, Calendar.getInstance());
		}
		else {
			return null;
		}
	}
	
	public static boolean tamanhoImagemAceito(int tamanho) {
		int valorMaxMBConfigurado = Integer.parseInt(Parametro.buscarConfigSistemaEmVigor("imagem.filesize"));		
		final int valor1MB = 1048576;  
		int valorMaximo = valorMaxMBConfigurado * valor1MB;
		
		if (tamanho <= valorMaximo) {
			return true;
		}
		
		return false;
	}
}