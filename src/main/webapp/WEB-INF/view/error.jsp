<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>ESS Error: ${title}</title>
  <link rel="stylesheet" type="text/css" href="${ctxPath}/assets/css/dest/app.min.css?v=${releaseVersion}"/>
</head>
<body>
<div id="error-page">
  <div class="ess-notification ${level}">
    <h2>${title}</h2>
    <p class="margin-10">
      ${message}
      <br><br>
      <a href="${ctxPath}/logout">Return to Login</a>
    </p>
  </div>
</div>
</body>
</html>