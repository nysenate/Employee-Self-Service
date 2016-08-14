<%@ tag description="Simple header to indicate the current page" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="topicTitle" required="true" %>
<%@ attribute name="colorClass" required="true" %>

<section class="section-title-container ${colorClass}">
    <span>${topicTitle}</span>
</section>