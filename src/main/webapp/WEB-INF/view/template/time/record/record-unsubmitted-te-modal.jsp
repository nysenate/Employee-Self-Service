<div class="confirm-modal">
  <h3 class="content-info">
    Warning: Previous Records Will Be Unavailable.
  </h3>
  <div class="confirmation-message">
    <p>
      All un-submitted temporary pay records for pay periods before the submitted record will be unavailable after submission.<br>
      This includes records for the following periods:
    </p>
    <ul class="unsubmitted-te-rec-list">
      <li ng-repeat="record in records">
        {{record.payPeriod.startDate | moment:'l'}} -
        {{record.payPeriod.endDate | moment:'l'}}
      </li>
    </ul>
    <hr/>
    <div class="input-container">
      <input ng-click="resolveModal()" class="submit-button" type="button" value="Proceed"/>
      <input ng-click="rejectModal()" class="reject-button" type="button" value="Cancel"/>
    </div>
  </div>
</div>
