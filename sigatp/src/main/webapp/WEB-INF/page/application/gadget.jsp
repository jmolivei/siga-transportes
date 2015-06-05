<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>
<%@ taglib prefix="tptags" uri="/WEB-INF/tpTags.tld"%>


<div class="gt-content-box gt-for-table">
	<table border="0" class="gt-table">
		<thead>
			<tr>
				<th width="50%">Descri&ccedil;&atilde;o</th>
				<th width="25%" style="text-align: right">Total</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="lista" var="item">
			<tr>
				<td ><a href="${item[0]}">${null != item[1] ? item[1].raw() : ""}</a></td>
				<td align="right" ><a href="${item[0]}">${item[2]}</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<br />

<form action="${linkTo[Requisicoes].incluir}">
	<div id="rightbottom"></div>
	<br>
	<input type="submit" value="<fmt:message key='views.botoes.novo' />" class="gt-btn-small gt-btn-right"/>

</form>	
