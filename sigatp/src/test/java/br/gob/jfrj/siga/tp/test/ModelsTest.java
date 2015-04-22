package br.gob.jfrj.siga.tp.test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.model.Abastecimento;
import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.AutoDeInfracao;
import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.CategoriaCNH;
import br.gov.jfrj.siga.tp.model.Condutor;
import br.gov.jfrj.siga.tp.model.Cor;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.Fornecedor;
import br.gov.jfrj.siga.tp.model.Gravidade;
import br.gov.jfrj.siga.tp.model.Grupo;
import br.gov.jfrj.siga.tp.model.LotacaoVeiculo;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.NivelDeCombustivel;
import br.gov.jfrj.siga.tp.model.Plantao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TipoDeCombustivel;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;

public class ModelsTest {

	//TODO Se precisar mudar visibilidade
	protected static Fornecedor fornecedor3 = new Fornecedor();
	protected static Veiculo veiculo1 = new Veiculo();
	protected static Veiculo veiculo2 = new Veiculo();
	protected static Grupo grupo1 = new Grupo();
	protected static Grupo grupo2 = new Grupo();
	protected static Grupo grupo3 = new Grupo();
	protected static Grupo grupo4 = new Grupo();
	protected static Grupo grupo5 = new Grupo();
	protected static Grupo grupo6 = new Grupo();
	protected static Grupo grupo7 = new Grupo();

	protected static Cor cor1 = new Cor();
	protected static Cor cor2 = new Cor();
	protected static LotacaoVeiculo lotacoesVeiculo1 = new LotacaoVeiculo();
	protected static LotacaoVeiculo lotacoesVeiculo2 = new LotacaoVeiculo();
	protected static LotacaoVeiculo lotacoesVeiculo3 = new LotacaoVeiculo();
	protected static LotacaoVeiculo lotacoesVeiculo4 = new LotacaoVeiculo();
	protected static Abastecimento abastecimento = new Abastecimento();
	protected static Avaria avaria = new Avaria();
	protected static Condutor condutor1 = new Condutor();
	protected static Condutor condutor2 = new Condutor();
	protected static AutoDeInfracao autoDeInfracao1 = new AutoDeInfracao();
	protected static Afastamento afastamento1 = new Afastamento();
	protected static Plantao plantao1 = new Plantao();
	protected static RequisicaoTransporte requisicaoTransporte1 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte2 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte3 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte4 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte5 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte6 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte7 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte8 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte9 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte10 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte11 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte12 = new RequisicaoTransporte();
	protected static RequisicaoTransporte requisicaoTransporte13 = new RequisicaoTransporte();
	protected static Andamento andamento1 = new Andamento();
	protected static Andamento andamento2 = new Andamento();
	protected static Andamento andamento3 = new Andamento();
	protected static Andamento andamento4 = new Andamento();
	protected static Andamento andamento5 = new Andamento();
	protected static Andamento andamento6 = new Andamento();
	protected static Andamento andamento7 = new Andamento();
	protected static Andamento andamento8 = new Andamento();
	protected static Andamento andamento9 = new Andamento();
	protected static Andamento andamento10 = new Andamento();
	protected static Andamento andamento11 = new Andamento();
	protected static Andamento andamento12 = new Andamento();
	protected static Andamento andamento13 = new Andamento();
	protected static Missao missao1 = new Missao();
	protected static Missao missao2 = new Missao();
	protected static Missao missao3 = new Missao();

	Session playSession;

	@Before
	public void prepararSessao() throws Exception {
		/*
		 * playSession = (Session) JPA.em().getDelegate(); CpDao.freeInstance(); CpDao.getInstance(playSession); HibernateUtil.setSessao(playSession);
		 * Cp.getInstance().getConf().limparCacheSeNecessario();
		 */
	}

	@Test
	public void deletaTabelas() {
		Afastamento.AR.deleteAll();
		Plantao.AR.deleteAll();
		AutoDeInfracao.AR.deleteAll();
		Abastecimento.AR.deleteAll();
		Avaria.AR.deleteAll();
		LotacaoVeiculo.AR.deleteAll();
		Veiculo.AR.deleteAll();
		Condutor.AR.deleteAll();
		Fornecedor.AR.deleteAll();
		Grupo.AR.deleteAll();
		Cor.AR.deleteAll();
		Missao.AR.deleteAll();
		RequisicaoTransporte.AR.deleteAll();
		Assert.assertTrue(true);

	}

