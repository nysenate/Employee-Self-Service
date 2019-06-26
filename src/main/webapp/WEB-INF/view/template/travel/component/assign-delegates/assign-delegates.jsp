<div ng-controller="AssignDelegatesCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Assign Delegates</h2>
    </div>
    <div class="content-container travel-content-controls">
      <h4 class="travel-content-info travel-text-bold">
        Grant your permissions to another employee by assigning them as a delegate.
      </h4>
    </div>
  </div>

  <div class="content-container">
    <div class="content-info travel-text-bold">
      Current Delegates
    </div>

    <div class="padding-10 text-align-center">
      <div ng-show="vm.activeDelegates.length < 1">
        <h2 class="dark-gray">No Current Delegates.</h2>
      </div>

      <div ng-show="vm.activeDelegates.length > 0">
        <table class="travel-table">
          <thead>
          <tr>
            <th></th>
            <th>Employee</th>
            <th>Start Date</th>
            <th>End Date</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="d in vm.activeDelegates">
            <td ng-click="vm.deleteDelegate($index)" class="icon-cross" style="font-size: 18px; cursor: pointer;"></td>
            <td style="width: 250px;">
              <span ng-show="d.delegate == undefined">
                <select ng-model="d.delegate" ng-options="emp.fullName for emp in vm.possibleDelegates"></select>
              </span>
              <span ng-show="d.delegate != undefined">
                {{::d.delegate.fullName}}
              </span>
            </td>
            <td>
              <input type="checkbox" ng-model="d.useStartDate" id="useStartDate" ng-change="vm.useStartDate(d)">
              <label for="useStartDate" class="travel-highlight-text margin-right-10">Set Start Date</label>
              <input datepicker ng-model="d.startDate" ng-disabled="!d.useStartDate" style="width: 105px;">
            </td>
            <td>

              <input type="checkbox" ng-model="d.useEndDate" id="useEndDate" ng-change="vm.useEndDate(d)">
              <label for="useEndDate" class="travel-highlight-text margin-right-10">Set End Date</label>
              <input datepicker ng-model="d.endDate" ng-disabled="!d.useEndDate" from-date="d.startDate"
                     style="width: 105px;">
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="padding-top-10">
        <input type="button" class="travel-neutral-button" value="Add New Delegate"
               ng-click="vm.addNewDelegate()">
        <input type="button" class="submit-button" value="Save Delegates"
               ng-click="vm.saveDelegates()">
        <span class="travel-notification margin-left-10" style="position:absolute;"
              ng-show="vm.displaySavedMessage">
          Delegates saved
        </span>
      </div>
    </div>
  </div>
</div>
