<h3 class="content-info no-bottom-margin">
  Before submitting, you must acknowledge the following
</h3>
<hr/>
<div class="approve-submit-modal-details">
  <p class="acknowledged-message">
    For purposes of submitting a timesheet, the employee should ensure that his or her timesheet reflects what is contained in this request.
  </p>
  <div class="review-listing" ng-if="approvedCount > 0">
    <p>You are <strong class="green">approving</strong> the following {{approvedCount}} time-off request(s):  </p>
    <ul>
      <li ng-repeat="request in approved">
        {{request.name}} ({{request.startDate | moment:'MM/DD'}} - {{request.endDate | moment:'MM/DD'}})
      </li>
    </ul>
  </div>
  <p class="acknowledged-message" ng-show="approvedCount>0">
    To the best of my knowledge, the above listed employees are using accrued time from previous service in past reported period(s),
    and all supplied information is correct.
  </p>
  <div class="review-listing" ng-if="disapprovedCount > 0">
    <p>You are <strong class="dark-red">disapproving</strong> the following {{disapprovedCount}} time-off request(s):  </p>
    <ul>
      <li ng-repeat="request in disapproved">
        {{request.name}} ({{request.startDate | moment:'MM/DD'}} - {{request.endDate | moment:'MM/DD'}})
      </li>
    </ul>
  </div>
  <hr/>
  <div class="approve-input-container">
    <input ng-click="resolve()" class="submit-button" style="" type="button" value="I agree"/>
    <input ng-click="cancel()" class="reject-button" type="button" value="Cancel"/>
  </div>
</div>