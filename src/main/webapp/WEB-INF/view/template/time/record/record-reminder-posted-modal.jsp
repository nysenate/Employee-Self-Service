
<section id="record-reminder-posted" class="content-container content-controls" style="padding: 0 20px" title="Email Reminder Results">
  <p class="content-info no-bottom-margin">
    Email reminder results:
  </p>
  <hr>
  <table class="reminder-records" align="center">
    <tbody>
    <tr ng-repeat="record in sortedRecords = (records | orderBy:['employee.fullName', 'beginDate'])"
        ng-init="firstEmpRec = $first || record.employeeId !== sortedRecords[$index-1].employeeId;
                         lastEmpRec = $last || sortedRecords.length === 1 ||
                                record.employeeId !== sortedRecords[$index + 1].employeeId"
        ng-class="{'first-emp-rec': firstEmpRec, 'last-emp-rec': lastEmpRec}">
      <td>{{ firstEmpRec ? record.employee.fullName : '' }}</td>
      <td>({{record.beginDate | moment:'MM/DD'}} - {{record.endDate | moment:'MM/DD'}})</td>
      <td ng-if="record.wasReminderSent" class="success-bold-label" style="padding: 0 20px 0 5px">Success</td>
      <td ng-if="!record.wasReminderSent" class="error-bold-label" style="padding: 0 20px 0 5px">Failure!</td>
    </tr>
    </tbody>
  </table>
  <hr>
  <div class="input-container">
    <input ng-click="resolve()" class="submit-button" type="button" value="Ok"/>
  </div>
</section>
