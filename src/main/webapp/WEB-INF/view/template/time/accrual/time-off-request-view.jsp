<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/19/19
  Time: 9:59 AM
  To change this template use File | Settings | File Templates.
--%>

<!--  This template creates a view of a single time-off
      request and allows the user to edit, save, or submit the request. -->

<!-- Error messages that appear on submit if request invalid -->
<div class="validationErrorContainer" ng-show="!validRequest">
  <p><strong>Please fix the following errors in your request: </strong></p>
  <p ng-repeat="msg in validationErrorMessages">
    {{msg}}
  </p>
</div>

<div class="content-container" ng-init="onloadFn()" ><h1>Review/Submit A Time Off Request</h1></div>
<!--Include their accruals-->
<p class="time-off-request-accruals" >&ensp;&ensp;Available Hours: &emsp; <span class="vacation-text">Vacation: {{accruals.vacation}}&ensp;</span>
                           <span class="personal-text">Personal: {{accruals.personal}}&ensp;</span>
                           <span class="sick-text">Sick: {{accruals.sick}}&ensp;</span></p>
<!--Go through all the days-->
<table class="time-off-request-view-table timeoff-request-table" >
  <thead>
    <tr>
      <th class="timeoff-table-checkbox" ng-if="mode==='input'"></th>  <!-- Select check boxes -->
      <th class="timeoff-table-date">Date</th>
      <th class="timeoff-table-hours">Work</th>
      <th class="timeoff-table-hours">Holiday</th>
      <th class="timeoff-table-hours vacation-text">Vacation</th>
      <th class="timeoff-table-hours personal-text">Personal</th>
      <th class="timeoff-table-hours sick-text">Sick Emp</th>
      <th class="timeoff-table-hours sick-text">Sick Fam</th>
      <th class="timeoff-table-hours">Misc</th>
      <th class="timeoff-table-misc">Misc Leave Type</th>
      <th class="timeoff-table-hours">Total</th>
    </tr>
  </thead>
  <tbody>
    <tr ng-repeat="day in data.days" ng-if="mode==='input'">
      <!--INSERT ACCRUAL VALUES ROW HERE IF FIRST DAY IN PAY PERIOD IS TRUE-->
      <td class="timeoff-table-checkbox"><input id="time-off-request-checkbox" type="checkbox" ng-model="day.checked"/></td>
      <td class="timeoff-table-date"><input id="first-date-picker" type="date" ng-model="day.date" ng-change="datePickerChanged(day)"/></td>
      <td class="timeoff-table-hours"><input type="number" min="0" max="24" step="0.5" placeholder="--" onpaste="return false;" ng-model="day.workHours" ng-change="updateTotals()" /></td>
      <td class="timeoff-table-hours holiday-hours-disable">{{day.holidayHours}}</td>
      <td class="timeoff-table-hours"><input type="number" min="0" max="24" step="0.5" placeholder="--" onpaste="return false;" ng-model="day.vacationHours" ng-change="updateTotals()"/></td>
      <td class="timeoff-table-hours"><input type="number" min="0" max="24" step="0.5" placeholder="--" onpaste="return false;" ng-model="day.personalHours" ng-change="updateTotals()"/></td>
      <td class="timeoff-table-hours"><input type="number" min="0" max="24" step="0.5" placeholder="--" onpaste="return false;" ng-model="day.sickEmpHours" ng-change="updateTotals()"/></td>
      <td class="timeoff-table-hours"><input type="number" min="0" max="24" step="0.5" placeholder="--" onpaste="return false;" ng-model="day.sickFamHours" ng-change="updateTotals()"/></td>
      <td class="timeoff-table-hours"><input type="number" min="0" max="24" step="0.5" placeholder="--" onpaste="return false;" ng-model="day.miscHours" ng-change="updateTotals()"/></td>
      <td class="timeoff-table-misc  misc-drop-down">
        <select ng-model="day.miscType" ng-options="miscLeave.type as miscLeave.shortName for miscLeave in miscTypeList">
          <option value="" selected="selected">Choose Type...</option>
        </select>
      </td>
      <td class="timeoff-table-hours" ng-bind="day.totalHours"></td>
    </tr>
    <tr ng-repeat="day in data.days" ng-if="mode==='output'">
      <td>{{day.dateStr}}</td>
      <td ng-switch="day.workHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.workHours}}</span>
      </td>
      <td ng-switch="day.holidayHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.holidayHours}}</span>
      </td>
      <td ng-switch="day.vacationHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.vacationHours}}</span>
      </td>
      <td ng-switch="day.personalHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.personalHours}}</span>
      </td>
      <td ng-switch="day.sickEmpHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.sickEmpHours}}</span>
      </td>
      <td ng-switch="day.sickFamHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.sickFamHours}}</span>
      </td>
      <td ng-switch="day.miscHours">
        <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
        <span ng-switch-default>{{day.miscHours}}</span>
      </td>
      <td ng-if="day.miscType != null">{{day.miscType}}</td>
      <td ng-if="day.miscType === null">--</td>
      <td>{{day.totalHours}}</td>
    </tr>
  </tbody>
</table>

<!--Accruals available after the request-->
<p class="time-off-request-accruals" ng-if="data.days.length > 0" >Hours After Request: &emsp;
  <span class="vacation-text">Vacation: {{accrualsPost.vacation}}&ensp;</span>
  <span class="personal-text">Personal: {{accrualsPost.personal}}&ensp;</span>
  <span class="sick-text">Sick: {{accrualsPost.sick}}&ensp;</span>
</p>


<!-- Inital Datepicker and Add Day and Delete Selected buttons -->
<div class="time-off-request-buttons" ng-show="mode==='input' && pageLoaded">
  <button ng-show="pageLoaded && data.days.length > 0" ng-click="deleteSelected()">Delete Selected</button>
  <button ng-click="addDay()">+ Add Another Date</button>
</div>

<!--Go though comments-->
<h3 ng-show="data.comments.length > 0 || mode==='input'">Comments:</h3>
<p style="color:black;" ng-show="data.comments.length < 1 && mode==='input'">This is the start of a comment thread between you and your {{otherContact}}:</p>
<div class="comment-list">
  <p class="comment" ng-repeat="comment in data.comments">
    <strong ng-if="comment.authorId === empId">Me:</strong>
    <strong ng-if="comment.authorId != empId">{{otherContact}}:</strong>
    &emsp;{{comment.text}}
  </p>
  <div ng-if="mode==='input'" class="new-comment-container">
    <p><strong class="comment">Me: </strong>{{comment.text}}</p>
    <textarea ng-model="addedComment"></textarea>
  </div>
</div>

<!--Save and Submit buttons-->
<div class="time-off-request-buttons" ng-if="pageLoaded">
  <button ng-if="mode==='input'" ng-click="saveRequest()" class="time-off-request-save-button">SAVE</button>
  <button ng-if="mode==='input'" ng-click="submitRequest()" class="time-off-request-submit-button">SUBMIT</button>
  <!-- Cannot edit a request if it has been submitted or approved-->
  <button ng-if="mode==='output' && data.status!=='APPROVED' && data.status!=='SUBMITTED'"
          ng-click="editMode()" class="time-off-request-edit-button">EDIT</button>
</div>
