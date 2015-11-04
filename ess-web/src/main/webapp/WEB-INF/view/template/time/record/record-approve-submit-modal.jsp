<h3 class="content-info no-bottom-margin">
  Before submitting, you must acknowledge the following
</h3>
<hr/>
<div class="approve-submit-modal-details">
  <div class="review-listing" ng-if="approvedCount > 0">
    <p>You are <strong class="green">approving</strong> the following {{approvedCount}} record(s):  </p>
    <ul>
      <li ng-repeat="(id, record) in approved">
        {{record.employee.fullName}} ({{record.beginDate | moment:'MM/DD'}} - {{record.endDate | moment:'MM/DD'}})
      </li>
    </ul>
  </div>
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