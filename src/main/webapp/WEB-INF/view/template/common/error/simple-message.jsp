<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--
Template for a message to be returned in place of a page.
Typically, when the user requests a page they don't have access to
-->
<ess-notification level="${level}" title="${title}">
  <p>
    <c:if test="${not empty message}">
      ${message}<br>
    </c:if>
    Please contact the STS Helpline at (518) 455-2011 if you require any assistance.
  </p>
</ess-notification>
<div modal-container></div>

