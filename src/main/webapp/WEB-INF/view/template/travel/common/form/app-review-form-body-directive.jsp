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

          <table class="travel-table">
            <tbody>
            <tr ng-repeat="action in appReview.actions | orderBy: 'dateTime'">
              <td ng-bind="::action.dateTime | date: 'shortDate'" ></td>
              <td ng-bind="::action.user.lastName"></td>
              <td ess-action-type-cell="action.type"></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>