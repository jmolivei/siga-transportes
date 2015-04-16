package br.gov.jfrj.siga.tp.validation;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.sql.DataSource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.vidageek.mirror.dsl.Mirror;
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
	private static final String SIGA_TP_DS = "java:/jboss/datasources/SigaTpDS";
	private static final String QUERY_TEMPLATE = "SELECT count(*) FROM [MODEL_CLASS] t WHERE t.[FIELD] = ?";
	private Unique unique;

	@Override
	public void initialize(Unique unique) {
		this.unique = unique;
	}

	@Override
	public boolean isValid(TpModel tpModel, ConstraintValidatorContext context) {
		Connection connection = dataBaseConnection();
		try {
			PreparedStatement statement = connection.prepareStatement(criarConsultaParaUnique(tpModel));
			return contar(statement, tpModel).equals(0L);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Long contar(PreparedStatement statement, TpModel tpModel) throws SQLException {
		atribuirParametros(statement, tpModel);
		ResultSet resultSet = statement.executeQuery();
		resultSet.next();
		return resultSet.getLong(1);
	}

	private void atribuirParametros(PreparedStatement statement, TpModel tpModel) throws SQLException {
		String field = unique.field();
		statement.setObject(1, new Mirror().on(tpModel).get().field(field));
		if (tpModel.getId() != null) {
			statement.setObject(2, tpModel.getId());
		}
	}

	private String criarConsultaParaUnique(TpModel tpModel) {
		String queryString = QUERY_TEMPLATE.replace("[MODEL_CLASS]", obterNomeTabela(tpModel));
		queryString = queryString.replace("[FIELD]", obterNomeColuna(tpModel));

		if (tpModel.getId() != null) {
			queryString += " AND t.id != ? ";
		}
		return queryString;
	}

	private CharSequence obterNomeColuna(TpModel tpModel) {
		Field field = new Mirror().on(tpModel.getClass()).reflect().field(unique.field());
		Column column = field.getAnnotation(Column.class);

		if (column != null && column.name() != null && !column.name().isEmpty()) {
			return column.name();
		}
		return unique.field();
	}

	private String obterNomeTabela(TpModel tpModel) {
		Table table = tpModel.getClass().getAnnotation(Table.class);
		if (table != null && table.name() != null && !table.name().isEmpty()) {
			return table.name();
		}
		return tpModel.getClass().getSimpleName();
	}

	private Connection dataBaseConnection() {
		try {
			DataSource dataSource = (DataSource) new InitialContext().lookup(SIGA_TP_DS);
			return dataSource.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}