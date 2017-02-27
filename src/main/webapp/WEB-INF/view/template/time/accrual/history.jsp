<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccrualHistoryCtrl" ng-init="showDialog = false">
    <div class="time-attendance-hero">
        <h2>Accrual History</h2>
    </div>
    <div class="content-container content-controls">
        <p class="content-info">Filter By Year &nbsp;
            <select ng-model="state.selectedYear" ng-change="getAccSummaries(state.selectedYear)"
                    ng-options="year for year in state.activeYears">
            </select>
        </p>
    </div>

    <ess-notification ng-show="state.searching === false && state.error !== null" level="warn"
                      title="{{state.error.title}}" message="{{state.error.message}}">
    </ess-notification>

    <div ng-show="state.isTe">
        <jsp:include page="te-accruals.jsp"/>
    </div>

    <div loader-indicator class="loader" ng-show="state.searching === true"></div>

    <div class="content-container" ng-show="state.searching === false">
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
                       float-thead-enabled="true" ng-model="state.accSummaries">
                    <thead>
                    <tr>
                        <th colspan="2">Pay Period</th>
                        <th colspan="4" class="">Personal Hours</th>
                        <th colspan="5" class="">Vacation Hours</th>
                        <th colspan="6" class="">Sick Hours</th>
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
                    <tr ng-repeat="record in state.accSummaries[state.selectedYear]"
                        ng-class="{'highlighted': record.payPeriod.current}">
                        <td>{{record.payPeriod.payPeriodNum}}</td>
                        <td>

                            <a target="_blank" title="Open a Printable View for this Record"
                               ng-href="{{getAccrualReportURL(record)}}">
                                {{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}
                            </a>


                        </td>
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
                        <td class="accrual-hours sick">{{record.sickEmpUsedDelta + record.sickFamUsedDelta}}</td>
                        <td class="accrual-hours sick">{{record.sickEmpUsed + record.sickFamUsed}}</td>
                        <td class="accrual-hours available-hours sick">{{record.sickAvailable}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div modal-container></div>
</section>