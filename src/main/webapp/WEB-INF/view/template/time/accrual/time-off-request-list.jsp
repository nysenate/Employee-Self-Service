<table class="timeoff-request-table" ng-show="data.length > 0"> <!--Only show if they have records-->
  <thead>
  <tr>
    <th class="timeoff-table-date-range">Date Range</th>
    <th class="timeoff-table-status">Status</th>
    <th class="timeoff-table-total-hours">Total Hours</th>
    <th class="timeoff-table-hour-types"></th>
  </tr>
  </thead>
  <tbody>
  <!--Loop through all current time off requests and display them in tabular form  -->
  <!--chips in each row will be given a classname of md-chip-->
    <tr ng-repeat="request in data">
      <td class="timeoff-table-date-range">{{request.startDate}} - {{request.endDate}}</td>
      <td class="timeoff-table-status">{{request.status | timeOffRequestStatus}}</td>
      <td class="timeoff-table-total-hours">{{request.totalHours}}</td>
      <td class="timeoff-table-hour-types chip-container" >
        <div class="md-chip" ng-class="{vacation: type === 'VACATION', personal: type === 'PERSONAL',
                        sick: type === 'SICKEMP' || type === 'SICKFAM'}" ng-repeat="type in request.accrualTypes">
          {{type | timeOffRequestAccrualType}}
        </div>
        <div class="md-chip misc" ng-repeat="type in request.miscTypes">
          {{type | miscLeave}}
        </div>
      </td>
    </tr>
  </tbody>
</table>
<div ng-show="data.length < 1" class="no-requests" > <!--Only show if they have no requests-->
  No Time Off Requests
</div>