<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>

<ess-layout:head>
  <jsp:attribute name="pageTitle">ESS - Employee Self Service</jsp:attribute>
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
        <h3 id="help-link"><a href="${pageContext.request.contextPath}/assets/pdf/ess-help.pdf" onclick="window.open('${pageContext.request.contextPath}/assets/pdf/ess-help.pdf', 'helpwindow', 'width=1024,height=768,location=no,menubar=no,personalbar=no,status=no,titlebar=no,toolbar=no'); return false;">View in New<br/>Window</a>
        </h3>
      </div>
    </section>
  </jsp:body>
</ess-layout:body>
