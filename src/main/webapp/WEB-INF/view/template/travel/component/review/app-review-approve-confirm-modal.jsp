<%@ page import="gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="content-container no-top-margin padding-top-5">
  <h3 class="content-info">
    Approval Confirmation
  </h3>
  <div style="margin-left: 20px; margin-right: 20px;">
    <p>
      You are about to approve a Travel Application for
      <span ng-bind="::appReview.travelApplication.traveler.fullName"></span>
      <span ng-show="isSingleDayTravel">
      on <span ng-bind="::appReview.travelApplication.activeAmendment.startDate | date: 'shortDate'"></span>.
      </span>
      <span ng-show="!isSingleDayTravel">
      from <span ng-bind="::appReview.travelApplication.activeAmendment.startDate | date: 'shortDate'"></span>
      to <span ng-bind="::appReview.travelApplication.activeAmendment.endDate | date: 'shortDate'"></span>.
      </span>
    </p>

    <p>
      You may leave a note for the other reviewers below.
    </p>

    <div class="margin-top-20">
      <label>Approval Notes:
        <textarea ng-model="notes" cols="40" rows="3"></textarea>
      </label>
    </div>

    <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_REQUEST_DISCUSSION.getPermissionString() %>">
    <div class="margin-top-20">
      <input type="checkbox" id="requestDiscussion" ng-model="isDiscussionRequested">
      <label for="requestDiscussion">Request Discussion</label>
    </div>
    </shiro:hasPermission>

  </div>

  <div class="travel-button-container">
    <input type="button" class="submit-button" value="Approve Application"
           title="Approve Application" ng-click="approve()">
    <input type="button" class="neutral-button" value="Cancel Approval"
           title="Cancel Approval" ng-click="cancel()">
  </div>
</div>