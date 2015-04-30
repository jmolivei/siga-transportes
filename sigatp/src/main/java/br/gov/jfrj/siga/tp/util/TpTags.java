package br.gov.jfrj.siga.tp.util;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.i18n.Messages;
import play.mvc.Router.ActionDefinition;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import br.gov.jfrj.siga.tp.tag.ErroLinkTag;

/**
 * 
 * @author db1
 *
 */
@Deprecated
@FastTags.Namespace("tp.tags")
public class TpTags extends FastTags {

	private static final String IMG_LINKNOVAJANELAICON = "/sigatp/public/images/linknovajanelaicon.png";

	public static void _link(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Exception {
		String texto;
		String parteTextoLink;
		boolean ehEditavel;
		ActionDefinition comando;
		String comandoEditavel = "";

		if (args.size() < 3) {
			throw new Exception(Messages.get("tpTags.parametrosInvalidos.exception"));
		}
		if ((!args.containsKey("parteTextoLink")) && (!args.containsKey("comando"))) {
			throw new Exception(Messages.get("tpTags.naoInformados.exception"));
		}

		if (!args.containsKey("ehEditavel")) {
			ehEditavel = false;
		} else {
			ehEditavel = (Boolean) args.get("ehEditavel");
		}

		texto = (String) args.get("arg");
		parteTextoLink = (String) args.get("parteTextoLink");
		comando = (ActionDefinition) args.get("comando");

		if (ehEditavel) {
			comandoEditavel = comando.url.replace("true", "false");
		}

		StringBuffer saida = new StringBuffer();
		if (!comandoEditavel.isEmpty()) {
			saida.append("<a href='" + comandoEditavel + "'>" + parteTextoLink + "</a>");
		} else {
			saida.append(parteTextoLink);
		}
		// saida.append(" <a href=\"#\" onclick=\"javascript:window.open('" + comando.url + "');\">");

		FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
		String caminhoUrl = formata.retornarCaminhoContextoUrl(comando.url);
		saida.append(" <a href=\"#\" onclick=\"javascript:window.open('" + caminhoUrl + "');\">");
		saida.append("<img src=\"" + TpTags.IMG_LINKNOVAJANELAICON + "\" alt=\"Abrir em uma nova janela\" title=\"Abrir em uma nova janela\"></a> ");
		saida.append(texto.replace(parteTextoLink, ""));

		out.println(saida.toString());
	}

	/**
	 * Migrado para {@link ErroLinkTag}
	 * @param args
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 * @throws Exception
	 */
	@Deprecated
	public static void _errolink(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Exception {
		String sequencias;
		ActionDefinition comando;
		String urlTransformada;
		String classe = "";

		if (args.size() < 2) {
			throw new Exception(Messages.get("tpTags.parametrosInvalidos.exception"));
		}

		if (!args.containsKey("comando")) {
			throw new Exception(Messages.get("tpTags.comandoNaoInformado.exception"));
		}

		sequencias = (String) args.get("arg");
		comando = (ActionDefinition) args.get("comando");
		String[] sequenciasMissao = sequencias.split(",");
		StringBuffer saida = new StringBuffer();

		if (args.containsKey("classe")) {
			classe = (String) args.get("classe");
		}

		if (classe.contains("Veiculo")) {
			saida.append("<li> O ve&iacute;culo est&aacute; ocupado nas miss&otilde;es: ");
		} else if (classe.contains("Condutor")) {
			saida.append("<li> O condutor est&aacute; ocupado nas miss&otilde;es: ");
		} else if (classe.contains("EscalaDeTrabalho")) {
			saida.append("<li> N&atilde;o est&aacute; mais dispon&iacute;vel, para a escala abaixo, o condutor escalado nas miss&otilde;es: ");
		}

		String delimitador = "";
		for (int i = 0; i < sequenciasMissao.length; i++) {
			saida.append(delimitador);
			urlTransformada = comando.url.replace("parse", sequenciasMissao[i]);

			// saida.append(sequenciasMissao[i] + " <a href=\"#\" onclick=\"javascript:window.open('" + urlTransformada + "');\">");

			FormataCaminhoDoContextoUrl formata = new FormataCaminhoDoContextoUrl();
			String caminhoUrl = formata.retornarCaminhoContextoUrl(urlTransformada);
			saida.append(sequenciasMissao[i] + " <a href=\"#\" onclick=\"javascript:window.open('" + caminhoUrl + "');\">");
			saida.append("<img src=\"" + TpTags.IMG_LINKNOVAJANELAICON + "\" alt=\"Abrir em uma nova janela\" title=\"Abrir em uma nova janela\"></a>");
			delimitador = " , ";
		}
		saida.append(". </li>");
		out.println(saida.toString());
	}

	public static void _erroGenericolink(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Exception {
		String mensagem;
		String comando;

		if (args.size() < 2) {
			throw new Exception(Messages.get("tpTags.parametrosInvalidos.exception"));
		}
		if (!args.containsKey("comando")) {
			throw new Exception(Messages.get("tpTags.comandoNaoInformado.exception"));
		}

		mensagem = (String) args.get("arg");
		comando = (String) args.get("comando");

		StringBuffer saida = new StringBuffer();
		saida.append("<li>");
		saida.append(mensagem);
		saida.append(comando);
		saida.append("</li>");
		out.println(saida.toString());
	}

	@Deprecated
	public static void _ola(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Exception {
		out.println("ola¡");
	}
}