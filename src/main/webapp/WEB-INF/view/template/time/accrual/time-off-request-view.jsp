<%--
  Created by IntelliJ IDEA.
  User: senate
  Date: 8/19/19
  Time: 9:59 AM
  To change this template use File | Settings | File Templates.
--%>

<!-- This template creates a view of a single time-off
request and allows the user to edit, save, or submit the request. -->

<!-- Error messages that appear on submit if request invalid -->
<div class="ess-notification error" style="width:100%" ng-show="!validRequest">
  <p><strong style="color: white">Please fix the following errors in your request: </strong></p>
  <p ng-repeat="msg in validationErrorMessages" style="color: white">
    {{msg}}
  </p>
</div>

<div class="content-container" ng-init="onloadFn()">

  <div><p style="color: black"><b>Enter the dates and time for approval by your Time and Attendance supervisor.<br/>
    If the hours are approved you will still have to enter them in the time record for that date.</b></p></div>

  <h1>Review/Submit A Time Off Request</h1></div>
<!--Include their accruals-->
<div class="accrual-loader" ng-show="timeOffLoading">
  <h3 class="loading-text">Loading Accruals...</h3>
  <div loader-indicator class="sm-loader"></div>
</div>

<div class="accrual-hours-container" ng-hide="timeOffLoading" style>

  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption personal">Personal Hours</div>
      <div class="hours-display ng-binding">{{accruals.personal}}</div>
    </div>
  </div>

  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption vacation">Vacation Hours</div>
      <div class="hours-display ng-binding">{{accruals.vacation}}</div>
    </div>
  </div>

  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption sick">Sick Hours</div>
      <div class="hours-display ng-binding">{{accruals.sick}}</div>
    </div>
  </div>

</div>
<!--Go through all the days-->
<table class="ess-table time-record-entry-table">
  <thead>
  <tr class="time-record-row">
    <th ng-if="mode==='input'"></th>  <!-- Select check boxes -->
    <th>Date</th>
    <th>Work</th>
    <th>Holiday</th>
    <th>Vacation</th>
    <th>Personal</th>
    <th>Sick Emp</th>
    <th>Sick Fam</th>
    <th>Misc</th>
    <th>Misc Type</th>
    <th>Misc 2</th>
    <th>Misc 2 Type</th>
    <th>Total</th>
  </tr>
  </thead>
  <tbody>
  <tr ng-repeat="day in data.days" ng-if="mode==='input'">
    <!--INSERT ACCRUAL VALUES ROW HERE IF FIRST DAY IN PAY PERIOD IS TRUE-->
    <td><input id="time-off-request-checkbox" type="checkbox" ng-model="day.checked"/></td>
    <td><input id="first-date-picker" type="date" ng-model="day.date" ng-change="datePickerChanged(day)"/></td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.workHours" ng-change="updateTotals()"/></td>
    <td class="timeoff-table-hours holiday-hours-disable">{{day.holidayHours}}</td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.vacationHours" ng-change="updateTotals()"/></td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.personalHours" ng-change="updateTotals()"/></td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.sickEmpHours" ng-change="updateTotals()"/></td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.sickFamHours" ng-change="updateTotals()"/></td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.miscHours" ng-change="updateTotals()"/></td>
    <td class="timeoff-table-misc  misc-drop-down">
      <select ng-model="day.miscType" ng-options="miscLeave.type as miscLeave.shortName for miscLeave in miscTypeList">
        <option value="" selected="selected">Choose Type...</option>
      </select>
    </td>
    <td><input type="number" placeholder="--" onpaste="return false;"
               ng-model="day.misc2Hours" ng-change="updateTotals()"/></td>
    <td class="timeoff-table-misc  misc-drop-down">
      <select ng-model="day.miscType2" ng-options="miscLeave.type as miscLeave.shortName for miscLeave in miscTypeList">
        <option value="" selected="selected">Choose Type...</option>
      </select>
    </td>
    <td ng-bind="day.totalHours"></td>
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

    <td ng-switch="day.misc2Hours">
      <span ng-switch-when="null|0" ng-switch-when-separator="|">--</span>
      <span ng-switch-default>{{day.misc2Hours}}</span>
    </td>
    <td ng-if="day.miscType2 != null">{{day.miscType2}}</td>
    <td ng-if="day.miscType2 === null">--</td>
    <td>{{day.totalHours}}</td>
  </tr>
  </tbody>
</table>

<!-- Inital Datepicker and Add Day and Delete Selected buttons -->
<div class="time-off-request-buttons" ng-hide="timeOffLoading">
  <button ng-show="pageLoaded && data.days.length > 0" ng-click="deleteSelected()"
          ng-if="mode==='input' && data.status!=='APPROVED' && data.status!=='SUBMITTED'">Delete Selected
  </button>
  <button ng-show="pageLoaded" ng-if="mode==='input' && data.status!=='APPROVED' && data.status!=='SUBMITTED'"
          ng-click="addDay()">+ Add Another Date
  </button>
</div>

<!--Accruals available after the request-->
<div class="accrual-loader" ng-show="timeOffLoading">
  <h3 class="loading-text">Loading Projected Accruals...</h3>
  <div loader-indicator class="sm-loader"></div>
</div>

<div class="accrual-hours-container" ng-hide="timeOffLoading" style>

  <div class="hours-caption personal" style="background:rgb(92, 116, 116);color:white;font-weight: 600">Hours After
    Request:
  </div>

  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption personal">Personal Hours</div>
      <div class="hours-display ng-binding">{{accrualsPost.personal}}</div>
    </div>
  </div>

  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption vacation">Vacation Hours</div>
      <div class="hours-display ng-binding">{{accrualsPost.vacation}}</div>
    </div>
  </div>

  <div class="accrual-component">
    <div class="captioned-hour-square" style="float:left;">
      <div class="hours-caption sick">Sick Hours</div>
      <div class="hours-display ng-binding">{{accrualsPost.sick}}</div>
    </div>
  </div>

</div>

<!--Go though comments-->
<h3 ng-show="data.comments.length > 0 || mode==='input'">Comments:</h3>
<p style="color:black;" ng-show="data.comments.length < 1 && mode==='input'">This is the start of a comment thread
  between you and your {{otherContact}}:</p>
<div class="comment-list">
  <p class="comment" ng-repeat="comment in data.comments">
    <strong ng-if="comment.authorId === empId">Me:</strong>
    <strong ng-if="comment.authorId != empId">{{otherContact}}:</strong>
    &emsp;{{comment.text}}
  </p>
  <div ng-if="mode==='input'" class="new-comment-container">
    <p><strong class="comment">Me: </strong>{{comment.text}}</p>
    <textarea ng-model="data.addedComment"></textarea>
  </div>
</div>

<!--Save and Submit buttons-->
<div class="time-off-request-buttons" ng-if="pageLoaded" ng-hide="timeOffLoading">
  <button ng-if="mode==='input'" ng-disabled="data.days.length.isEmpty()" ng-click="saveRequest()"
          class="time-off-request-save-button">SAVE
  </button>
  <button ng-if="mode==='input'" ng-disabled="data.days.length.isEmpty()" ng-click="submitRequest()"
          class="time-off-request-submit-button">SUBMIT
  </button>
  <!-- Cannot edit a request if it has been submitted or approved-->
  <button ng-if="mode==='output' && data.status!=='APPROVED' && data.status!=='SUBMITTED'"
          ng-click="editMode()" class="time-off-request-edit-button">EDIT
  </button>
</div>