	@Test
	public void incluiFornecedor() throws Exception {
		fornecedor3.setRazaoSocial("AZURRA PARIS VEICULOS LTDA - PARIS PENHA");
		fornecedor3.setCnpj("31861115000870");
		fornecedor3.setLogradouro("AVENIDA LOBO JUNIOR, N. 1408".toLowerCase());
		fornecedor3.setCep("21020120");
		fornecedor3.setEnderecoVirtual("www.grupoleauto.com.br");
		fornecedor3.setTelefone("2138361999");
		fornecedor3.save();
		// fornecedor3.refresh();

		Fornecedor fornecedor1 = Fornecedor.AR.findById(fornecedor3.getId());
		Assert.assertEquals(fornecedor1.getCnpj(), fornecedor3.getCnpj());
		Assert.assertEquals(fornecedor1.getRazaoSocial(), fornecedor3.getRazaoSocial());
		Assert.assertEquals(fornecedor1.getLogradouro(), fornecedor3.getLogradouro().toUpperCase());

	}

	@Test
	public void incluiGrupoVeiculo() throws Exception {

		grupo1.setNome("VEÍCULO DE REPRESENTAÇÃO");
		grupo1.setFinalidade("TRANSPORTE DOS PRESIDENTES, DOS VICE-PRESIDENTES E DOS CORREGEDORES DOS TRIBUNAIS REGIONAIS FEDERAIS");
		grupo1.setCaracteristicas("VEÍCULOS DE MÉDIO PORTE, TIPO SEDAN, COR PRETA, COM CAPACIDADE DE TRANSPORTE DE ATÉ 5 (CINCO) PASSAGEIROS" + ", MOTOR DE POTÊNCIA MÍNIMA"
				+ "DE 116 CV E MÁXIMA DE 159 CV (GASOLINA) E ITENS DE SEGURANÇA CONDIZENTES COM" + " O SERVIÇO");
		grupo1.setLetra("A");
		grupo1.save();
		grupo1.refresh();
		Assert.assertNotSame(new Long(0), grupo1.getId());

		grupo2.setNome("VEÍCULO DE TRANSPORTE INSTITUCIONAL");
		grupo2.setFinalidade("TRANSPORTE, EM OBJETO DE SERVIÇO, DOS JUÍZES DE SEGUNDO GRAU E" + " DOS JUÍZES DIRETORES DE FORO E DIRETORES DE SUBSEÇÕES JUDICIÁRIAS");
		grupo2.setCaracteristicas("VEÍCULOS DE MÉDIO PORTE, TIPO SEDAN, COR PRETA, COM CAPACIDADE DE TRANSPORTE DE ATÉ 5 (CINCO) PASSAGEIROS" + ", MOTOR DE POTÊNCIA MÍNIMA"
				+ "DE 116 CV E MÁXIMA DE 159 CV (GASOLINA) E ITENS DE SEGURANÇA CONDIZENTES COM" + " O SERVIÇO");
		grupo2.setLetra("B");
		grupo2.save();
		grupo2.refresh();
		Assert.assertNotSame(new Long(0), grupo2.getId());

		grupo3.setNome("VEÍCULO DE SERVIÇO COMUM");
		grupo3.setFinalidade("TRANSPORTE, EM OBJETO DE SERVIÇO, DE JUÍZES DE PRIMEIRO GRAU E" + " SERVIDORES NO DESEMPENHO DE ATIVIDADES EXTERNAS DE INTERESSE DA ADMINISTRAÇÃO");
		grupo3.setCaracteristicas("VEÍCULOS DE PEQUENO PORTE, COM CAPACIDADE DE ATÉ 5 (CINCO)" + " OCUPANTES, MOTOR COM POTÊNCIA MÍNIMA DE 80 CV E MÁXIMA DE 112 CV (GASOLINA)"
				+ "E ITENS DE SEGURANÇA CONDIZENTES COM O SERVIÇO");
		grupo3.setLetra("C");
		grupo3.save();
		grupo3.refresh();
		Assert.assertNotSame(new Long(0), grupo3.getId());

		grupo4.setNome("VEÍCULO DE TRANSPORTE COLETIVO E DE APOIO ÀS ATIVIDADES JUDICIÁRIAS");
		grupo4.setFinalidade("transporte, em objeto de serviço, de magistrados e servidores no" + " desempenho de atividades externas de interesse da administração, aí incluído o"
				+ "funcionamento dos juizados especiais federais itinerantes".toUpperCase());
		grupo4.setCaracteristicas("pick-ups cabine dupla, vans com capacidade mínima de 12" + " (doze) ocupantes, microônibus e ônibus, motor com potência condizente com o" + " serviço".toUpperCase());
		grupo4.setLetra("D");
		grupo4.save();
		grupo4.refresh();
		Assert.assertNotSame(new Long(0), grupo4.getId());

		grupo5.setNome("Veículo de transporte de carga leve".toUpperCase());
		grupo5.setFinalidade("transporte de cargas leves no desempenho de atividades externas" + " de interesse da administração".toUpperCase());
		grupo5.setCaracteristicas("furgões, pick-ups de cabine simples, reboques e semireboques," + " motor de potência condizente com o serviço".toUpperCase());
		grupo5.setLetra("E");
		grupo5.save();
		grupo5.refresh();
		Assert.assertNotSame(new Long(0), grupo5.getId());

		grupo6.setNome("Veículo de transporte de carga pesada".toUpperCase());
		grupo6.setFinalidade("transporte de cargas pesadas".toUpperCase());
		grupo6.setCaracteristicas("veículos tipo caminhão, motor de potência condizente com o serviço".toUpperCase());
		grupo6.setLetra("F");
		grupo6.save();
		grupo6.refresh();
		Assert.assertNotSame(new Long(0), grupo6.getId());

		grupo7.setNome("Veículo de serviço de apoio especial".toUpperCase());
		grupo7.setFinalidade("atendimento, em caráter de socorro médico ou de apoio às" + "atividades de segurança, a magistrados e servidores".toUpperCase());
		grupo7.setCaracteristicas("ambulâncias e veículos com dispositivo de alarme e luz" + " vermelha intermitente, motor de potência condizente com o serviço".toUpperCase());
		grupo7.setLetra("G");
		grupo7.save();
		grupo7.refresh();
		Assert.assertNotSame(new Long(0), grupo7.getId());
	}

