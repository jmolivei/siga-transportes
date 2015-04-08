<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ tag body-content="empty"%>

<c:if test="${errors.size() > 0}">
	<div id="divErros" class="gt-error">
		<ul>
			<c:forEach items="${errors}" var="error">
				<li>${error.message}</li>
			</c:forEach>
		</ul>
	</div>
</c:if>