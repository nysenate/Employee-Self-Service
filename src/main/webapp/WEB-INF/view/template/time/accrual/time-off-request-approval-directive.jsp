<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/14/19
  Time: 2:41 PM
  To change this template use File | Settings | File Templates.
--%>

<!-- This directive will provide the table needed for supervisors.
     It will be the table that holds the requests needing approval
     and the requests that have been approved, but could be rejected
     if the supervisor chooses to reject them. -->
<h5 ng-show="format==='pending'">Select pending requests in the table below and click 'Review Selected'
     <br>at the bottom to review the record details and either approve or reject them.</h5>
<h5 ng-show="format==='approved'">Select approved requests in the table below and click,'Review Selected'
     <br>at the bottom to review the record details or change their status.</h5>
<table class="time-off-request-approval-table" ng-show="requests.length > 0">
     <thead>
       <tr>
          <th class="time-off-approval-table-emp-column">Employee</th>
          <th class="time-off-approval-table-select-column">Select</th>
          <th class="time-off-approval-table-dates-column">Dates Affected</th>
          <th class="time-off-approval-table-hours-column">Total Hours</th>
          <th class="time-off-approval-table-chips-column"></th> <!--Blank header for the chips column-->
       </tr>
     </thead>
     <tbody>
     <!--Loop through all current time off requests and display them in tabular form  -->
     <!--chips in each row will be given a classname of md-chip-->
          <tr ng-repeat="request in requests">
               <td class="time-off-approval-table-emp-column">{{request.name}}</td>
               <td class="time-off-approval-table-select-column">
                    <input id="time-off-request-checkbox"
                           type="checkbox" ng-model="request.checked"/>
               </td>
               <td class="time-off-approval-table-dates-column">{{request.startDatePrint}} - {{request.endDatePrint}}</td>
               <td class="time-off-approval-table-hours-column">{{request.totalHours}}</td>
               <td class="time-off-table-hour-types chip-container">
                  <div class="md-chip" ng-repeat="type in request.accrualTypes"
                       ng-class="{vacation: type === 'VACATION', personal: type === 'PERSONAL', sick: type === 'SICKEMP' || type === 'SICKFAM'}">
                    {{type | timeOffRequestAccrualType}}
                  </div>
                  <div class="md-chip misc" ng-repeat="type in request.miscTypes">
                    {{type | miscLeave}}
                  </div>
               </td>
          </tr>
     </tbody>
</table>
<div ng-show="requests.length < 1 && format==='pending'" class="no-requests">
     No Pending Time Off Requests
</div>
<div ng-show="requests.length < 1 && format==='approved'" class="no-requests">
     No Approved Time Off Requests
</div>