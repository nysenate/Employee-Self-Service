<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccrualProjectionCtrl" ng-init="showDialog = false">
    <div class="time-attendance-hero">
        <h2>Accrual Projections</h2>
    </div>

    <div loader-indicator class="loader" ng-show="state.searching === true"></div>

    <ess-notification ng-show="state.searching === false && state.error !== null" level="warn"
                      title="{{state.error.title}}" message="{{state.error.message}}">
    </ess-notification>

    <div ng-show="state.isTe" class="margin-top-10">
        <jsp:include page="te-accruals.jsp"/>
    </div>

    <div class="content-container"
         ng-show="state.searching === false">
        <p class="content-info" ng-hide="state.projections.length > 0">
            No projections exist for this year.
        </p>
        <div ng-show="state.projections.length > 0">
            <p class="content-info">
                The following hours are projected and can be adjusted as time records are processed.<br/>
                Enter hours into the 'Use' column to view projected available hours. No changes will be saved.<br/>
                Click a row to view or print a detailed summary of projected accrual hours.
            </p>
            <div class="padding-10">
                <table class="detail-acc-history-table projections" float-thead-enabled="true"
                       float-thead="floatTheadOpts" ng-model="state.projections">
                    <thead>
                    <tr>
                        <th colspan="3">Pay Period</th>
                        <th colspan="2" class="">Personal Hours</th>
                        <th colspan="3" class="">Vacation Hours</th>
                        <th colspan="4" class="">Sick Hours</th>
                    </tr>
                    <tr>
                        <th class="pay-period">#</th>
                        <th class="date">Start Date</th>
                        <th class="date">End Date</th>
                        <th class="personal used-hours">Use</th>
                        <th class="personal available-hours">Avail</th>
                        <th class="vacation rate">Rate</th>
                        <th class="vacation used-hours">Use</th>
                        <th class="vacation available-hours">Avail</th>
                        <th class="sick rate">Rate</th>
                        <th class="sick used-hours">Emp Use</th>
                        <th class="sick used-hours">Fam Use</th>
                        <th class="sick available-hours">Avail</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="record in state.projections"
                        ng-class="{'highlighted': record.payPeriod.current}"
                        ng-attr-id="{{$last && 'earliest-projection' || undefined}}"
                        title="Open a Detail View of this Record"
                    >
                        <td class="pay-period" ng-click="viewDetails(record)">
                            {{record.payPeriod.payPeriodNum}}
                        </td>
                        <td class="date" ng-click="viewDetails(record)">
                            {{record.payPeriod.startDate | moment:'MM/DD/YYYY'}}
                        </td>
                        <td class="date" ng-click="viewDetails(record)">
                            {{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}
                        </td>
                        <td class="accrual-hours personal used-hours"
                            title="Project Personal Hour Usage"
                        >
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].biweekPersonalUsed"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                        </td>
                        <td class="accrual-hours personal available-hours" ng-click="viewDetails(record)">
                            {{record.personalAvailable}}
                        </td>
                        <td class="accrual-hours vacation rate" ng-click="viewDetails(record)">
                            {{record.vacationRate}}
                        </td>
                        <td class="accrual-hours vacation used-hours"
                            title="Project Vacation Hour Usage"
                        >
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].biweekVacationUsed"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                        </td>
                        <td class="accrual-hours vacation available-hours" ng-click="viewDetails(record)">
                            {{record.vacationAvailable}}
                        </td>
                        <td class="accrual-hours sick rate" ng-click="viewDetails(record)">
                            {{record.sickRate}}
                        </td>
                        <td class="accrual-hours sick used-hours"
                            title="Project Employee Sick Hour Usage"
                        >
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].biweekSickEmpUsed"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);" />
                        </td>
                        <td class="accrual-hours sick used-hours"
                            title="Project Family Sick Hour Usage"
                        >
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].biweekSickFamUsed"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);" />
                        </td>
                        <td class="accrual-hours sick available-hours" ng-click="viewDetails(record)">
                            {{record.sickAvailable}}
                        </td>

                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div modal-container>
        <modal modal-id="accrual-details">
            <div accrual-details></div>
        </modal>
    </div>
</section>
