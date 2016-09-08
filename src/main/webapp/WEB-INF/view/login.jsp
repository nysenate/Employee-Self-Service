<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ess" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ess-component" tagdir="/WEB-INF/tags/component" %>
<%@ taglib prefix="ess-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ess-layout:head>
    <jsp:attribute name="pageTitle">ESS Login</jsp:attribute>
    <jsp:body>
        <ess:ess-assets/>
        <ess:login-assets/>
    </jsp:body>
</ess-layout:head>
<ess-layout:body>
    <jsp:body>
        <input type="hidden" value="0LOKZECwqdNGvKZFy3uB"> <!-- Used by angular to check if response is the login page. -->
        <c:if test="${runtimeLevel != 'prod'}">
            <div ess-notification level="warn" title="Running in ${runtimeLevel} mode"
                 message="Actions performed here will not affect the production database."></div>
        </c:if>
        <section id="loginContainer" class="login-container">
            <p style="display:none" class="login-message">You have been logged out.</p>
            <div id="loginHeader">
                <p>New York State Senate Employee Self Service</p>
            </div>
            <div id="photoContainer" class="login-info-container">
                <img src="${ctxPath}/assets/img/capital-exterior.jpg" width="400px"/>
            </div>
            <div ng-controller="LoginController" id="loginFormContainer" class="login-form-container">
                <div class="login-error-container" ng-class="{'show-error': showError}">
                    <span>{{errorMessage}}</span>
                </div>
                <form id="loginForm" method="post" ng-submit="login()">
                    <ess-component:xsrf-token/>
                    <ul class="input-list" ng-class="{'hide-view': !isActiveView(1)}">
                        <li>
                            <span class="heading-span">Sign in to proceed</span>
                            <hr/>
                        </li>
                        <li class="shift-down-slightly">
                            <label for="usernameInput">Username</label>
                            <input ng-model="credentials.username"
                                   ng-class="{'error': errorFields.username}"
                                   ng-change="showError = false"
                                   ng-click="errorFields.username = false"
                                   type="text" maxlength="64" id="usernameInput" name="username" />
                        </li>
                        <li>
                            <label for="passwordInput">Password</label>
                            <input ng-model="credentials.password"
                                   ng-class="{'error': errorFields.password}"
                                   ng-change="showError = false"
                                   ng-click="errorFields.password = false"
                                   type="password" maxlength="128" id="passwordInput" name="password" />
                        </li>
                        <li class="shift-down-slightly">
                            <div class="login-action-container">
                                <div class="login-loader" ng-show="loginInProgress"><span>Logging in</span></div>
                                <button ng-hide="loginInProgress" type="submit" id="submitButton">Login</button>
                            </div>
                        </li>
                        <li>
                            <a id="helpMeLink" class="subtle-link"
                               ng-click="setActiveView(2); showError = false;">Having trouble logging in?</a>
                        </li>
                    </ul>
                </form>
                <div class="additional-view">
                    <ul class="help-list" ng-class="{'hide-view': !isActiveView(2)}">
                        <li>
                            <span class="heading-span">What kind of problem are you having?</span>
                            <hr/>
                        </li>
                        <li class="shift-down-slightly shift-right">
                            <a ng-click="setActiveView(3)">I forgot my password.</a>
                        </li>
                        <li class="shift-right">
                            <a ng-click="setActiveView(3)">I forgot my username.</a>
                        </li>
                        <li class="shift-right">
                            <a ng-click="setActiveView(3)">I'm having a different problem.</a>
                        </li>
                        <li class="shift-down-slightly">
                            <button type="reset" ng-click="setActiveView(1)">Return to login</button>
                        </li>
                    </ul>
                </div>
                <div class="additional-view">
                    <ul class="contact-list" ng-class="{'hide-view': !isActiveView(3)}">
                        <li>
                            <span class="heading-span">Phone Support</span>
                            <hr/>
                        </li>
                        <li class="shift-down-slightly shift-right">
                            <span>For technical problems call:</span><br/>
                            <span class="font-weight-bold">STS Help Line at Senate x2011</span>
                        </li>
                        <li class="shift-down-slightly shift-right">
                            <span>For Personnel related questions:</span> <br/>
                            <span class="font-weight-bold">Senate Personnel Office - (518) 455 3376</span>
                        </li>
                        <li class="shift-down-slightly">
                            <button type="reset" ng-click="setActiveView(1)">Return to login</button>
                        </li>
                    </ul>
                </div>
            </div>
        </section>
    </jsp:body>
</ess-layout:body>
<ess-layout:footer/>