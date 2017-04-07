<div class="employee-select content-container content-controls">
  <p class="content-info" ng-if="supEmpGroups.length > 1">
    <span>
      View Employees Under Supervisor &nbsp;
    </span>
    <span>
      <select ng-model="$parent.iSelEmpGroup"
              ng-options="supEmpGroups.indexOf(eg) as eg.dropDownLabel
                          group by eg.group
                          for eg in supEmpGroups | filter:activeFilter">
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
                          for emp in allEmps | filter:activeFilter">
      </select>
    </span>
  </p>
</div>
