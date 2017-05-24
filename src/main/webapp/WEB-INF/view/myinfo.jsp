<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Employee Self Service</jsp:attribute>
  <jsp:body>
    <ess:ess-assets/>
    <ess:myinfo-assets/>
  </jsp:body>
</ess-layout:head>

<ess-layout:body>
  <jsp:body>
    <base href="/" />
    <ess-component-nav:top-nav activeTopic="myinfo"/>
    <section class="content-wrapper">
      <ess-component-nav:myinfo-nav/>
      <div class="view-animate-container">
        <div ng-view class="view-animate"></div>
      </div>
    </section>
  </jsp:body>
</ess-layout:body>
