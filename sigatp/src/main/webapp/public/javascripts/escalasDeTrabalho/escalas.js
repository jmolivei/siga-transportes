var escalas = {};

escalas.componentes = function(){
	escalas.salvar = document.getElementById('salvar');
	escalas.finalizar = document.getElementById('finalizar');
	escalas.cancelar = document.getElementById('cancelar');
	escalas.incluir = document.getElementById("btn-Incluir-DiasDeTrabalho");
	escalas.formulario = document.getElementById('formEscalasDeTrabalho');
	escalas.exluir = document.getElementsByName('linkExcluirSelecionados');
};

escalas.init = function(){
	escalas.componentes();
	
	escalas.salvar.addEventListener("click", function(){
			escalas.submitForm(urlSalvar);
		});

	escalas.finalizar.addEventListener('click', function(){
			escalas.submitForm(urlFinalizar);
		});

	escalas.cancelar.addEventListener('click', function(){
			window.location = urlCancelar;
		});

	escalas.incluir.addEventListener('click', function(){
			var divrow = document.getElementById("rowDiasDeTrabalho");
			var htmlNaoSelecionado = divrow.innerHTML;
			var htmlSelecionado = htmlNaoSelecionado.replace(/naoSelecionado/g, "selecionado");
			console.log(htmlSelecionado);
			var html = '<tr>';
			html = html	+ '<th width="15%" class="obrigatorio">Dia Inicio / Fim :</th>';
			html = html + '<td>';
			html = html + htmlSelecionado;
			html = html + '</td>';
			html = html	+ '<td width="8%" ><a class="linkExcluir" name="linkExcluirSelecionados" onclick="escalas.apagaLinha(this)" style="display:inline" href="#">Excluir</a></td>';
			html = html + '</tr>';

			$("#htmlgridDiasDeTrabalho tbody").append(html);
		});
};

escalas.submitForm = function(acao){
	escalas.formulario.setAttribute('action', acao);
	escalas.formulario.setAttribute('method', 'POST');
	var idxSelect = 0;
	var isSelect = false;
	var x = 0;
	var inputsDiaDeTrabalho = $('.selecionado');
	for (var i = 0; i < inputsDiaDeTrabalho.length; i++) {
		var nome = inputsDiaDeTrabalho[i].name;
		if(nome === undefined) {
			isSelect = true;
			nome = $(inputsDiaDeTrabalho[i]).parent()[idxSelect].name;
		}
		// 5 - Número de colunas renomeadas por linha de dia de trabalho
		// x - Indice do array de novaEscala.diasDeTrabalho[x] a ser enviado para o servidor
		x = ~~(i / 5);
		var nomeCompleto = "novaEscala.diasDeTrabalho[" + x + "]." + nome;
		if(isSelect) {
			isSelect = false;
			$(inputsDiaDeTrabalho[i]).parent()[idxSelect].setAttribute("name", nomeCompleto);
		} else {
			inputsDiaDeTrabalho[i].setAttribute("name", nomeCompleto);
		}
	}

	var tbodydiadetrabalho = document.getElementById("tbody");
	var tform = document.getElementById("formEscalasDeTrabalho");
	escalas.formulario.submit();
};

escalas.escapeRegExp = function(str) {
	return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
};

escalas.replaceAll = function(find, replace, str) {
	return str.replace(new RegExp(escalas.escapeRegExp(find), 'g'), replace);
};

escalas.apagaLinha = function(link) {
	if ($(link).attr('disabled'))
		return false;

	var html = "";
	if (confirm('Tem certeza de que deseja excluir este dia?')) {
		var trExcluir = link.parentNode.parentNode;
		var tabela = trExcluir.parentNode;
		tabela.removeChild(trExcluir);
	}
};

$(document).ready(function(){
	escalas.init();
});