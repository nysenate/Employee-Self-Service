
<section ng-controller="EmployeeSearchCtrl" id="employee-search-page">
  <div class="time-attendance-hero">
    <h2>Employee Search</h2>
  </div>

  <employee-search selected-emp="selectedEmp"></employee-search>

  <div ng-if="selectedEmp">


    <ess-notification ng-hide="selectedEmp.active" class="employee-search-page-notification"
                      level="info" title="{{selectedEmp.fullName}} is not a current Senate employee.">
    </ess-notification>
    <ess-notification level="info" title="{{selectedEmp.fullName}} is a Senator"
                      ng-show="selectedEmp.senator" class="employee-search-page-notification">
      <p>
        They cannot use or project accruals. <br>
        They will not have any attendance or accrual history unless they were a non-senator employee in the past.
      </p>
    </ess-notification>

    <div ng-if="selectedEmp.active">
      <accrual-bar ng-if="showAccruals" accruals="accruals" loading="loadingAccruals"></accrual-bar>

      <allowance-bar ng-if="selectedEmp.payType === 'TE'"
                     allowance="allowance" loading="loadingAllowance"></allowance-bar>
    </div>

    <toggle-panel open="false" label="Attendance History">
      <record-history emp-sup-info="selectedEmp" hide-title="true"></record-history>
    </toggle-panel>

    <toggle-panel open="false" label="Accrual History" ng-if="showAccrualHistory">
      <accrual-history emp-sup-info="selectedEmp" hide-title="true"></accrual-history>
    </toggle-panel>

    <toggle-panel open="false" label="Accrual Projections"
                  ng-if="selectedEmp.payType !== 'TE' && !selectedEmp.senator">
      <accrual-projections emp-sup-info="selectedEmp" hide-title="true"></accrual-projections>
    </toggle-panel>

    <toggle-panel open="false" label="Allowance History" ng-if="showAllowanceHistory">
      <allowance-history emp-sup-info="selectedEmp" hide-title="true"></allowance-history>
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
