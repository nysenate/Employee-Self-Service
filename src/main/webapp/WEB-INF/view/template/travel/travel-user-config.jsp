<div ng-controller="UserConfigCtrl">
  <div class="travel-hero">
    <h2>User Configuration</h2>
  </div>

  <div>
    <div class="content-container content-controls">
      <p class="content-info">Grant another person the ability to file a travel request for you</p>
      <div class="padding-10">
        <table class="simple-table">
          <thead>
          <tr>
            <th>#</th>
            <th>Requester</th>
            <th>Status</th>
            <th>Start Date</th>
            <th>End Date</th>
          </tr>
          </thead>
          <tbody>
            <tr>
              <td>1</td>
              <td>
                <select ng-init="granteeInfo.selectedGrantee = null"
                        ng-if="dataLoaded"
                      ng-model="granteeInfo.selectedGrantee"
                      ng-options="employee as employee.fullName for employee in grantees | orderBy:'fullName'"></select>

                <p class="travel-loading" ng-if="dataLoaded == false">Loading...</p>
              </td>
              <td>
                <input id="requester-permanent-box" type="checkbox" ng-model="granteeInfo.permanent"
                       ng-click="setPermanent()">
                <label for="requester-permanent-box">Permanent?</label>
              </td>
              <td>

                <div class="horizontal-input-group">
                  <input id="grant-start-date" ng-checked="granteeInfo.startDate"
                         ng-disabled="granteeInfo.permanent" type="checkbox" ng-click="setStartDate()"/>
                  <label for="grant-start-date">Set Start Date</label>
                  <input ng-model="granteeInfo.startDate" to-date="granteeInfo.endDate"
                         ng-disabled="granteeInfo.permanent"
                         style="width:100px" type="text" datepicker/>
                </div>
              </td>
              <td>
                <div class="horizontal-input-group">
                  <input id="grant-end-date" ng-checked="granteeInfo.endDate"
                         ng-disabled="granteeInfo.permanent" type="checkbox" ng-click="setEndDate()"/>
                  <label for="grant-end-date">Set End Date</label>
                  <input ng-disabled="granteeInfo.permanent"
                         ng-model="granteeInfo.endDate" from-date="granteeInfo.startDate"
                         style="width:100px" type="text" datepicker/>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <hr/>
        <div class="content-info" style="text-align: center;">
          <input type="button" class="time-neutral-button" value="Discard Changes" ng-click="reset()"/>
          <input type="button" class="submit-button" ng-disabled="formNotFilledOut()" ng-click="saveGrants()" value="Grant Requester Access"/>
        </div>

        <p>You picked: {{granteeInfo.selectedGrantee.fullName}} with id of {{granteeInfo.selectedGrantee.empId}}
          from {{granteeInfo.startDate}} to {{granteeInfo.endDate}}</p>
      </div>
    </div>
  </div>
</div>