	@Test
	public void incluiCor() throws Exception {

		cor1 = new Cor();
		cor1.setNome("Azul");
		cor1.save();
		cor1.refresh();
		Assert.assertNotSame(new Long(0), cor1.getId());

		cor2 = new Cor();
		cor2.setNome("Prata");
		cor2.save();
		cor2.refresh();
		Assert.assertNotSame(new Long(0), cor1.getId());
	}

	@Test
	public void incluiVeiculo() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(Long.valueOf(3));

		veiculo1.setPlaca("LTF4444");
		veiculo1.setCategoriaCNH(CategoriaCNH.AB);
		veiculo1.setFornecedor(fornecedor3);
		veiculo1.setChassi("3N1BC1ADXDK197641");
		veiculo1.setTipoDeCombustivel(TipoDeCombustivel.ALCOOL);
		veiculo1.setCor(cor1);
		veiculo1.setDirecao("ELETRICA");
		veiculo1.setGrupo(grupo1);
		veiculo1.setLotacoes(null);
		veiculo1.setMarca("CHEVROLET");
		veiculo1.setModelo("CELTA");
		veiculo1.setMotor("1.6");
		veiculo1.setNumeroCartaoAbastecimento("11");
		veiculo1.setNumeroCartaoSeguro("22");
		veiculo1.setOutros("SOLEIRA ESPORTIVA");
		veiculo1.setPatrimonio("33333333333");
		veiculo1.setPotencia("100 cv");
		veiculo1.setPneuMedida("175");
		veiculo1.setPneuPressaoDianteira("28");
		veiculo1.setPneuPressaoTraseira("28");
		veiculo1.setProcessoAlienacao("2222222222222222");
		veiculo1.setRenavam("481563857");
		veiculo1.setTanque("60");
		veiculo1.setCpOrgaoUsuario(cpOrgaoUsuario);
		veiculo1.save();
		veiculo1.refresh();

		veiculo2.setPlaca("LTF3333");
		veiculo2.setCategoriaCNH(CategoriaCNH.AB);
		veiculo2.setFornecedor(fornecedor3);
		veiculo2.setChassi("3N1BC1ADXDK197641");
		veiculo2.setTipoDeCombustivel(TipoDeCombustivel.ALCOOL);
		veiculo2.setCor(cor1);
		veiculo2.setDirecao("ELETRICA");
		veiculo2.setGrupo(grupo1);
		veiculo2.setLotacoes(null);
		veiculo2.setMarca("CHEVROLET");
		veiculo2.setModelo("CRUIZE");
		veiculo2.setMotor("1.6");
		veiculo2.setNumeroCartaoAbastecimento("11");
		veiculo2.setNumeroCartaoSeguro("22");
		veiculo2.setOutros("SOLEIRA ESPORTIVA");
		veiculo2.setPatrimonio("33333333333");
		veiculo2.setPotencia("100 cv");
		veiculo2.setPneuMedida("175");
		veiculo2.setPneuPressaoDianteira("28");
		veiculo2.setPneuPressaoTraseira("28");
		veiculo2.setProcessoAlienacao("2222222222222222");
		veiculo2.setRenavam("481563857");
		veiculo2.setTanque("60");
		veiculo2.setCpOrgaoUsuario(cpOrgaoUsuario);
		veiculo2.save();
		veiculo2.refresh();

