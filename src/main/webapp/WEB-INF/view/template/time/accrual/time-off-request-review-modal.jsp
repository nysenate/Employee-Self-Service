<section id="time-off-request-review-content" class="content-container content-controls"
         title="Review and Approve Time Off Requests">
  <div>

    <!-- Header -->
    <p class="content-info no-bottom-margin">
      Click a time-off request from the Time-Off Request List on the left hand side to review the request.
      You can then either Approve or Disapprove the request.
    </p>

    <div id="time-off-request-review-outer-box">
      <!-- Left panel with list of requests -->
      <div id="time-off-request-selection-pane">
        <div class="pane-title">
          <h4>Employee Time-Off Request List</h4>
        </div>
        <table id="time-off-request-selection-table" class="ess-table">
          <thead>
          <tr>
            <th>Employee</th>
            <th>Action</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat='request in requests' ng-class="{'active': iSelectedRequest === $index,
                                                         'approved':getApprovalStatus(request) === 'approved',
                                                         'disapproved': getApprovalStatus(request) === 'disapproved'}"
           ng-click="selectRequest($index)" id="{{request.requestId}}" title="Select Time-Off Request for Review">
            <td class="name-column">{{request.name}}</td>
            <td ng-switch="getApprovalStatus(request)" style="width: 10em">
              <span ng-switch-when="approved">Approve</span>
              <span ng-switch-when="disapproved">Disapprove</span>
              <span ng-switch-default>--</span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Single Request Review -->
      <div id="time-off-request-review-inner-box">
        <!-- Header for the request -->
        <div id="time-off-request-review-header">
          <h2>Time-Off Request for <strong>{{requests[iSelectedRequest].name}}</strong>,
            submitted on {{requests[iSelectedRequest].timestampPrint}}</h2>
        </div>
        <!-- Request View -->
        <div id="time-off-request-details-view">
          <p class="time-off-request-accruals">&ensp;&ensp;&ensp;&ensp;&ensp;Available Hours: &emsp;
            <span class="vacation-text">Vacation: {{accruals.vacation}}&ensp;</span>
            <span class="personal-text">Personal: {{accruals.personal}}&ensp;</span>
            <span class="sick-text">Sick: {{accruals.sick}}&ensp;</span>
          </p>
          <!--Table of requested days off-->
          <table class="time-off-request-view timeoff-request-table">
            <thead>
              <tr>
                <th class="timeoff-table-date">Date</th>
                <th class="timeoff-table-hours">Work</th>
                <th class="timeoff-table-hours">Holiday</th>
                <th class="timeoff-table-hours vacation-text">Vacation</th>
                <th class="timeoff-table-hours personal-text">Personal</th>
                <th class="timeoff-table-hours sick-text">Sick Emp</th>
                <th class="timeoff-table-hours sick-text">Sick Fam</th>
                <th class="timeoff-table-hours">Misc</th>
                <th class="timeoff-table-misc">Misc Leave Type</th>
                <th class="timeoff-table-hours">Total</th>
              </tr>
            </thead>
            <tbody>
              <tr ng-repeat="day in requests[iSelectedRequest].days">
                <td>{{day.datePrint}}</td>
                <td ng-switch="day.workHours">
                  <span ng-switch-default>{{day.workHours}}</span>
                  <span ng-switch-when=null>--</span>
                </td>
                <td ng-switch="day.holidayHours">
                  <span ng-switch-default>{{day.holidayHours}}</span>
                  <span ng-switch-when="null">--</span>
                </td>
                <td ng-switch="day.vacationHours">
                  <span ng-switch-default>{{day.vacationHours}}</span>
                  <span ng-switch-when="null">--</span>
                </td>
                <td ng-switch="day.personalHours">
                  <span ng-switch-default>{{day.personalHours}}</span>
                  <span ng-switch-when="null">--</span>
                </td>
                <td ng-switch="day.sickEmpHours">
                  <span ng-switch-default>{{day.sickEmpHours}}</span>
                  <span ng-switch-when="null">--</span>
                </td>
                <td ng-switch="day.sickFamHours">
                  <span ng-switch-default>{{day.sickFamHours}}</span>
                  <span ng-switch-when="null">--</span>
                </td>
                <td ng-switch="day.miscHours">
                  <span ng-switch-default>{{day.miscHours}}</span>
                  <span ng-switch-when="null">--</span>
                </td>
                <td ng-if="day.miscType != null">{{day.miscType}}</td>
                <td ng-if="day.miscType === null">--</td>
                <td>{{day.totalHours}}</td>
              </tr>
            </tbody>
          </table>
          <p class="time-off-request-accruals">Hours After Request:&ensp;
            <span class="vacation-text">Vacation: {{accrualsPost.vacation}}&ensp;</span>
            <span class="personal-text">Personal: {{accrualsPost.personal}}&ensp;</span>
            <span class="sick-text">Sick: {{accrualsPost.sick}}&ensp;</span>
          </p>
          <!--Comments-->
          <h3>Comments: </h3>
          <div class="comment-list">
            <p class="comment" ng-repeat="comment in requests[iSelectedRequest].comments">
              <strong ng-if="comment.authorId == requests[iSelectedRequest].employeeId">
                {{requests[iSelectedRequest].name}}:&nbsp</strong>
              <strong ng-if="comment.authorId == requests[iSelectedRequest].supervisorId">Me:&nbsp</strong>
              {{comment.text}}
            </p>
            <!-- Input for new comment from Supervisor -->
            <div class="new-comment-container">
              <p class="comment"><strong>Me:</strong></p>
              <textarea ng-model="addedComments[requests[iSelectedRequest].requestId]"></textarea>
            </div>
          </div>

        </div>
        <!-- Button Container -->
        <div id="time-off-request-review-button-container">
          <div id="time-off-request-review-upper-buttons" class="record-approval-buttons"
               ng-switch="getApprovalStatus(requests[iSelectedRequest])">
            <input type="button" class="reject-button" value="Undo Approval"
                   title="Undo Approval" ng-switch-when="approved"
                   ng-click="cancelRequest()"/>
            <input type="button" class="time-neutral-button" value="Undo Disapproval"
                   title="Undo Disapproval" ng-switch-when="disapproved"
                   ng-click="cancelRequest()"/>
            <input type="button" class="submit-button" value="Approve Request"
                   title="Approve Request" ng-switch-default
                   ng-click="approveRequest()"/>
            <input type="button" class="reject-button" value="Disapprove Request"
                   title="Disapprove Request" ng-switch-default
                   ng-click="rejectRequest()">
          </div>
          <div id="time-off-request-review-lower-buttons">
            <input type="button" class="submit-button" value="Submit Changes"
                   title="Submit Approved and Disapproved Requests"
                   ng-click="submitChanges()" ng-disabled="submissionEmpty()"/>
            <input type="button" class="time-neutral-button" value="Cancel"
                   title="Cancel Request Review (All changes will be lost)"
                   ng-click="close()"/>
          </div>
        </div>
      </div>
    </div>


  </div>
</section>