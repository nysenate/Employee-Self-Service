<div class="employee-select content-container content-controls">
  <p class="content-info" ng-if="validSupEmpGroupCount > 1">
    <span>
      View Employees Under Supervisor &nbsp;
    </span>
    <span>
      <select ng-model="$parent.iSelEmpGroup"
              ng-options="supEmpGroups.indexOf(eg) as eg.dropDownLabel
                          group by eg.group
                          for eg in supEmpGroups | filter:supEmpGroupFilter">
      </select>
    </span>
  </p>
  <p class="content-info">
    <span>
      View {{selectSubject}} for Employee &nbsp;
    </span>
    <span>
      <select ng-model="$parent.iSelEmp" ng-if="allEmps.length > 0" ng-change="empChange(iSelEmp)"
              ng-options="allEmps.indexOf(emp) as emp.dropDownLabel
                          group by emp.group
                          for emp in allEmps">
      </select>
    </span>
  </p>
</div>
<div ng-show="!request.supervisor && allEmps.length == 0">
  <ess-notification level="info" ng-show="validSupEmpGroupCount > 1">
    No valid Employee {{subject}}s can be viewed for for the selected supervisor.
  </ess-notification>
  <ess-notification level="info" ng-hide="validSupEmpGroupCount > 1">
    No valid Employee {{subject}}s are available for viewing.
  </ess-notification>
</div>
