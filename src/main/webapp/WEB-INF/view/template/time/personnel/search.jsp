
<section ng-controller="EmployeeSearchCtrl">
  <div class="time-attendance-hero">
    <h2>Employee Search</h2>
  </div>

  <employee-search selected-emp="selectedEmp"></employee-search>

  <div ng-if="selectedEmp">
    <toggle-panel open="false" label="Attendance History">
      <record-history emp-sup-info="selectedEmp" hide-title="true"></record-history>
    </toggle-panel>
  </div>

  <div ng-if="selectedEmp && selectedEmp.payType === 'TE'">
    <toggle-panel open="false" label="Allowed Hours">
      <allowance-status emp-sup-info="selectedEmp" hide-title="true">
      </allowance-status>
    </toggle-panel>
  </div>

  <div ng-if="selectedEmp">
    <toggle-panel open="false" label="Accrual History">
      <accrual-history emp-sup-info="selectedEmp" hide-title="true"></accrual-history>
    </toggle-panel>
  </div>

  <div ng-if="selectedEmp && selectedEmp.payType !== 'TE' && !selectedEmp.senator">
    <toggle-panel open="false" label="Accrual Projections">
      <accrual-projections emp-sup-info="selectedEmp" hide-title="true"></accrual-projections>
    </toggle-panel>
  </div>

  <div modal-container>
    <modal modal-id="record-details">
      <div record-detail-modal></div>
    </modal>
    <modal modal-id="accrual-details">
      <div accrual-details></div>
    </modal>
  </div>
</section>
