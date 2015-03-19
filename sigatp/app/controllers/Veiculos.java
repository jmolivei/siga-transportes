package controllers;

import java.util.Calendar;
import java.util.List;

import models.Grupo;
import models.ItemMenu;
import models.LotacaoVeiculo;
import models.Veiculo;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import play.mvc.With;
import uteis.Combo;
import uteis.MenuMontador;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import controllers.AutorizacaoGI.RoleAdmin;
import controllers.AutorizacaoGI.RoleAdminFrota;

@With(AutorizacaoGI.class)
public class Veiculos extends Controller {

	public static void listar() throws Exception {
	 	CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGI.titular().getOrgaoUsuario();
	 	List<Veiculo> veiculos = Veiculo.listarTodos(cpOrgaoUsuario);
		render(veiculos);
	}
	
	public static void listarAvarias(Long idVeiculo) {
		Avarias.montarListaDeAvariasPorVeiculo(idVeiculo);
		render("@Avarias.listarPorVeiculo");
	}
	
	@Before(priority=200,only={"incluir","editar", "salvar"})
	protected static void montarCombos() throws Exception {
       	renderArgs = Combo.montar(renderArgs,Combo.Cor,Combo.Fornecedor);
       	
       	List<Grupo> grupos = Grupo.listarTodos();
       	renderArgs.put(Combo.Grupo.getDescricao(), grupos);
       	
       	CpOrgaoUsuario cpOrgaoUsuario = AutorizacaoGI.titular().getOrgaoUsuario();
       	List<DpLotacao> dpLotacoes = DpLotacao.find("orgaoUsuario = ? and DATA_FIM_LOT is null order by NOME_LOTACAO", cpOrgaoUsuario).fetch();
       	renderArgs.put("dpLotacoes", dpLotacoes);
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void salvar(Veiculo veiculo) throws Exception {
		
		if(veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			Double odometroAnterior = veiculo.getUltimoOdometroDeLotacao();
			if(odometroAnterior > veiculo.odometroEmKmAtual) {
				Validation.addError("odometroEmKmAtual", "veiculo.odometroEmKmAtual.maiorAnterior.validation");
			}
			if(veiculo.odometroEmKmAtual.equals(new Double(0))) {
				Validation.addError("odometroEmKmAtual", "veiculo.odometroEmKmAtual.zero.validation");
			}
		}

		validation.valid(veiculo);
		redirecionarSeErroAoSalvar(veiculo);
		
		veiculo.cpOrgaoUsuario = AutorizacaoGI.titular().getOrgaoUsuario();
		veiculo.save();
		
		if(lotacaoDoVeiculoMudou(veiculo)) {
			LotacaoVeiculo.atualizarDataFimLotacaoAnterior(veiculo);
		}
		
		if(veiculoNaoTemLotacaoCadastrada(veiculo) || lotacaoDoVeiculoMudou(veiculo)) {
			LotacaoVeiculo novalotacao = new LotacaoVeiculo(null, veiculo, veiculo.lotacaoAtual, Calendar.getInstance(), null, veiculo.odometroEmKmAtual);
			novalotacao.save();	
		}
		
		listar();
	}

	private static boolean veiculoNaoTemLotacaoCadastrada(Veiculo veiculo) {
		return veiculo.lotacoes == null;
	}

	private static boolean lotacaoDoVeiculoMudou(Veiculo veiculo) {
		if(veiculo.lotacoes == null) {
			return true;
		}
		return (veiculo.lotacoes.size() > 0) && (!veiculo.lotacoes.get(0).lotacao.equivale(veiculo.lotacaoAtual));
	}
	
	private static void redirecionarSeErroAoSalvar(Veiculo veiculo) throws Exception {
		if(Validation.hasErrors()) 
		{
			montarCombos();
			MenuMontador.instance().RecuperarMenuVeiculos(veiculo.id, ItemMenu.DADOSCADASTRAIS);
			String template = veiculo.id > 0 ? "@editar" : "@incluir";
			if(veiculoNaoTemLotacaoCadastrada(veiculo) || veiculo.lotacoes.isEmpty() || (!veiculo.lotacoes.get(0).lotacao.equivale(veiculo.lotacaoAtual))) {
				RenderArgs.current().put("mostrarCampoOdometro", true);
			}
			renderTemplate(template, veiculo);
		}
	}
	
	@RoleAdmin
	@RoleAdminFrota
	public static void incluir(){
		Veiculo veiculo = new Veiculo();
		veiculo.lotacaoAtual = new DpLotacao();
		MenuMontador.instance().RecuperarMenuVeiculos(new Long(0), ItemMenu.DADOSCADASTRAIS);
		render(veiculo);
	}
	
	public static void buscarPeloId(Long Id) throws Exception {
		Veiculo veiculo = Veiculo.findById(Id);
		veiculo.configurarLotacaoAtual();
		//veiculo.configurarOdometroParaMudancaDeLotacao();
		montarCombos();
		MenuMontador.instance().RecuperarMenuVeiculos(Id, ItemMenu.DADOSCADASTRAIS);
		renderTemplate("@ler", veiculo);
	}
	

	@RoleAdmin
	@RoleAdminFrota
	public static void editar(Long id) {
		Veiculo veiculo = Veiculo.findById(id);
		veiculo.configurarLotacaoAtual();
		//veiculo.configurarOdometroParaMudancaDeLotacao();
		MenuMontador.instance().RecuperarMenuVeiculos(id, ItemMenu.DADOSCADASTRAIS);
		render(veiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	public static void excluir(Long id) throws Exception {
		Veiculo veiculo = Veiculo.findById(id);
		veiculo.delete();
		listar();
	}

	public static void emdesenvolvimento() {
		render();
	}
}
