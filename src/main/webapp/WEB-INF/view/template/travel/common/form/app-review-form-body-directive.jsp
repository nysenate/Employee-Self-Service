<div style="width: 1200px;">
  <div style="display: flex;">
    <div>
      <ess-app-form-body app="appReview.travelApplication"></ess-app-form-body>
    </div>

    <div style="flex-grow: 1; margin-right: 0px; border-left: 2px solid #e6e6e6;">
      <div style="margin-left: 10px;">
        <div ng-if="!hasActions">
          <h3 class="dark-gray text-align-center">No Actions</h3>
        </div>

        <div ng-if="hasActions">
          <h3 class="text-align-center">Previous Actions</h3>

          <hr/>

          <div style="display: flex; flex-direction: column; margin: 10px;">
            <div ng-repeat="action in modifiedActions">

              <div style="display: flex; flex-direction: row; justify-content: space-between">
                <div ng-if="action.resubmitted" class="approved-text">Resubmitted by user</div>
                <div style="" ng-bind="::action.dateTime | date: 'shortDate'"></div>
                <div style="" ng-bind="::action.user.lastName"></div>
                <div ess-review-action-status="action"></div>
              </div>
              <div ng-show="action.notes" class="dark-gray" style="font-size: .9em; margin-top: 5px; margin-left: 40px;">
                <span ng-bind="::action.notes"></span>
              </div>

              <hr/>

            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</div>
