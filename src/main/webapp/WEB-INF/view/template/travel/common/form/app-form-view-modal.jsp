<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-form-body app="app"></ess-app-form-body>
  </div>

  <div class="margin-20">
    <div style="display: flex; flex-direction: column;">

      <div class="app-form-grid">
        <div class="app-form-label">
          Status:
        </div>
        <div class="app-form-s-col">
          <span ess-app-status="app"></span>
        </div>
      </div>

      <div class="app-form-grid" ng-if="app.status.note">
        <div class="app-form-label">
          Reason:
        </div>
        <div class="app-form-l-col">
          {{::app.status.note}}
        </div>
      </div>

    </div>
  </div>

  <div class="travel-button-container">
    <a class="margin-10" target="_blank" ng-click="viewExpenseSummary(app)">Expense Summary</a>
    <a class="margin-10 margin-right-20" target="_blank"
       ng-href="${ctxPath}/api/v1/travel/application/{{app.id}}.pdf">Print</a>
    <input type="button"
           ng-if="app.status.isDisapproved"
           class="neutral-button"
           value="Edit and Resubmit"
           ng-click="vm.onEditAndResubmit(app)">
    <input type="button" class="travel-neutral-button" value="Close"
           ng-click="exit()">
  </div>
</div>
