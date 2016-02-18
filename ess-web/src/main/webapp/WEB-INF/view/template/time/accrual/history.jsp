<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccrualHistoryCtrl" ng-init="showDialog = false">
    <div class="time-attendance-hero">
        <h2>Accrual Summary and Projections</h2>
    </div>
    <div class="content-container content-controls">
        <p class="content-info">Filter By Year &nbsp;
            <select ng-model="state.selectedYear" ng-change="getAccSummaries(state.selectedYear)"
                    ng-options="year for year in state.activeYears">
            </select>
        </p>
    </div>

    <div loader-indicator ng-show="state.searching === true"></div>

    <ess-notification ng-show="state.searching === false && state.error !== null" level="warn"
                      title="{{state.error.title}}" message="{{state.error.message}}">
    </ess-notification>

    <toggle-panel open="true" label="Accrual Projections" ng-if="state.searching === false && state.projections[state.selectedYear].length > 0">
        <p class="content-info">The following hours are projected and can be adjusted as time records are processed.<br/>
            Toggle the hours in the 'Use' column to get the projected available hours. No changes will be saved.</p>
        <div class="padding-10">
            <table class="detail-acc-history-table">
                <thead>
                <tr>
                    <th colspan="3">Pay Period</th>
                    <th colspan="2" class="">Personal Hours</th>
                    <th colspan="3" class="">Vacation Hours</th>
                    <th colspan="3" class="">Sick Hours</th>
                </tr>
                <tr>
                    <th>#</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th class="personal">Use</th>
                    <th class="personal">Avail</th>
                    <th class="vacation">Rate</th>
                    <th class="vacation">Use</th>
                    <th class="vacation">Avail</th>
                    <th class="sick">Rate</th>
                    <th class="sick">Used</th>
                    <th class="sick">Avail</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="record in state.projections[state.selectedYear]"
                    ng-class="{'highlighted': record.payPeriod.current}">
                    <td>{{record.payPeriod.payPeriodNum}}</td>
                    <td>{{record.payPeriod.startDate | moment:'MM/DD/YYYY'}}</td>
                    <td>{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
                    <td class="accrual-hours personal">
                        <input type="number" min="0" step=".5" placeholder="0"
                                ng-model="$parent.state.projections[state.selectedYear][$index].personalUsedDelta"
                                ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                    </td>
                    <td class="accrual-hours available-hours personal">{{record.personalAvailable}}</td>
                    <td class="accrual-hours vacation">{{record.vacationRate}}</td>
                    <td class="accrual-hours vacation">
                        <input type="number" min="0" step=".5" placeholder="0"
                               ng-model="$parent.state.projections[state.selectedYear][$index].vacationUsedDelta"
                               ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                    </td>
                    <td class="accrual-hours available-hours vacation">{{record.vacationAvailable}}</td>
                    <td class="accrual-hours sick">{{record.sickRate}}</td>
                    <td class="accrual-hours sick">
                        <input type="number" min="0" step=".5" placeholder="0"
                               ng-model="$parent.state.projections[state.selectedYear][$index].sickUsedDelta"
                               ng-change="recalculateProjectionTotals(state.selectedYear);" />
                    </td>
                    <td class="accrual-hours available-hours sick">{{record.sickAvailable}}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </toggle-panel>


    <div class="content-container" ng-show="state.searching === false && state.accSummaries[state.selectedYear].length > 0">
        <h1 class="teal">Accrual History</h1>
        <p class="content-info">
            Summary of historical accrual records.
        </p>
        <div class="padding-10">
            <table class="detail-acc-history-table" float-thead="floatTheadOpts" ng-model="state.accSummaries[state.selectedYear]">
                <thead>
                <tr>
                    <th colspan="2">Pay Period</th>
                    <th colspan="4" class="">Personal Hours</th>
                    <th colspan="5" class="">Vacation Hours</th>
                    <th colspan="5" class="">Sick Hours</th>
                </tr>
                <tr>
                    <th>#</th>
                    <th>End Date</th>
                    <th class="personal">Accrued</th>
                    <th class="personal">Used</th>
                    <th class="personal">Used Ytd</th>
                    <th class="personal">Avail</th>
                    <th class="vacation">Rate</th>
                    <th class="vacation">Accrued</th>
                    <th class="vacation">Used</th>
                    <th class="vacation">Used Ytd</th>
                    <th class="vacation">Avail</th>
                    <th class="sick">Rate</th>
                    <th class="sick">Accrued</th>
                    <th class="sick">Used</th>
                    <th class="sick">Used Ytd</th>
                    <th class="sick">Avail</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="record in state.accSummaries[state.selectedYear]" ng-class="{'highlighted': record.payPeriod.current}">
                    <td>{{record.payPeriod.payPeriodNum}}</td>
                    <td >{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
                    <td class="accrual-hours personal">{{record.personalAccruedYtd}}</td>
                    <td class="accrual-hours personal">{{record.personalUsedDelta}}</td>
                    <td class="accrual-hours personal">{{record.personalUsed}}</td>
                    <td class="accrual-hours available-hours personal">{{record.personalAvailable}}</td>
                    <td class="accrual-hours vacation">{{record.vacationRate}}</td>
                    <td class="accrual-hours vacation">{{record.vacationAccruedYtd + record.vacationBanked}}</td>
                    <td class="accrual-hours vacation">{{record.vacationUsedDelta}}</td>
                    <td class="accrual-hours vacation">{{record.vacationUsed}}</td>
                    <td class="accrual-hours available-hours vacation">{{record.vacationAvailable}}</td>
                    <td class="accrual-hours sick">{{record.sickRate}}</td>
                    <td class="accrual-hours sick">{{record.sickAccruedYtd}}</td>
                    <td class="accrual-hours sick">{{record.sickUsedDelta}}</td>
                    <td class="accrual-hours sick">{{record.empSickUsed + record.famSickUsed}}</td>
                    <td class="accrual-hours available-hours sick">{{record.sickAvailable}}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div modal-container></div>
</section>