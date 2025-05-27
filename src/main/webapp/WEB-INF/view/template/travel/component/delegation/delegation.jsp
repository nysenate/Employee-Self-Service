<div ng-controller="DelegationCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Assign Delegates</h2>
    </div>
    <div class="content-container travel-content-controls">
      <h4 class="travel-content-info travel-text-bold">
        Grant your roles to another employee by assigning them as a delegate.
      </h4>
    </div>
  </div>

  <div class="content-container">
    <div class="content-info travel-text-bold">
      Current Delegates
    </div>

    <div ng-if="vm.data.isLoading">
      <div loader-indicator class="loader no-collapse"></div>
    </div>

    <div ng-if="!vm.data.isLoading">
      <div class="padding-10 text-align-center">
        <div ng-show="vm.data.activeDelegations.length < 1">
          <h2 class="dark-gray">No Current Delegates.</h2>
        </div>

        <div ng-show="vm.data.activeDelegations.length > 0">
          <table class="travel-table">
            <thead>
            <tr>
              <th></th>
              <th>Role</th>
              <th>Employee</th>
              <th>Start Date</th>
              <th>End Date</th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="d in vm.data.activeDelegations">
              <td ng-click="vm.deleteDelegation($index)" class="icon-cross"
                  style="font-size: 18px; cursor: pointer;"></td>
              <td>
                <select ng-model="d.role"
                        ng-options="role as role.displayName for role in vm.data.roles track by role.name">
                </select>
              </td>

              <td style="width: 250px;">
              <span ng-show="d.delegate == undefined">
                <ui-select ng-model="d.delegate" style="min-width:175px;">
                  <ui-select-match allow-clear="true">
                    <span ng-bind="$select.selected.fullName"></span>
                  </ui-select-match>
                  <ui-select-choices repeat="emp in vm.data.allowedDelegates
                                            | filter: $select.search track by emp.fullName
                                            | orderBy: 'fullName'">
                    <div ng-bind-html="emp.fullName"></div>
                  </ui-select-choices>
                </ui-select>
              </span>
                <span ng-show="d.delegate != undefined">
                {{::d.delegate.fullName}}
              </span>
              </td>
              <td>
                <input type="checkbox" ng-model="d.useStartDate" id="useStartDate{{$index}}"
                       ng-change="vm.useStartDate(d)">
                <label for="useStartDate{{$index}}" class="travel-highlight-text margin-right-10">Set Start Date</label>
                <input datepicker ng-model="d.startDate" ng-disabled="!d.useStartDate" style="width: 105px;">
              </td>
              <td>
                <input type="checkbox" ng-model="d.useEndDate" id="useEndDate{{$index}}" ng-change="vm.useEndDate(d)">
                <label for="useEndDate{{$index}}" class="travel-highlight-text margin-right-10">Set End Date</label>
                <input datepicker ng-model="d.endDate" ng-disabled="!d.useEndDate" from-date="d.startDate"
                       style="width: 105px;">
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div class="padding-top-10">
          <button type="button" class="travel-primary-btn"
                 ng-click="vm.addNewDelegation()">
            Add New Delegate
          </button>
          <button type="button" class="travel-submit-btn"
                 ng-click="vm.saveDelegations()">
            Save Delegates
          </button>
          <span class="travel-notification margin-left-10" style="position:absolute;"
                ng-show="vm.data.displaySavedMessage">
          Delegates saved
        </span>
        </div>
      </div>
    </div>
  </div>
</div>
