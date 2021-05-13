<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission" %>

<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-review-form-body app-review="appReview"></ess-app-review-form-body>
  </div>

  <div class="travel-button-container" style="margin-top: 5px !important; min-height: 50px;">
    <span ng-if="role.name === appReview.nextReviewerRole">
      <button type="button" class="travel-submit-btn"
             ng-click="approve()">
        Approve Application
      </button>
      <button type="button" class="travel-reject-btn"
             ng-click="disapprove()">
        Disapprove application
      </button>

      <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_EDIT_APP.getPermissionString() %>">
        <button type="button" class="travel-neutral-btn"
               ng-click="vm.onEdit()">
          Edit Application
        </button>
      </shiro:hasPermission>
      <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_CAN_SHARE.getPermissionString() %>">
        <button ng-if="appReview.isShared"
               type="button"
               class="travel-neutral-btn"
               ng-click="onRemoveShare()">
          Remove share with SOS
        </button>
        <button ng-if="!appReview.isShared"
               type="button"
               class="travel-neutral-btn"
               ng-click="onShare()">
          Share with SOS
        </button>
      </shiro:hasPermission>
    </span>
    <div style="float: right;">
      <a class="margin-10" target="_blank"
         ng-href="${ctxPath}/api/v1/travel/application/{{appReview.travelApplication.id}}.pdf">Print</a>
      <button type="button" class="travel-neutral-btn"
             ng-click="exit()">
        Close
      </button>
    </div>
  </div>
</div>
