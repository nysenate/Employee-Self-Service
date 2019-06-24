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
      Active Delegates
    </div>

    <div class="padding-10 text-align-center">
      <div ng-show="vm.activeDelegates.length < 1">
        <h2 class="dark-gray">No Active Delegates.</h2>
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
              <span ng-show="d.fullName == undefined">
                <select ng-model="d.fullName" ng-options="name for name in vm.possibleDelegates"></select>
              </span>
              <span ng-show="d.fullName != undefined">
                {{::d.fullName}}
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
      </div>
    </div>
  </div>

  <div class="content-container">
    <div class="travel-content-info travel-text-bold">
      Delegate History
    </div>

    <div class="padding-10 text-align-center">
      <div ng-show="vm.pastDelegates.length < 1">
        <h2 class="dark-gray">No Delegate History.</h2>
      </div>

      <div ng-show="vm.pastDelegates.length > 0">
        <table class="travel-table">
          <thead>
          <tr>
            <th>Employee Name</th>
            <th>Activated Date</th>
            <th>Deactivated Date</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="d in vm.pastDelegates | orderBy: endDate" class="travel-text">
            <td ng-bind="::d.fullName"></td>
            <td ng-bind="::d.startDate"></td>
            <td ng-bind="::d.endDate"></td>
          </tr>
          </tbody>
        </table>

      </div>
    </div>
  </div>

</div>
