<%@tag description="Left navigation menu for Travel screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Travel Menu" colorClass="orange"/>
    <h3 class="main-topic">My Travel</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/travel/stub">Travel Request</a></li>
    </ul>
  </section>
</div>