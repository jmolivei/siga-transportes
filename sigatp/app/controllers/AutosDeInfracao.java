package controllers;

import java.util.List;

import models.AutoDeInfracao;
import models.Condutor;
import models.ItemMenu;
import models.Penalidade;
import models.TipoDeNotificacao;
import models.Veiculo;
import uteis.CustomJavaExtensions;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import play.mvc.With;
import uteis.MenuMontador;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminMissao;
import controllers.AutorizacaoGI.RoleAdminMissaoComplexo;
import controllers.AutorizacaoGI.RoleAgente;

@With(AutorizacaoGI.class)
public class AutosDeInfracao extends Controller {

	public static void listarPorVeiculo(Long idVeiculo) {
		Veiculo veiculo = Veiculo.findById(idVeiculo);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.buscarAutosDeInfracaoPorVeiculo(veiculo);
		MenuMontador.instance().RecuperarMenuVeiculos(idVeiculo, ItemMenu.INFRACOES);
		render(autosDeInfracao, veiculo);
	}
	
	public static void listarPorCondutor(Long idCondutor) {
		Condutor condutor = Condutor.findById(idCondutor);
		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.buscarAutosDeInfracaoPorCondutor(condutor);
		MenuMontador.instance().RecuperarMenuCondutores(idCondutor, ItemMenu.INFRACOES);
		render(autosDeInfracao, condutor);
	}
	
	public static void listar() {
  		List<AutoDeInfracao> autosDeInfracao = AutoDeInfracao.listarOrdenado();
  		render(autosDeInfracao);
    }
		
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void incluir(String notificacao) throws Exception {
		renderVeiculosCondutoresEPenalidades();
		AutoDeInfracao autoDeInfracao = new AutoDeInfracao();
    	TipoDeNotificacao tipoNotificacao = TipoDeNotificacao.valueOf(notificacao);
		render(autoDeInfracao, tipoNotificacao);   
    }

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void editar(Long id) throws Exception {
		renderVeiculosCondutoresEPenalidades();
 		AutoDeInfracao autoDeInfracao = AutoDeInfracao.findById(id);
 		TipoDeNotificacao tipoNotificacao = autoDeInfracao.tipoDeNotificacao;
 		render(autoDeInfracao, tipoNotificacao);		
    }
	
	private static void renderVeiculosCondutoresEPenalidades() throws Exception {
		List<Veiculo> veiculos = Veiculo.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
    	List<Condutor> condutores = Condutor.listarTodos(AutorizacaoGI.titular().getOrgaoUsuario());
    	List<Penalidade> penalidades = Penalidade.listarTodos();
    	RenderArgs.current().put("veiculos", veiculos);
    	RenderArgs.current().put("condutores", condutores);
    	RenderArgs.current().put("penalidades", penalidades);
    }
    
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void salvar(@Valid AutoDeInfracao autoDeInfracao) throws Exception{
        if (autoDeInfracao.dataDePagamento != null && autoDeInfracao.dataPosteriorDataCorrente(autoDeInfracao.dataDePagamento)) {
			Validation.addError("dataPagamento", "autosDeInfracao.dataDePagamento.validation");
        }
        
		if(Validation.hasErrors()){
			renderVeiculosCondutoresEPenalidades();
			String template = autoDeInfracao.id > 0 ? "AutosDeInfracao/editar.html" : "AutosDeInfracao/incluir.html";
            renderTemplate(template, autoDeInfracao);
        }
        else{
        	autoDeInfracao.save();
        	listar();
        }
    }              

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	public static void excluir(Long id) {
	   AutoDeInfracao autoDeInfracao = AutoDeInfracao.findById(id);
	   autoDeInfracao.delete();
	   listar();
    }
	
	/* Método AJAX */
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void listarValorPenalidade(Long idPenalidade) throws Exception {
		Penalidade penalidade = Penalidade.findById(idPenalidade);		
		String formataMoedaBrasileiraSemSimbolo = CustomJavaExtensions.formataMoedaBrasileiraSemSimbolo(penalidade.valor);		
		renderText(formataMoedaBrasileiraSemSimbolo);
	}
	
	
	/* Método AJAX */
	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAgente
	public static void listarClassificacaoPenalidade(Long idPenalidade) throws Exception {
		Penalidade penalidade = Penalidade.findById(idPenalidade);	
		renderText(penalidade.classificacao.getDescricao());
	}
	
	
}