		List<DpLotacao> dpLotacoes = DpLotacao.AR.find("orgaoUsuario", cpOrgaoUsuario).fetch();
		LotacaoVeiculo lotacoesVeiculo1 = null;
		LotacaoVeiculo lotacoesVeiculo2 = null;

		lotacoesVeiculo3 = new LotacaoVeiculo();
		lotacoesVeiculo3.setLotacao(dpLotacoes.get(0));
		lotacoesVeiculo3.setVeiculo(veiculo2);
		lotacoesVeiculo3.setDataHoraInicio(new GregorianCalendar(2011, Calendar.OCTOBER, 11, 8, 0));
		lotacoesVeiculo3.setDataHoraFim(new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 0));
		lotacoesVeiculo4 = new LotacaoVeiculo();
		lotacoesVeiculo4.setLotacao(dpLotacoes.get(1));
		lotacoesVeiculo4.setVeiculo(veiculo2);
		lotacoesVeiculo4.setDataHoraInicio(new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 1));
		lotacoesVeiculo4.setDataHoraFim(null);

		lotacoesVeiculo3.save();
		lotacoesVeiculo4.save();
		lotacoesVeiculo3.refresh();
		lotacoesVeiculo4.refresh();

		Assert.assertNotSame(new Long(0), veiculo2.getId());
		Assert.assertNotSame(new Long(0), lotacoesVeiculo3.getId());
		Assert.assertNotSame(new Long(0), lotacoesVeiculo4.getId());

		lotacoesVeiculo1 = new LotacaoVeiculo();
		lotacoesVeiculo1.setLotacao(dpLotacoes.get(0));
		lotacoesVeiculo1.setVeiculo(veiculo1);
		lotacoesVeiculo1.setDataHoraInicio(new GregorianCalendar(2011, Calendar.OCTOBER, 11, 8, 0));
		lotacoesVeiculo1.setDataHoraFim(new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 0));
		lotacoesVeiculo2 = new LotacaoVeiculo();
		lotacoesVeiculo2.setLotacao(dpLotacoes.get(1));
		lotacoesVeiculo2.setVeiculo(veiculo1);
		lotacoesVeiculo2.setDataHoraInicio(new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 1));
		lotacoesVeiculo2.setDataHoraFim(null);

		lotacoesVeiculo1.save();
		lotacoesVeiculo2.save();
		lotacoesVeiculo1.refresh();
		lotacoesVeiculo2.refresh();
	}

	@Test
	public void incluiAvaria() throws Exception {
		avaria.setDescricao("Batida de frente");
		avaria.setDataDeRegistro(Calendar.getInstance());
		avaria.setVeiculo(veiculo1);
		avaria.save();
		avaria.refresh();
		Assert.assertNotSame(new Long(0), avaria.getId());
	}

	@Test
	public void incluiCondutor() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(new Long(3));
		List<DpPessoa> listaPossiveisCondutores = Condutor.getPossiveisCondutores(cpOrgaoUsuario);

		condutor1.setCategoriaCNH(CategoriaCNH.E);
		condutor1.setDataVencimentoCNH(Calendar.getInstance());
		condutor1.setDpPessoa(listaPossiveisCondutores.get(1));
		condutor1.setTelefoneInstitucional("2132618888");
		condutor1.setCelularInstitucional("2177665544");
		condutor1.setTelefonePessoal("2122334455");
		condutor1.setCelularPessoal("2199887766");
		condutor1.save();
		condutor1.refresh();
		Assert.assertNotSame(new Long(0), condutor1.getId());

		condutor2 = new Condutor();
		condutor2.setCategoriaCNH(CategoriaCNH.D);
		condutor2.setDataVencimentoCNH(Calendar.getInstance());
		condutor2.setDpPessoa(listaPossiveisCondutores.get(2));
		condutor2.setTelefoneInstitucional("2132618888");
		condutor2.setCelularInstitucional("21776655443");
		condutor2.setTelefonePessoal("2122334455");
		condutor2.setCelularPessoal("21998877665");
		condutor2.save();
		condutor2.refresh();
		Assert.assertNotSame(new Long(0), condutor2.getId());

	}

	@Test
	public void incluiAbastecimento() throws Exception {
		abastecimento.setDataHora(Calendar.getInstance());
		abastecimento.setFornecedor(fornecedor3);
		abastecimento.setVeiculo(veiculo1);
		abastecimento.setNivelDeCombustivel(NivelDeCombustivel.E);
		abastecimento.setPrecoPorLitro(3.19d);
		abastecimento.setValorTotalDaNotaFiscal(120d);
		abastecimento.setNumeroDaNotaFiscal("123456");
		abastecimento.setTipoDeCombustivel(TipoDeCombustivel.GASOLINA);
		abastecimento.setCondutor(condutor1);
		abastecimento.save();
		abastecimento.refresh();
		Assert.assertNotSame(new Long(0), abastecimento.getId());
	}

	@Test
	public void incluiAutoDeInfracao() throws Exception {
		autoDeInfracao1.setDataHora(new GregorianCalendar(2011, Calendar.OCTOBER, 11, 8, 0));
		autoDeInfracao1.setCodigoDaAutuacao("123456789012345");
		autoDeInfracao1.setCodigoDaPenalidade("123456789012345");
		autoDeInfracao1.setDescricao("xxxxxxxxxxxxx");
		autoDeInfracao1.setGravidade(Gravidade.GRAVISSIMA);
		autoDeInfracao1.setEnquadramento("xxxxxxxxxxxxx");
		autoDeInfracao1.setLocal("xxxxxxxxxxxxx");
		autoDeInfracao1.setFoiRecebido(PerguntaSimNao.SIM);
		autoDeInfracao1.setValor(500.00D);
		autoDeInfracao1.setValorComDesconto(450.00D);
		autoDeInfracao1.setDataDeVencimento(new GregorianCalendar(2011, Calendar.OCTOBER, 12, 8, 0));
		autoDeInfracao1.setDataDePagamento(new GregorianCalendar(2011, Calendar.OCTOBER, 12, 8, 0));
		autoDeInfracao1.setCondutor(condutor1);
		autoDeInfracao1.setVeiculo(veiculo1);
		autoDeInfracao1.save();
		autoDeInfracao1.refresh();
		Assert.assertNotSame(new Long(0), autoDeInfracao1.getId());
	}

	@Test
	public void incluiAfastamento() throws Exception {
		afastamento1.setCondutor(condutor1);
		afastamento1.setDescricao("Licença Nojo");
		afastamento1.setDataHoraInicio(new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 1));
		afastamento1.setDataHoraFim(new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 0));
		afastamento1.save();
		afastamento1.refresh();
		Assert.assertNotSame(new Long(0), afastamento1.getId());
	}

	@Test
	public void incluiPlantao() throws Exception {
		plantao1.condutor = condutor2;
		plantao1.dataHoraInicio = new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 1);
		plantao1.dataHoraFim = new GregorianCalendar(2012, Calendar.OCTOBER, 11, 8, 0);
		plantao1.save();
		plantao1.refresh();
		Assert.assertNotSame(new Long(0), plantao1.id);
	}

	@Test
	public void incluiRequisicoes() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(Long.valueOf(3));
		requisicaoTransporte1.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte1.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 11, 8, 1);
		requisicaoTransporte1.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 11, 13, 10);
		requisicaoTransporte1.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 11, 15, 10);
		requisicaoTransporte1.finalidade = "CURSO EXTERNO";
		requisicaoTransporte1.itinerarios = "RUA DO ACRE - AV.ALMEIRANTE BARROSO";
		requisicaoTransporte1.passageiros = "BAYLON, FERNANDO, PERALBA";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);

		requisicaoTransporte1.save();
		requisicaoTransporte1.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte1.id);

		andamento1.descricao = "Nova requisição";
		andamento1.dataAndamento = Calendar.getInstance();
		andamento1.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento1.requisicaoTransporte = requisicaoTransporte1;
		andamento1.save();

		requisicaoTransporte2.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte2.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte2.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 12, 13, 10);
		requisicaoTransporte2.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 12, 15, 10);
		requisicaoTransporte2.finalidade = "CONGRESSO EM BRASILIA";
		requisicaoTransporte2.itinerarios = "RUA DO ACRE - AEROPORTO SANTOS DUMONT";
		requisicaoTransporte2.passageiros = "PERALBA, DR.PAULO";

		requisicaoTransporte2.save();
		requisicaoTransporte2.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte2.id);

		andamento2.descricao = "Nova requisição";
		andamento2.dataAndamento = Calendar.getInstance();
		andamento2.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento2.requisicaoTransporte = requisicaoTransporte2;
		andamento2.save();

		requisicaoTransporte3.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte3.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte3.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 12, 13, 10);
		requisicaoTransporte3.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 12, 15, 10);
		requisicaoTransporte3.finalidade = "CONGRESSO EM FORTALEZA";
		requisicaoTransporte3.itinerarios = "RUA DO ACRE - AEROPORTO GALEAO";
		requisicaoTransporte3.passageiros = "ALBERTO, KATIA";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte3.save();
		requisicaoTransporte3.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte3.id);

		andamento3.descricao = "Nova requisição";
		andamento3.dataAndamento = Calendar.getInstance();
		andamento3.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento3.requisicaoTransporte = requisicaoTransporte3;
		andamento3.save();

		requisicaoTransporte7.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte7.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 11, 8, 1);
		requisicaoTransporte7.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 12, 13, 10);
		requisicaoTransporte7.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 12, 15, 10);
		requisicaoTransporte7.finalidade = "VOLTA PARA CASA";
		requisicaoTransporte7.itinerarios = "RUA DO ACRE - RUA URUGUAI";
		requisicaoTransporte7.passageiros = "FERNANDO";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte7.save();
		requisicaoTransporte7.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte7.id);

		andamento7.descricao = "Nova requisição";
		andamento7.dataAndamento = Calendar.getInstance();
		andamento7.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento7.requisicaoTransporte = requisicaoTransporte7;
		andamento7.save();

		requisicaoTransporte8.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte8.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte8.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 12, 13, 10);
		requisicaoTransporte8.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 12, 15, 10);
		requisicaoTransporte8.finalidade = "VOLTA PARA CASA";
		requisicaoTransporte8.itinerarios = "RUA DO ACRE - RUA VOLUNTARIOS DA PATRIA";
		requisicaoTransporte8.passageiros = "BAYLON,PERALBA";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte8.save();
		requisicaoTransporte8.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte8.id);

		andamento8.descricao = "Nova requisição";
		andamento8.dataAndamento = Calendar.getInstance();
		andamento8.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento8.requisicaoTransporte = requisicaoTransporte8;
		andamento8.save();

		requisicaoTransporte9.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte9.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte9.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 12, 13, 10);
		requisicaoTransporte9.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 12, 15, 10);
		requisicaoTransporte9.finalidade = "VOLTA PARA CASA";
		requisicaoTransporte9.itinerarios = "RUA DO ACRE - VILA DA PENHA";
		requisicaoTransporte9.passageiros = "ALBERTO, KATIA";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte9.save();
		requisicaoTransporte9.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte9.id);

		andamento9.descricao = "Nova requisição";
		andamento9.dataAndamento = Calendar.getInstance();
		andamento9.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento9.requisicaoTransporte = requisicaoTransporte9;
		andamento9.save();

		cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(Long.valueOf(2));
		requisicaoTransporte4.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte4.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 11, 8, 1);
		requisicaoTransporte4.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 11, 13, 10);
		requisicaoTransporte4.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 11, 15, 10);
		requisicaoTransporte4.finalidade = "CURSO TRF";
		requisicaoTransporte4.itinerarios = "AV.ALMEIRANTE BARROSO-RUA DO ACRE";
		requisicaoTransporte4.passageiros = "CAROL, DJALMA, ANDERSON";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte4.save();
		requisicaoTransporte4.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte4.id);

		andamento4.descricao = "Nova requisição";
		andamento4.dataAndamento = Calendar.getInstance();
		andamento4.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento4.requisicaoTransporte = requisicaoTransporte4;
		andamento4.save();

		requisicaoTransporte5.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte5.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte5.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 11, 13, 10);
		requisicaoTransporte5.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 11, 15, 10);
		requisicaoTransporte5.finalidade = "CURSO TRF";
		requisicaoTransporte5.itinerarios = "AV.ALMEIRANTE BARROSO-RUA DO ACRE";
		requisicaoTransporte5.passageiros = "VINICIUS, CARLINHOS";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte5.save();
		requisicaoTransporte5.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte5.id);

		andamento5.descricao = "Nova requisição";
		andamento5.dataAndamento = Calendar.getInstance();
		andamento5.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento5.requisicaoTransporte = requisicaoTransporte5;
		andamento5.save();

		requisicaoTransporte6.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte6.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte6.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 12, 13, 10);
		requisicaoTransporte6.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 12, 15, 10);
		requisicaoTransporte6.finalidade = "CONGRESSO EM FORTALEZA";
		requisicaoTransporte6.itinerarios = "AV.ALMEIRANTE BARROSO - AEROPORTO GALEAO";
		requisicaoTransporte6.passageiros = "DJALMA, CAROL";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte6.save();
		requisicaoTransporte6.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte6.id);

		andamento6.descricao = "Nova requisição";
		andamento6.dataAndamento = Calendar.getInstance();
		andamento6.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento6.requisicaoTransporte = requisicaoTransporte6;
		andamento6.save();

		cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(Long.valueOf(3));
		requisicaoTransporte11.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte11.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 12, 8, 1);
		requisicaoTransporte11.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 13, 13, 10);
		requisicaoTransporte11.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 13, 15, 10);
		requisicaoTransporte11.finalidade = "VOLTA PARA CASA";
		requisicaoTransporte11.itinerarios = "RUA DO ACRE - RUA URUGUAI";
		requisicaoTransporte11.passageiros = "FERNANDO";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte11.save();
		requisicaoTransporte11.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte11.id);

		andamento11.descricao = "Nova requisição";
		andamento11.dataAndamento = Calendar.getInstance();
		andamento11.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento11.requisicaoTransporte = requisicaoTransporte11;
		andamento11.save();

		requisicaoTransporte12.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte12.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 13, 8, 1);
		requisicaoTransporte12.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 13, 16, 10);
		requisicaoTransporte12.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 13, 17, 10);
		requisicaoTransporte12.finalidade = "VOLTA PARA CASA";
		requisicaoTransporte12.itinerarios = "RUA DO ACRE - RUA VOLUNTARIOS DA PATRIA";
		requisicaoTransporte12.passageiros = "BAYLON,PERALBA";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte12.save();
		requisicaoTransporte12.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte12.id);

		andamento12.descricao = "Nova requisição";
		andamento12.dataAndamento = Calendar.getInstance();
		andamento12.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento12.requisicaoTransporte = requisicaoTransporte12;
		andamento12.save();

		requisicaoTransporte13.cpOrgaoUsuario = cpOrgaoUsuario;
		requisicaoTransporte13.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 13, 8, 1);
		requisicaoTransporte13.dataHoraSaidaPrevista = new GregorianCalendar(2014, Calendar.JANUARY, 13, 17, 30);
		requisicaoTransporte13.dataHoraRetornoPrevisto = new GregorianCalendar(2014, Calendar.JANUARY, 13, 18, 30);
		requisicaoTransporte13.finalidade = "VOLTA PARA CASA";
		requisicaoTransporte13.itinerarios = "RUA DO ACRE - VILA DA PENHA";
		requisicaoTransporte13.passageiros = "ALBERTO, KATIA";
		requisicaoTransporte1.setSequence(cpOrgaoUsuario);
		requisicaoTransporte13.save();
		requisicaoTransporte13.refresh();
		Assert.assertNotSame(new Long(0), requisicaoTransporte13.id);

		andamento13.descricao = "Nova requisição";
		andamento13.dataAndamento = Calendar.getInstance();
		andamento13.estadoRequisicao = EstadoRequisicao.ABERTA;
		andamento13.requisicaoTransporte = requisicaoTransporte13;
		andamento13.save();
	}

	@Test
	public void autorizarRequisicao() throws Exception {
		requisicaoTransporte1 = RequisicaoTransporte.AR.findById(requisicaoTransporte1.id);
		requisicaoTransporte1.save();
		requisicaoTransporte1.refresh();

		andamento1 = new Andamento();
		andamento1.descricao = "Requisicao autorizada";
		andamento1.dataAndamento = Calendar.getInstance();
		andamento1.estadoRequisicao = EstadoRequisicao.AUTORIZADA;
		andamento1.requisicaoTransporte = requisicaoTransporte1;
		andamento1.save();

		requisicaoTransporte2 = RequisicaoTransporte.AR.findById(requisicaoTransporte2.id);
		requisicaoTransporte2.save();
		requisicaoTransporte2.refresh();

		andamento2 = new Andamento();
		andamento2.descricao = "Requisicao autorizada";
		andamento2.dataAndamento = Calendar.getInstance();
		andamento2.estadoRequisicao = EstadoRequisicao.AUTORIZADA;
		andamento2.requisicaoTransporte = requisicaoTransporte2;
		andamento2.save();

		requisicaoTransporte3 = RequisicaoTransporte.AR.findById(requisicaoTransporte3.id);
		requisicaoTransporte3.save();
		requisicaoTransporte3.refresh();

		andamento3 = new Andamento();
		andamento3.descricao = "Requisicao autorizada";
		andamento3.dataAndamento = Calendar.getInstance();
		andamento3.estadoRequisicao = EstadoRequisicao.AUTORIZADA;
		andamento3.requisicaoTransporte = requisicaoTransporte3;
		andamento3.save();
	}

	@Test
	public void rejeitarRequisicao() throws Exception {
		requisicaoTransporte7 = RequisicaoTransporte.AR.findById(requisicaoTransporte7.id);
		requisicaoTransporte7.save();
		requisicaoTransporte7.refresh();

		andamento7 = new Andamento();
		andamento7.descricao = "Requisicao rejeitada";
		andamento7.dataAndamento = Calendar.getInstance();
		andamento7.estadoRequisicao = EstadoRequisicao.REJEITADA;
		andamento7.requisicaoTransporte = requisicaoTransporte7;
		andamento7.save();

		requisicaoTransporte8 = RequisicaoTransporte.AR.findById(requisicaoTransporte8.id);
		requisicaoTransporte8.save();
		requisicaoTransporte8.refresh();

		andamento8 = new Andamento();
		andamento8.descricao = "Requisicao rejeitada";
		andamento8.dataAndamento = Calendar.getInstance();
		andamento8.estadoRequisicao = EstadoRequisicao.REJEITADA;
		andamento8.requisicaoTransporte = requisicaoTransporte8;
		andamento8.save();

		requisicaoTransporte9 = RequisicaoTransporte.AR.findById(requisicaoTransporte9.id);
		requisicaoTransporte9.save();
		requisicaoTransporte9.refresh();

		andamento9 = new Andamento();
		andamento9.descricao = "Requisicao rejeitada";
		andamento9.dataAndamento = Calendar.getInstance();
		andamento9.estadoRequisicao = EstadoRequisicao.REJEITADA;
		andamento9.requisicaoTransporte = requisicaoTransporte9;
		andamento9.save();
	}

	@Test
	public void incluiMissoes() throws Exception {
		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.findById(Long.valueOf(3));
		missao1.condutor = condutor1;
		missao1.cpOrgaoUsuario = cpOrgaoUsuario;
		missao1.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 31, 8, 1);
		missao1.dataHoraSaida = new GregorianCalendar(2014, Calendar.JANUARY, 31, 8, 1);
		missao1.dataHoraRetorno = new GregorianCalendar(2014, Calendar.JANUARY, 31, 10, 17);
		missao1.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		missao1.requisicoesTransporte.add(requisicaoTransporte1);
		missao1.requisicoesTransporte.add(requisicaoTransporte2);
		missao1.requisicoesTransporte.add(requisicaoTransporte3);
		missao1.responsavel = condutor1.getDpPessoa();
		missao1.veiculo = veiculo1;
		missao1.setSequence(1);
		missao1.save();

		missao2.condutor = condutor2;
		missao2.cpOrgaoUsuario = cpOrgaoUsuario;
		missao2.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 31, 15, 1);
		missao2.dataHoraSaida = new GregorianCalendar(2014, Calendar.JANUARY, 31, 17, 1);
		;
		missao2.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		missao2.requisicoesTransporte.add(requisicaoTransporte7);
		missao2.requisicoesTransporte.add(requisicaoTransporte8);
		missao2.requisicoesTransporte.add(requisicaoTransporte9);
		missao2.responsavel = condutor2.getDpPessoa();
		missao2.veiculo = veiculo2;
		missao2.setSequence(2);
		missao2.save();

		missao3.condutor = condutor2;
		missao3.cpOrgaoUsuario = cpOrgaoUsuario;
		missao3.dataHora = new GregorianCalendar(2014, Calendar.JANUARY, 30, 15, 1);
		missao3.dataHoraSaida = new GregorianCalendar(2014, Calendar.JANUARY, 30, 17, 1);
		;
		missao3.requisicoesTransporte = new ArrayList<RequisicaoTransporte>();
		missao3.requisicoesTransporte.add(requisicaoTransporte11);
		missao3.requisicoesTransporte.add(requisicaoTransporte12);
		missao3.requisicoesTransporte.add(requisicaoTransporte13);
		missao3.responsavel = condutor2.getDpPessoa();
		missao3.veiculo = veiculo2;
		missao3.setSequence(3);
		missao3.save();
	}
}
