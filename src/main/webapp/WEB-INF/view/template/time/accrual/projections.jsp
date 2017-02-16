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
                Enter hours into the 'Use' column to view projected available hours. No changes will be saved.
            </p>
            <div class="padding-10">
                <table class="detail-acc-history-table projections" float-thead-enabled="true"
                       float-thead="floatTheadOpts" ng-model="state.projections">
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
                        <th class="sick used-hours">Use</th>
                        <th class="sick available-hours">Avail</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="record in state.projections"
                        ng-class="{'highlighted': record.payPeriod.current}"
                        ng-attr-id="{{$last && 'earliest-projection' || undefined}}">
                        <td class="pay-period">{{record.payPeriod.payPeriodNum}}</td>
                        <td class="date">{{record.payPeriod.startDate | moment:'MM/DD/YYYY'}}</td>
                        <td class="date">{{record.payPeriod.endDate | moment:'MM/DD/YYYY'}}</td>
                        <td class="accrual-hours personal used-hours">
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].personalUsedDelta"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                        </td>
                        <td class="accrual-hours personal available-hours">{{record.personalAvailable}}</td>
                        <td class="accrual-hours vacation rate">{{record.vacationRate}}</td>
                        <td class="accrual-hours vacation used-hours">
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].vacationUsedDelta"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);"/>
                        </td>
                        <td class="accrual-hours vacation available-hours">{{record.vacationAvailable}}</td>
                        <td class="accrual-hours sick rate">{{record.sickRate}}</td>
                        <td class="accrual-hours sick used-hours">
                            <input type="number" min="0" step=".5" placeholder="0"
                                   ng-model="$parent.state.projections[$index].sickUsedDelta"
                                   ng-change="recalculateProjectionTotals(state.selectedYear);" />
                        </td>
                        <td class="accrual-hours sick available-hours">{{record.sickAvailable}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div modal-container></div>
</section>
