<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/7/19
  Time: 4:06 PM
  To change this template use File | Settings | File Templates.
--%>

<section ng-controller="RequestApprovalCtrl" ng-init="updateLists()">
  <div class="time-attendance-hero">
    <h2>Employee Time Off Requests</h2>
  </div>

  <!-- TABLE FOR REQUESTS PENDING APPROVAL -->
  <div class="content-container content-controls" ng-if="!loadingRequests">
    <div class="content-container"><h1>Time Off Requests Needing Approval</h1></div>

    <time-off-request-approval ng-if="!loadingRequests && !loadingEmployees" format="pendingFormat"
                               requests="pendingRequests">
    </time-off-request-approval>

    <!--Select Buttons-->
    <div class="record-manage-controls">
      <ul class="horizontal">
        <li><a ng-click="selectAll('SUBMITTED')">SelectAll</a></li>
        <li><a ng-click="selectNone('SUBMITTED')">Select None</a></li>
      </ul>
      <div class="select-actions">
        <input class="submit-button" type="button" value="Approve Selected"
               ng-disabled = "hasSelections('SUBMITTED') === false"
               ng-click="approveSelected('SUBMITTED')">
        <input class="time-neutral-button" type="button" value="Review Selected"
               ng-disabled = "hasSelections('SUBMITTED') === false"
               ng-click="reviewSelected('SUBMITTED')">
      </div>
    </div>
  </div>


  <!-- TABLE FOR APPROVED AND UPCOMING REQUESTS -->
  <div class="content-container content-controls" ng-if="!loadingRequests">
    <div class="content-container"><h1>Approved and Upcoming Time Off Requests</h1></div>

    <time-off-request-approval ng-if="!loadingRequests && !loadingEmployees" format="approvedFormat"
                               requests="activeRequests">
    </time-off-request-approval>

    <!--Select Buttons-->
    <div class="record-manage-controls">
      <ul class="horizontal">
        <li><a ng-click="selectAll('APPROVED')">SelectAll</a></li>
        <li><a ng-click="selectNone('APPROVED')">Select None</a></li>
      </ul>
      <div class="select-actions">
        <input class="reject-button" type="button" value="Reject Selected"
               ng-disabled = "hasSelections('APPROVED') === false"
               ng-click="rejectSelected('APPROVED')">
        <input class="time-neutral-button" type="button" value="Review Selected"
               ng-disabled = "hasSelections('APPROVED') === false"
               ng-click="reviewSelected('APPROVED')">
      </div>
    </div>
  </div>


  <!--Modals-->
  <div modal-container>
    <!--Review window-->
    <modal id="time-off-request-review" modal-id="time-off-request-review">
      <div time-off-request-review-modal></div>
    </modal>
    <!--Closing the review window-->
    <modal modal-id="time-off-request-review-close">
      <div confirm-modal rejectable="true" title="Unsubmitted Requests"
           confirm-message="There are one or more reviewed requests that have not been submitted. Discard changes?"
           resolve-button="Discard Changes" resolve-class="reject-button"
           reject-button="Resume Review" reject-class="submit-button">
      </div>
    </modal>
    <!--Submitting changes-->
    <modal modal-id="time-off-request-approve-submit-modal">
      <div time-off-request-approve-submit-modal></div>
    </modal>
  </div>
</section>
