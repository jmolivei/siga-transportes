package br.gov.jfrj.siga.tp.vraptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import play.data.binding.As;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.tp.model.Afastamento;
import br.gov.jfrj.siga.tp.model.DiaDaSemana;
import br.gov.jfrj.siga.tp.model.DiaDeTrabalho;
import br.gov.jfrj.siga.tp.model.EscalaDeTrabalho;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.Plantao;
import br.gov.jfrj.siga.tp.model.ServicoVeiculo;
import br.gov.jfrj.siga.vraptor.SigaObjects;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Resource
@Path("/app/relatorio")
public class RelatorioController extends TpController {

    public RelatorioController(HttpServletRequest request, Result result,
            CpDao dao, Validator validator, SigaObjects so, EntityManager em) {
        super(request, result, dao, validator, so, em);
    }
    
    private static final int HORA_FINAL_EXPEDIENTE = 19;
    private static final int MINUTO_FINAL_EXPEDIENTE = 0;
    private static final int SEGUNDO_FINAL_EXPEDIENTE = 0;
    private static final int HORA_FINAL_DIA = 23;
    private static final int MINUTO_FINAL_DIA = 59;
    private static final int SEGUNDO_FINAL_DIA = 59;
    private static final int HORA_INICIAL_DIA = 0;
    private static final int MINUTO_INICIAL_DIA = 0;
    private static final int SEGUNDO_INICIAL_DIA = 0;

    
    public void listarAgendaPorCondutorNoProximoDia(@As(lang={"*"}, value={"dd/MM/yyyy"}) Calendar dataPesquisa,Long idCondutor) throws ParseException{
        dataPesquisa.add(Calendar.DAY_OF_MONTH, 1);
        listarAgendaPorCondutor(dataPesquisa,idCondutor);
    }
    
    @Path("/listarAgendaTodosCondutores")
    public void listarAgendaTodosCondutores() throws ParseException {
        result.forwardTo(RelatorioController.class).listarAgendaPorCondutor(Calendar.getInstance(),null);
    }
    
