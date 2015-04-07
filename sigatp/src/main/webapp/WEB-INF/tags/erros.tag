<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ tag body-content="empty"%>

 <div id="divErros" class="gt-error">
	<ul>
		<c:forEach items="${errors}" var="error">
			<li>${error.category } - ${error.message }</li>
		</c:forEach>
	</ul>
</div>