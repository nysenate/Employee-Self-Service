
<section ng-controller="EmployeeSearchCtrl">
  <div class="time-attendance-hero">
    <h2>Employee Search</h2>
  </div>

  <employee-search selected-emp="selectedEmp"></employee-search>

  <div ng-if="selectedEmp.senator" class="margin-top-20">
    <ess-notification level="info" title="{{selectedEmp.fullName}} is a Senator">
      <p>
        They cannot use or project accruals. <br>
        They will not have any attendance or accrual history unless they were a non-senator employee in the past.
      </p>
    </ess-notification>
  </div>

  <div ng-if="accruals">
    <accrual-bar accruals="accruals" loading="loadingAccruals"></accrual-bar>
  </div>

  <div ng-if="allowance" class="margin-top-10">
    <allowance-bar allowance="allowance" loading="loadingAllowance"></allowance-bar>
  </div>

  <div ng-if="selectedEmp">
    <toggle-panel open="false" label="Attendance History">
      <record-history emp-sup-info="selectedEmp" hide-title="true"></record-history>
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
