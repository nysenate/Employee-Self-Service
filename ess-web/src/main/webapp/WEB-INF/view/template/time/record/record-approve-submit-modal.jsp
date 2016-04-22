<h3 class="content-info no-bottom-margin">
  Before submitting, you must acknowledge the following
</h3>
<hr/>
<div class="approve-submit-modal-details">
  <p class="acknowledged-message">
    For purposes of submitting a timesheet, the username and password is the electronic signature of the employee.&nbsp;
    As liability attaches to each timesheet, the employee should ensure that his or her username or password
    is securely kept and used.
  </p>
  <div class="review-listing" ng-if="approvedCount > 0">
    <p>You are <strong class="green">approving</strong> the following {{approvedCount}} record(s):  </p>
    <ul>
      <li ng-repeat="(id, record) in approved">
        {{record.employee.fullName}} ({{record.beginDate | moment:'MM/DD'}} - {{record.endDate | moment:'MM/DD'}})
      </li>
    </ul>
  </div>
  <p class="acknowledged-message" ng-show="approvedCount > 0">
    To the best of my knowledge, the above listed employees were employed by the office
    and have performed the proper duties assigned to them during the reported period(s),
    and all supplied information is correct.&nbsp;
    This Time and Attendance Record will be sent to the Personnel Office electronically.
  </p>
  <div class="review-listing" ng-if="disapprovedCount > 0">
    <p>You are <strong class="dark-red">disapproving</strong> the following {{disapprovedCount}} record(s):  </p>
    <ul>
      <li ng-repeat="(id, record) in disapproved">
        {{record.employee.fullName}} ({{record.beginDate | moment:'MM/DD'}} - {{record.endDate | moment:'MM/DD'}})
      </li>
    </ul>
  </div>
  <hr/>
  <div class="approve-input-container">
    <input ng-click="resolve()" class="submit-button" style="" type="button" value="I acknowledge"/>
    <input ng-click="cancel()" class="reject-button" type="button" value="Cancel"/>
  </div>
</div>