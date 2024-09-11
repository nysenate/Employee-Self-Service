<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="content-container no-top-margin">
  <h2 class="content-info" style="color: red;">
    Travel Date Error
  </h2>
  <div style="margin-left: 20px; margin-right: 20px;">
    One or more of the specified travel dates is invalid.
    <br/>
    Please review the travel dates in both the outbound and return segments and try again.
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-btn" value="Ok"
           title="ok" ng-click="ok()">
  </div>
</div>
