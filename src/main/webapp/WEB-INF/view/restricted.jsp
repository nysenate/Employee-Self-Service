
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ESS: Application Access Restricted</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/assets/css/dest/app.min.css?v=${releaseVersion}"/>
</head>
<body>
    <div id="restricted-warning">
        <div class="ess-notification warn">
            <h2>Access Restricted</h2>
            <p class="margin-10">
                ESS is currently only available to a subset of Senate employees for testing.<br>
                <br>
                <a href="${ctxPath}/logout">Return to Login</a>
            </p>
        </div>
    </div>
</body>
</html>
