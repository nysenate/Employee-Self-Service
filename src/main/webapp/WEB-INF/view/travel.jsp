<%@ page import="gov.nysenate.ess.travel.authorization.permission.TravelPermission" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Employee Self Service</jsp:attribute>
  <jsp:body>
    <ess:ess-assets/>
    <ess:travel-assets/>
  </jsp:body>
</ess-layout:head>

<ess-layout:body>
  <jsp:body>
    <ess-component-nav:top-nav activeTopic="travel"/>
    <section class="content-wrapper">

      <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_REVIEW.getPermissionString() %>">
      <div ng-controller="TravelBadgeCtrl">
        </shiro:hasPermission>

        <ess-component-nav:travel-nav/>
        <div class="view-animate-container">
          <div ng-view class="view-animate"></div>
        </div>

        <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_REVIEW.getPermissionString() %>">
      </div>
      </shiro:hasPermission>

    </section>
  </jsp:body>
</ess-layout:body>
