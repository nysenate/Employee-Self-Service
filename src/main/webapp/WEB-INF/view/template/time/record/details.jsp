<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="gov.nysenate.ess.time.service.attendance.SfmsAttendanceReportUrlService" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
  ApplicationContext ac = RequestContextUtils.findWebApplicationContext(request);
  SfmsAttendanceReportUrlService reportUrlService = (SfmsAttendanceReportUrlService) ac.getBean("sfmsurlservice");
  request.setAttribute("reportUrl", reportUrlService.getAccrualReportBaseUrl());
%>

<div class="grid">
  <h3 class="content-info" style="margin-bottom:0;">
    Attendance record for {{record.employee.fullName}} from {{record.beginDate | moment:'l'}} to {{record.endDate | moment:'l'}}
  </h3>
  <accrual-bar ng-show="showAccruals"
               accruals="accrual"
               loading="loadingAccruals">
  </accrual-bar>
  <allowance-bar ng-show="showAllowance"
                 submitted="true"
                 allowance="allowance"
                 loading="loadingAllowance"
                 temp-work-hours="record.totals.tempWorkHours">
  </allowance-bar>
  <div class="col-10-12">
    <div class="temp" ng-if="tempEntries">
      <h1 class="attendance-entry-sub-table-title margin-10" ng-if="annualEntries">Temporary Pay Entries</h1>
      <table class="attendance-entry-sub-table ess-table">
        <thead>
        <tr>
          <th class="day-col">Day</th>
          <th class="date-col">Date</th>
          <th class="hour-col">Work</th>
          <th>Work Time Description / Comments</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="entry in record.timeEntries | filter:{payType: 'TE'}">
          <td>{{entry.date | moment:'ddd'}}</td>
          <td>{{entry.date | moment:'l'}}</td>
          <td>{{entry.workHours | entryHours}}</td>
          <td class="entry-comment">{{entry.empComment}}</td>
        </tr>
        <tr class="time-totals-row" ng-if="!annualEntries">
          <td></td>
          <td><strong>Record Totals</strong></td>
          <td><strong>{{record.totals.tempWorkHours}}</strong></td>
          <td></td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="ra-sa" ng-if="annualEntries">
      <h1 class="attendance-entry-sub-table-title margin-10" ng-if="tempEntries">Regular/Special Annual Pay Entries</h1>
      <table class="attendance-entry-sub-table ess-table">
        <thead>
        <tr>
          <th class="day-col">Day</th>
          <th class="date-col">Date</th>
          <th class="hour-col">Work</th>
          <th class="hour-col">Holiday</th>
          <th class="hour-col">Vacation</th>
          <th class="hour-col">Personal</th>
          <th class="hour-col">Sick Emp</th>
          <th class="hour-col">Sick Fam</th>
          <th class="hour-col">Misc</th>
          <th>Misc Type</th>
          <th class="hour-col">Total</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="entry in record.timeEntries | filter:{payType: '!TE'}">
          <td>{{entry.date | moment:'ddd'}}</td>
          <td>{{entry.date | moment:'l'}}</td>
          <td>{{entry.workHours | entryHours}}</td>
          <td>{{entry.holidayHours | entryHours}}</td>
          <td>{{entry.vacationHours | entryHours}}</td>
          <td>{{entry.personalHours | entryHours}}</td>
          <td>{{entry.sickEmpHours | entryHours}}</td>
          <td>{{entry.sickFamHours | entryHours}}</td>
          <td>{{entry.miscHours | entryHours}}</td>
          <td>{{entry.miscType | miscLeave}}</td>
          <td>{{entry.total}}</td>
        </tr>
        <tr class="time-totals-row">
          <td></td>
          <td><strong>Record Totals</strong></td>
          <td><strong>{{record.totals.workHours}}</strong></td>
          <td><strong>{{record.totals.holidayHours}}</strong></td>
          <td><strong>{{record.totals.vacationHours}}</strong></td>
          <td><strong>{{record.totals.personalHours}}</strong></td>
          <td><strong>{{record.totals.sickEmpHours}}</strong></td>
          <td><strong>{{record.totals.sickFamHours}}</strong></td>
          <td><strong>{{record.totals.miscHours}}</strong></td>
          <td></td>
          <td><strong>{{record.totals.total}}</strong></td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div class="col-2-12">

    <h3 class="content-info">Notes</h3>
    <div class="record-details-section">
      <label ng-show="!record.remarks">This time record has no notes.</label>
      <span ng-bind="record.remarks"></span>
    </div>

    <h3 class="content-info">Supervisor</h3>
    <div class="record-details-section">
      <span>{{record.supervisor.fullName}}</span>
    </div>

    <h3 class="content-info">Status</h3>
    <div class="record-details-section">
      <span>{{record.recordStatus | timeRecordStatus}}</span>
    </div>

    <h3 class="content-info">Actions</h3>
    <div class="record-details-section">
      <a target="_blank" title="Open a Printable View for this Record"
         ng-href="${reportUrl}?report=PRTIMESHEET23&cmdkey=tsuser&p_stamp=N&p_nuxrtimesheet={{record.timeRecordId}}">
        Print Record
      </a>
      <div ng-if="showExitBtn">
        <br/>
        <br/>
        <a ng-click="close()" title="Close this window">Exit</a>
      </div>
    </div>
  </div>
</div>
