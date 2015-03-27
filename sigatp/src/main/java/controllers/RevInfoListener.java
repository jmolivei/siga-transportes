package controllers;

import java.net.InetAddress;

import org.hibernate.envers.RevisionListener;

import br.gov.jfrj.siga.tp.model.RevInfo;
import play.mvc.With;

@With(AutorizacaoGI.class)
public class RevInfoListener implements RevisionListener {
	@Override
	public void newRevision(Object revisionEntity) {
		RevInfo entity = (RevInfo) revisionEntity;
		entity.setMatricula(AutorizacaoGI.cadastrante().getSiglaCompleta());
		entity.setMotivoLog(AutorizacaoGI.getMotivoLog());
		
		try {
			entity.setEnderecoIp(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}