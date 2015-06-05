<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="sigla == 'M'">
	<jsp:include page="Missoes/ler.html"/>
</c:if>
<c:if test="sigla == 'R'">
	<jsp:include page="Requisicoes/ler.html"/>
</c:if>
<c:if test="sigla == 'S'">
	<jsp:include page="ServicosVeiculo/ler.html"/>
</c:if>