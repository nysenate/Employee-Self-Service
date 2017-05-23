
<section>
  <div class="time-attendance-hero">
    <h2>Employee Allowed Hours</h2>
  </div>
  <employee-select selected-emp="selectedEmp"
                   select-subject="Allowed Hours"
                   active-only="true"
                   pay-type="TE">
  </employee-select>
  <allowance-status ng-if="selectedEmp.empId" emp-sup-info="selectedEmp">
  </allowance-status>
  <div modal-container>
    <modal modal-id="accrual-details">
      <div accrual-details></div>
    </modal>
  </div>
</section>
