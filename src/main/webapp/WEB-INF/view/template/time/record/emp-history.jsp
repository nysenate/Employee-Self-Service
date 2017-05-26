<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section id="emp-history">
  <div class="time-attendance-hero">
    <h2>Employee Attendance History</h2>
  </div>
  <employee-select selected-emp="selectedEmp" select-subject="Attendance Records">
  </employee-select>

  <record-history ng-if="selectedEmp.empId" emp-sup-info="selectedEmp"></record-history>

  <div modal-container>
    <modal modal-id="record-details">
      <div record-detail-modal></div>
    </modal>
  </div>
</section>

