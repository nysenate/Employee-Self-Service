<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Time and Attendance</jsp:attribute>
  <jsp:body>
    <ess:ess-assets/>
    <ess:help-assets/>
  </jsp:body>
</ess-layout:head>

<ess-layout:body>
  <jsp:body>
    <base href="/" />
    <ess-component-nav:top-nav activeTopic="help"/>
    <section class="content-wrapper">
      <div id="help-viewer">
        <iframe id="help-frame" src="${pageContext.request.contextPath}/assets/pdf/ess-help.pdf"></iframe>
        <h3 id="help-link">
          <a target="_blank"
             href="${pageContext.request.contextPath}/assets/pdf/ess-help.pdf">
            View in New Tab
          </a>
        </h3>
      </div>
    </section>
  </jsp:body>
</ess-layout:body>