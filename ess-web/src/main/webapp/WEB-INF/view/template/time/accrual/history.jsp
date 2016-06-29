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

    <div loader-indicator class="loader" ng-show="state.searching === true"></div>

    <ess-notification ng-show="state.searching === false && state.error !== null" level="warn"
                      title="{{state.error.title}}" message="{{state.error.message}}">
    </ess-notification>

    <div class="content-container ess-tabs"
         ng-show="state.searching === false">
        <div class="tab-selector">
            <a href="javascript:void(0)" ng-click="activeAccrualTab = 'history'"
               ng-class="{active: !activeAccrualTab || activeAccrualTab === 'history'}">
                Accrual History
            </a>
            <a href="javascript:void(0)" ng-click="activeAccrualTab = 'projections'"
               ng-class="{active: activeAccrualTab === 'projections'}">
               Accrual Projections
            </a>
        </div>
        <div ng-show="!activeAccrualTab || activeAccrualTab === 'history'">
            <p class="content-info" ng-hide="state.accSummaries[state.selectedYear].length > 0">
                No historical accrual records exist for this year.
                If it is early in the year they may not have been created yet.
            </p>
            <div ng-show="state.accSummaries[state.selectedYear].length > 0">
                <p class="content-info">
                    Summary of historical accrual records.
                </p>
                <div class="padding-10">
                    <table class="detail-acc-history-table" float-thead="floatTheadOpts"
                           float-thead-enabled="true" ng-model="state.accSummaries[state.selectedYear]">
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
        </div>
        <div ng-show="activeAccrualTab === 'projections'">
            <p class="content-info" ng-hide="state.projections[state.selectedYear].length > 0">
                No projections exist for this year.
            </p>
            <div ng-show="state.projections[state.selectedYear].length > 0">
                <p class="content-info">
                    The following hours are projected and can be adjusted as time records are processed.<br/>
                    Enter hours into the 'Use' column to view projected available hours. No changes will be saved.
                </p>
                <div class="padding-10">
                    <table class="detail-acc-history-table projections" float-thead-enabled=true
                           float-thead="floatTheadOpts" ng-model="state.projections[state.selectedYear]">
                        <thead>
                        <tr>
                            <th colspan="3">Pay Period</th>
                            <th colspan="2" class="">Personal Hours</th>
                            <th colspan="3" class="">Vacation Hours</th>
                            <th colspan="3" class="">Sick Hours</th>
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
                            <th class="sick used-hours">Used</th>
                            <th class="sick available-hours">Avail</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="record in state.projections[state.selectedYear]"
                            ng-class="{'highlighted': record.payPeriod.current}"
                            ng-attr-id="{{$last && 'earliest-projection' || undefined}}">
                            <td class="pay-period">{{record.payPeriod.payPeriodNum}}</td>
                            <td class="date">{{record.payPeriod.startDate | moment:'MM/DD/YYYY'}}</td>
                            <td class="date">{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
                            <td class="accrual-hours personal used-hours">
                                <input type="number" min="0" step=".5" placeholder="0"
                                       ng-model="$parent.state.projections[state.selectedYear][$index].personalUsedDelta"
                                       ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                            </td>
                            <td class="accrual-hours personal available-hours">{{record.personalAvailable}}</td>
                            <td class="accrual-hours vacation rate">{{record.vacationRate}}</td>
                            <td class="accrual-hours vacation used-hours">
                                <input type="number" min="0" step=".5" placeholder="0"
                                       ng-model="$parent.state.projections[state.selectedYear][$index].vacationUsedDelta"
                                       ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                            </td>
                            <td class="accrual-hours vacation available-hours">{{record.vacationAvailable}}</td>
                            <td class="accrual-hours sick rate">{{record.sickRate}}</td>
                            <td class="accrual-hours sick used-hours">
                                <input type="number" min="0" step=".5" placeholder="0"
                                       ng-model="$parent.state.projections[state.selectedYear][$index].sickUsedDelta"
                                       ng-change="recalculateProjectionTotals(state.selectedYear);" />
                            </td>
                            <td class="accrual-hours sick available-hours">{{record.sickAvailable}}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div modal-container></div>
</section>