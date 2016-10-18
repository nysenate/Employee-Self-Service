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
      <ess-component-nav:help-nav/>
      <div class="view-animate-container">
        <div class="view-animate">
          <iframe width="100%" height="700"
                  src="https://docs.google.com/document/d/1lJLSfVzWtAXPFcq0_xE-7ISVC7ylpq_mTEaeydPfXRU/pub?embedded=true"></iframe>
        </div>
      </div>
    </section>
  </jsp:body>
</ess-layout:body>