    @Path("/listarAgendaPorCondutor")
    public void listarAgendaPorCondutor(Calendar dataPesquisa,Long idCondutor) throws ParseException{

        String registros = "";

        Calendar dataHoraPesquisa = Calendar.getInstance();
        SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String strDataPesquisa=null;

        if (dataPesquisa != null) {
            dataHoraPesquisa = dataPesquisa;
        }

        strDataPesquisa = String.format("%02d",dataHoraPesquisa.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",dataHoraPesquisa.get(Calendar.MONTH) + 1) + "/" + String.format("%04d",dataHoraPesquisa.get(Calendar.YEAR));
        dataHoraPesquisa.setTime(formatar.parse(strDataPesquisa + " 00:00"));

        List<EscalaDeTrabalho>  escalas = EscalaDeTrabalho.buscarPorCondutores(idCondutor,strDataPesquisa);
        List<EscalaDeTrabalho> escalasFiltradas = filtrarPorOrgao(escalas, EscalaDeTrabalho.class);

        List<Afastamento>  afastamentos = Afastamento.buscarPorCondutores(idCondutor,strDataPesquisa);
        List<Afastamento> afastamentosFiltrados = filtrarPorOrgao(afastamentos, Afastamento.class);

        List<Plantao> plantoes = Plantao.buscarPorCondutores(idCondutor,strDataPesquisa);
        List<Plantao> plantoesFiltrados = filtrarPorOrgao(plantoes, Plantao.class);

        List<Missao> missoes = Missao.buscarPorCondutores(idCondutor,strDataPesquisa);
        List<Missao> missoesFiltradas = filtrarPorOrgao(missoes, Missao.class);

        String delim = "";
        //for (EscalaDeTrabalho escala : escalas) {
        for (EscalaDeTrabalho escala : escalasFiltradas) {

            SimpleDateFormat formatar1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String srtDataPesquisa = formatar1.format(dataHoraPesquisa.getTime());

            DiaDaSemana diaDePesquisa = DiaDaSemana.getDiaDaSemana(srtDataPesquisa);

            SimpleDateFormat formatoData1 = new SimpleDateFormat("yyyy,M,d,H,m,s");

            Calendar dataHoraInicioTemp = Calendar.getInstance();
            Calendar dataHoraFimTemp = recuperarDataEHora(escala.getDataVigenciaInicio(), HORA_FINAL_DIA, MINUTO_FINAL_DIA,SEGUNDO_FINAL_DIA);

            for (DiaDeTrabalho dia: escala.getDiasDeTrabalho()) {
                if (diaDePesquisa.isEquals(dia.getDiaEntrada())) {
                    dataHoraInicioTemp = recuperarDataEHora(dataHoraPesquisa,dia.getHoraEntrada().get(Calendar.HOUR_OF_DAY), dia.getHoraEntrada().get(Calendar.MINUTE),dia.getHoraEntrada().get(Calendar.SECOND));
                    if (diaDePesquisa.isEquals(dia.getDiaSaida())) {
                        dataHoraFimTemp = recuperarDataEHora(dataHoraPesquisa, dia.getHoraSaida().get(Calendar.HOUR_OF_DAY), dia.getHoraSaida().get(Calendar.MINUTE),dia.getHoraSaida().get(Calendar.SECOND));
                    } else {
                        dataHoraFimTemp = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
                    }

                    registros += delim;
                    registros += "[ \'" + "Escalas" + "\', \'" + escala.getCondutor().getNome() + "\', new Date(" + formatoData1.format(dataHoraInicioTemp.getTime()) + "), new Date(";
                    registros += formatoData1.format(dataHoraFimTemp.getTime()) + ") ]";
                    delim = ", ";
                }
            }
        }

        String registrosEscala=registros;
        //registros = gerarTimeLine(dataHoraPesquisa,registrosEscala, afastamentos, plantoes, missoes, new ArrayList<ServicoVeiculo>(), "condutor");
        registros = gerarTimeLine(dataHoraPesquisa,registrosEscala, afastamentosFiltrados, plantoesFiltrados, missoesFiltradas, new ArrayList<ServicoVeiculo>(), "condutor");

        //TODO  HD render
//        renderArgs.put("dataPesquisa", dataHoraPesquisa);
//        render(registros, idCondutor);
        result.include("dataPesquisa", dataHoraPesquisa);
        result.include("registros", registros);
        result.include("idCondutor", idCondutor);
        result.include("entidade", "Condutor");
    }
    
    public void listarAgendaPorVeiculo(Calendar dataPesquisa,Long idVeiculo) throws ParseException {

       String registros = "";

       Calendar dataHoraPesquisa = Calendar.getInstance();
       SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");

       String strDataPesquisa=null;

       if (dataPesquisa != null) {
           dataHoraPesquisa = dataPesquisa;
       }

       strDataPesquisa = String.format("%02d",dataHoraPesquisa.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",dataHoraPesquisa.get(Calendar.MONTH) + 1) + "/" + String.format("%04d",dataHoraPesquisa.get(Calendar.YEAR));
       dataHoraPesquisa.setTime(formatar.parse(strDataPesquisa + " 00:00"));

       List<Missao> missoes = Missao.buscarPorVeiculos(idVeiculo,strDataPesquisa);
       List<Missao> missoesFiltradas = filtrarPorOrgao(missoes, Missao.class);

       List<ServicoVeiculo> servicosVeiculos = ServicoVeiculo.buscarPorVeiculo(idVeiculo,strDataPesquisa);
       List<ServicoVeiculo> servicosFiltrados = filtrarPorOrgao(servicosVeiculos, ServicoVeiculo.class);

       //registros = gerarTimeLine(dataHoraPesquisa,"", new ArrayList<Afastamento>(),new ArrayList<Plantao>(), missoes, servicosVeiculos, "veiculo" );
       registros = gerarTimeLine(dataHoraPesquisa,"", new ArrayList<Afastamento>(),new ArrayList<Plantao>(), missoesFiltradas, servicosFiltrados, "veiculo" );

//       renderArgs.put("dataPesquisa", dataHoraPesquisa);
//       render(registros, idVeiculo);
       result.include("dataPesquisa", dataHoraPesquisa);
       result.include("registros", registros);
       result.include("idVeiculo", idVeiculo);
       result.include("entidade", "Veiculo");
   }
    
    private String gerarTimeLine(Calendar dataHoraPesquisa, String registros,
            List<Afastamento> afastamentos, List<Plantao> plantoes,
            List<Missao> missoes, List<ServicoVeiculo> servicosVeiculos, String entidade) {

        String delim = "";
        if (! registros.isEmpty())
            delim = ", ";

        for (Afastamento afastamento : afastamentos) {
            registros += delim;
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");

            registros += "[ \'" + "Afastamentos" + "\', \'" + afastamento.getCondutor().getNome() + "\', new Date(" ;

            if (afastamento.getDataHoraInicio().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    afastamento.getDataHoraInicio().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    afastamento.getDataHoraInicio().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    afastamento.getDataHoraInicio().after(dataHoraPesquisa)) {
                registros += formatoData.format(afastamento.getDataHoraInicio().getTime()) + "), new Date(";
            }
            else {
                Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
                registros += formatoData.format(dataHora.getTime()) + "), new Date(";
            }

            if (afastamento.getDataHoraFim() != null){
                if (afastamento.getDataHoraFim().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    afastamento.getDataHoraFim().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    afastamento.getDataHoraFim().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    afastamento.getDataHoraFim().after(dataHoraPesquisa)) {
                    registros += formatoData.format(afastamento.getDataHoraFim().getTime()) + ") ]";
                    delim = ", ";
                    continue;
                }
            }
            Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
            registros += formatoData.format(dataHora.getTime()) + ") ]";
            delim = ", ";
        }

        for (Plantao plantao : plantoes) {
            registros += delim;
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");

            registros += "[ \'" + "Plantoes" + "\', \'" + plantao.getCondutor().getNome() + "\', new Date(" ;

            if (plantao.getDataHoraInicio().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    plantao.getDataHoraInicio().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    plantao.getDataHoraInicio().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    plantao.getDataHoraInicio().after(dataHoraPesquisa)) {
                registros += formatoData.format(plantao.getDataHoraInicio().getTime()) + "), new Date(";
            }
            else {
                Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
                registros += formatoData.format(dataHora.getTime()) + "), new Date(";

            }

            if (plantao.getDataHoraFim() != null){
                if (plantao.getDataHoraFim().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    plantao.getDataHoraFim().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    plantao.getDataHoraFim().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    plantao.getDataHoraFim().after(dataHoraPesquisa)) {
                    registros += formatoData.format(plantao.getDataHoraFim().getTime()) + ") ]";
                    delim = ", ";
                    continue;
                }
            }
            Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
            registros += formatoData.format(dataHora.getTime()) + ") ]";
            delim = ", ";
        }

        for (ServicoVeiculo servicoVeiculo : servicosVeiculos) {
            registros += delim;
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");

            registros += "[ \'" + "Servicos" + "\', \'" + servicoVeiculo.getVeiculo().getPlaca() + "\', new Date(" ;

            if (servicoVeiculo.getDataHoraInicio().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    servicoVeiculo.getDataHoraInicio().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    servicoVeiculo.getDataHoraInicio().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    servicoVeiculo.getDataHoraInicio().after(dataHoraPesquisa)) {
                registros += formatoData.format(servicoVeiculo.getDataHoraInicio().getTime()) + "), new Date(";
            }
            else {
                Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
                registros += formatoData.format(dataHora.getTime()) + "), new Date(";

            }

            if (servicoVeiculo.getDataHoraFim() != null){
                if (servicoVeiculo.getDataHoraFim().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    servicoVeiculo.getDataHoraFim().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    servicoVeiculo.getDataHoraFim().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    servicoVeiculo.getDataHoraFim().after(dataHoraPesquisa)) {
                    registros += formatoData.format(servicoVeiculo.getDataHoraFim().getTime()) + ") ]";
                    delim = ", ";
                    continue;
                }
            }
            Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
            registros += formatoData.format(dataHora.getTime()) + ") ]";
            delim = ", ";
        }

        String label="condutor";
        for (Missao missao : missoes) {
            registros += delim;
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy,M,d,H,m,s");

            if (entidade.equals("condutor")) {
                label = missao.getSequence() + "-" + missao.getCondutor().getNome();
            }
            else {
                label = missao.getSequence() + "-" + missao.getVeiculo().getPlaca();
            }

            registros += "[ \'" + "Missoes" + "\', \'" + label + "\', new Date(" ;

            if (missao.getDataHoraSaida().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    missao.getDataHoraSaida().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    missao.getDataHoraSaida().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    missao.getDataHoraSaida().after(dataHoraPesquisa)) {
                registros += formatoData.format(missao.getDataHoraSaida().getTime()) + "), new Date(";
            }
            else {
                Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_INICIAL_DIA, MINUTO_INICIAL_DIA,SEGUNDO_INICIAL_DIA);
                registros += formatoData.format(dataHora.getTime()) + "), new Date(";

            }

            if (missao.getDataHoraRetorno() != null){
                if (missao.getDataHoraRetorno().get(Calendar.YEAR) == dataHoraPesquisa.get(Calendar.YEAR) &&
                    missao.getDataHoraRetorno().get(Calendar.MONTH) == dataHoraPesquisa.get(Calendar.MONTH) &&
                    missao.getDataHoraRetorno().get(Calendar.DAY_OF_MONTH) == dataHoraPesquisa.get(Calendar.DAY_OF_MONTH) &&
                    missao.getDataHoraRetorno().after(dataHoraPesquisa)) {
                    registros += formatoData.format(missao.getDataHoraRetorno().getTime()) + ") ]";
                    delim = ", ";
                    continue;
                }
            }
            Calendar dataHora = recuperarDataEHora(dataHoraPesquisa, HORA_FINAL_EXPEDIENTE, MINUTO_FINAL_EXPEDIENTE,SEGUNDO_FINAL_EXPEDIENTE);
            registros += formatoData.format(dataHora.getTime()) + ") ]";
            delim = ", ";
        }
        return registros;
    }
    
    private <T> List<T> filtrarPorOrgao(List<T> lista, final Class<T> classe) {
        List<T> listaFiltrada = Lists.newArrayList(Iterables.filter(lista, new Predicate<T>() {
                public boolean apply(T objeto) {
                    if (classe.equals(Plantao.class)) {
                        Plantao obj = (Plantao)objeto;
                        return obj.getCondutor().getCpOrgaoUsuario().getId().equals(getTitular().getOrgaoUsuario().getId());
                    }
                    else if(classe.equals(Afastamento.class)) {
                        Afastamento obj = (Afastamento)objeto;
                        return obj.getCondutor().getCpOrgaoUsuario().getId().equals(getTitular().getOrgaoUsuario().getId());
                    }
                    else if(classe.equals(EscalaDeTrabalho.class)) {
                        EscalaDeTrabalho obj = (EscalaDeTrabalho)objeto;
                        return obj.getCondutor().getCpOrgaoUsuario().getId().equals(getTitular().getOrgaoUsuario().getId());
                    }
                    else if(classe.equals(ServicoVeiculo.class)) {
                        ServicoVeiculo obj = (ServicoVeiculo)objeto;
                        return obj.getCpOrgaoUsuario().getId().equals(getTitular().getOrgaoUsuario().getId());
                    }
                    else if(classe.equals(Missao.class)) {
                        Missao obj = (Missao)objeto;
                        return obj.getCpOrgaoUsuario().getId().equals(getTitular().getOrgaoUsuario().getId());
                    }

                    return false;
                }
            }
        ));

        return listaFiltrada;
    }

    private static Calendar recuperarDataEHora(Calendar dataHoraPesquisa, int hora, int minuto, int segundo) {
        Calendar dataHora = Calendar.getInstance();
        dataHora.set(Calendar.YEAR,  dataHoraPesquisa.get(Calendar.YEAR));
        dataHora.set(Calendar.MONTH,  dataHoraPesquisa.get(Calendar.MONTH));
        dataHora.set(Calendar.DAY_OF_MONTH,  dataHoraPesquisa.get(Calendar.DAY_OF_MONTH));
        dataHora.set(Calendar.HOUR_OF_DAY, hora);
        dataHora.set(Calendar.MINUTE,minuto);
        dataHora.set(Calendar.SECOND,segundo);
        return dataHora;
    }
}
