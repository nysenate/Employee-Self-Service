<div loader-indicator class="loader" ng-show="request.allowance"></div>

<div class="content-container" ng-hide="request.allowance">
  <div class="content-container content-controls" ng-if="!isUser()">
    <h1>
      {{empSupInfo.empFirstName}}
      {{empSupInfo.empLastName | possessive}}
      Current Allowed Hours
    </h1>
  </div>

  <p class="content-info" ng-hide="payType === 'TE'">
    Selected employee is non-temporary and does not have an allowance.
  </p>

  <div class="padding-10">
    <allowance-bar allowance="allowance"
                   ng-show="payType === 'TE' || payType === null">
    </allowance-bar>
  </div>

</div>
