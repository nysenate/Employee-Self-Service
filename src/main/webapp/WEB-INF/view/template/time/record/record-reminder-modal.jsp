
<section id="record-reminder-prompt" class="content-container content-controls" title="Send Email Reminders">
    <p class="content-info no-bottom-margin">
        The following employees will have an email reminder sent for the listed records.
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
            </tr>
        </tbody>
    </table>
    <hr>
    <div class="input-container">
        <input ng-click="resolve()" class="submit-button" type="button" value="Send"/>
        <input ng-click="reject()" class="reject-button" type="button" value="Cancel"/>
    </div>
</section>
