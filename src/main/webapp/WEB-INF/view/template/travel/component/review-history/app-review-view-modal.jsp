<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission" %>
<div class="content-container no-top-margin padding-top-5" style="overflow: hidden;">

  <div>
    <ess-app-review-form-body app-review="appReview"></ess-app-review-form-body>
  </div>

  <div class="travel-button-container" style="margin-top: 5px !important; float: left; width: 100%;">
    <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_EDIT_APP.getPermissionString() %>">
      <button type="button" class="travel-reject-btn" ng-click="vm.onEdit(appReview)">
        Edit Application
      </button>
    </shiro:hasPermission>

    <div class="" style="float: right;">
      <a class="margin-10" target="_blank"
         ng-href="${ctxPath}/api/v1/travel/application/{{appReview.travelApplication.id}}.pdf">Print</a>
      <button type="button" class="travel-neutral-btn"
             ng-click="exit()">
        Close
      </button>
    </div>
  </div>
</div>