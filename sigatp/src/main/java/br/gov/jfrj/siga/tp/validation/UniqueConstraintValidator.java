package br.gov.jfrj.siga.tp.validation;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.vidageek.mirror.dsl.Mirror;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.model.TpModel;
import br.gov.jfrj.siga.tp.validation.annotation.Unique;

/**
 * Validador de campo "unique" em entidades. A anotacao {@link Unique} deve ser inserida na entidade o campo a ser validado informado. Monta a consulta dinanicamente realizando um count para verificar
 * a existencia de duplicidade de registro com o campo.
 * 
 * @author db1
 *
 */
public class UniqueConstraintValidator implements ConstraintValidator<Unique, TpModel> {
	private static final String QUERY_TEMPLATE = "SELECT count(t) FROM [MODEL_CLASS] t WHERE t.[FIELD] = :[FIELD]";
	private Unique unique;

	@Override
	public void initialize(Unique unique) {
		this.unique = unique;
	}

	@Override
	public boolean isValid(TpModel tpModel, ConstraintValidatorContext context) {
		Query query = criarConsultaParaUnique(tpModel);
		return contar(query, tpModel).equals(0L);
	}

	private Long contar(Query query, TpModel tpModel) {
		atribuirParametros(query, tpModel);
		return (Long) query.getSingleResult();
	}

	private void atribuirParametros(Query query, TpModel tpModel) {
		String field = unique.field();

		query.setParameter(field, new Mirror().on(tpModel).get().field(field));
		if (tpModel.getId() != null) {
			query.setParameter("id", tpModel.getId());
		}
	}

	private Query criarConsultaParaUnique(TpModel tpModel) {
		EntityManager em = ContextoPersistencia.em();

		String queryString = QUERY_TEMPLATE.replace("[MODEL_CLASS]", tpModel.getClass().getCanonicalName());
		queryString = queryString.replace("[FIELD]", unique.field());

		if (tpModel.getId() != null) {
			queryString += " AND t.id != :id ";
		}
		return em.createQuery(queryString);
	}
}