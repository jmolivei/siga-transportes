package controllers;

import java.net.InetAddress;

import org.hibernate.envers.RevisionListener;

import br.gov.jfrj.siga.tp.model.RevInfo;
import play.mvc.With;

@With(AutorizacaoGIAntigo.class)
public class RevInfoListener implements RevisionListener {
	@Override
	public void newRevision(Object revisionEntity) {
		RevInfo entity = (RevInfo) revisionEntity;
		entity.setMatricula(AutorizacaoGIAntigo.cadastrante().getSiglaCompleta());
		entity.setMotivoLog(AutorizacaoGIAntigo.getMotivoLog());
		
		try {
			entity.setEnderecoIp(InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}