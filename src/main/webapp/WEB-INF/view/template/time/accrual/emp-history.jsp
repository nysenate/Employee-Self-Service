
<section>
  <div class="time-attendance-hero">
    <h2>Employee Accrual History</h2>
  </div>
  <employee-select selected-emp="selectedEmp"
                   select-subject="Accrual History">
  </employee-select>
  <accrual-history ng-if="selectedEmp.empId" emp-sup-info="selectedEmp">
  </accrual-history>
  <div modal-container>
    <modal modal-id="accrual-details">
      <div accrual-details></div>
    </modal>
  </div>
</section>