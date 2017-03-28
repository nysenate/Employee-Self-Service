
<section>
  <div class="time-attendance-hero">
    <h2>Employee Accrual Projections</h2>
  </div>
  <employee-select selected-emp="selectedEmp" select-subject="Accrual Projections" active-only="true">
  </employee-select>
  <accrual-projections ng-if="selectedEmp.empId" emp-sup-info="selectedEmp">
  </accrual-projections>
  <div modal-container>
    <modal modal-id="accrual-details">
      <div accrual-details></div>
    </modal>
  </div>
</section>

