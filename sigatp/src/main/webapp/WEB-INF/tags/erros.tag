<%@ tag body-content="empty"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sigatpTags" uri="/WEB-INF/tpTags.tld"%>

<c:if test="${errors.size() > 0}">
	<div id="divErros" class="gt-error">
		<c:forEach items="${errors}" var="error">
			<c:choose>
			    <c:when test="${error.category.contains('LinkErro')}">
			    	<sigatpTags:erroLink  
			    		message="${error.message}" 
			    		classe="${error.category.replace('LinkErro','')}" 
			    		comando='${linkTo[MissoesController].buscarPelaSequenceAposErro}'
		    		/>
			    </c:when>
			    <c:when test="${error.category.contains('LinkGenericoErro')}">
			    	TODO: Migrar para utilizar tag jsp
			    	#{if error.key.contains('LinkGenericoErro')}
						#{tp.tags.erroGenericolink error.message(), comando: '<a href="#" onclick="javascript:window.history.back();">Voltar</a>'}
						#{/tp.tags.erroGenericolink} 
				    #{/if}
			    </c:when>
			    <c:otherwise>
			     	${error.message}
			    </c:otherwise>
			</c:choose>
			</br>
		</c:forEach>
	</div>
</c:if>