<div class="padding-10">
  <table class="ess-table approve-attendance-rec-table">
    <thead>
    <tr>
      <th colspan="1">Employee</th>
      <th ng-if="selectedIndices">Select</th>
      <th>Pay Period</th>
      <th>Work</th>
      <th>Holiday</th>
      <th>Vacation</th>
      <th>Personal</th>
      <th>Sick Emp</th>
      <th>Sick Fam</th>
      <th>Misc</th>
      <th>Total Hours</th>
    </tr>
    </thead>
    <tbody>
    <tr ng-repeat="record in records" ng-click="(selectedIndices) ? toggleSelected($index) : showDetails(record)"
        ng-init="showName = ($first == true || $parent.records[$index - 1].employeeId !== record.employeeId)"
        ng-class="{'active': selectedIndices[$index] === true, 'name-row': showName}" title="Select record">
      <%--
      Display the name only once for each employee's list of records.
      When clicked, it should toggle all of the records for that employee.
      --%>
      <td ng-click="toggleRecsForEmp(record); $event.stopPropagation();">
        <div ng-if="showName">
          {{record.employee.fullName || record.employeeId}}
          <br/>
          <small class="light-teal">Supervisor: {{record.supervisor.lastName}}</small>
        </div>
      </td>
      <td ng-if="selectedIndices" style="text-align: center;">
        <input type="checkbox" ng-checked="selectedIndices[$index] === true"/>
      </td>
      <td>{{record.beginDate | moment:'l'}} - {{record.endDate | moment:'l'}}</td>
      <td>{{record.totals.workHours}}</td>
      <td>{{record.totals.holidayHours}}</td>
      <td>{{record.totals.vacationHours}}</td>
      <td>{{record.totals.personalHours}}</td>
      <td>{{record.totals.sickEmpHours}}</td>
      <td>{{record.totals.sickFamHours}}</td>
      <td>{{record.totals.miscHours}}</td>
      <td>{{record.totals.total}}</td>
    </tr>
    </tbody>
  </table>

</div>
