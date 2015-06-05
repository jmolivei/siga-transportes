<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization', 'version':'1','packages':['timeline']}]}"/>

<script type="text/javascript">
  google.load("search", "1");
  google.load("jquery", "1.4.2");
  google.load("jqueryui", "1.7.2");
</script>

<script type="text/javascript">
	$(function() {
	   $('#inputDataPesquisa').change( function() {	
		   dataalterada = $('#inputDataPesquisa').val();
		   if("${entidade}" == "Condutor") {
		      controller = "${linkTo[RelatorioController].listarAgendaPorCondutor['dataatrocar'][idCondutor]}";
		      controller = controller.replace("dataatrocar", dataalterada);
		   } else {
 			  controller = "${linkTo[RelatorioController].listarAgendaPorVeiculo['dataatrocar'][idVeiculo]}";
		      controller = controller.replace("dataatrocar", dataalterada);
		   }
 		   $(location).attr('href',controller);	
	   });
	});

	google.setOnLoadCallback(drawChart);
	
	function drawChart() {
	    var isIE10 = false;
	    /*@cc_on
	        if (/^10/.test(@_jscript_version)) {
	            isIE10 = true;
	        }
	    @*/
		
  		var container = document.getElementById('agendamentos');

  		if ("${registros}" != ""){
	  		var chart = new google.visualization.Timeline(container);
	  		var dataTable = new google.visualization.DataTable();
	
	  		dataTable.addColumn({ type: 'string', id: 'Agendamento' });
	  		dataTable.addColumn({ type: 'string', id: 'Nome' });
	  		dataTable.addColumn({ type: 'date', id: 'Inicio' });
	  		dataTable.addColumn({ type: 'date', id: 'Fim' });

			dataTable.addRows([
				${registros}
	    	]);

		    var options = {
		    		    timeline: { colorByRowLabel: true },
		    		    enableInteractivity: !isIE10
					  };

	        chart.draw(dataTable,options);
	        
		} else
			container.innerHTML = "<h3>N&atilde;o existem agendamentos para esta data.</h3>";
	}
</script>

<div class="gt-bd clearfix">
	<div class="gt-content clearfix">
	<h2>Relat&oacute;rio de Agendamentos por ${entidade} no dia 
	<c:choose>
		<c:when test="${entidade == 'Condutor'}">
			<a class="" href="${linkTo[RelatorioController].listarAgendaPorCondutorNoDiaAnterior[dataPesquisa][idCondutor]}"><img src="/sigatp/public/images/esquerda.png" alt="Retornar" title="Retornar"></a>
			<input type="text" id="inputDataPesquisa" size="10" readonly="readonly" class="datePicker" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${dataPesquisa.time}"/>" />
			<a class="" href="${linkTo[RelatorioController].listarAgendaPorCondutorNoProximoDia[dataPesquisa][idCondutor]}"><img src="/sigatp/public/images/direita.png" alt="Avancar" title="Avancar"></a>
		</c:when>
		<c:otherwise>
			<a class="" href="${linkTo[RelatorioController].listarAgendaPorVeiculoNoDiaAnterior[dataPesquisa][idVeiculo]}"><img src="/sigatp/public/images/esquerda.png" alt="Retornar" title="Retornar"></a>
			<input type="text" id="inputDataPesquisa" readonly="readonly" class="datePicker" value="<fmt:formatDate pattern="dd/MM/yyyy" value="${dataPesquisa.time}"/>" />
			<a class="" href="${linkTo[RelatorioController].listarAgendaPorVeiculoNoProximoDia[dataPesquisa][idVeiculo]}"><img src="/sigatp/public/images/direita.png" alt="Avancar" title="Avancar"></a>
		</c:otherwise>
	</c:choose>
	</h2>
		<div class="gt-content-box gt-for-table">
			<div style="height:600px" id="agendamentos" ></div>
		</div>	
	</div>
</div>