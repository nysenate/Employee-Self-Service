<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="GrantPrivilegesCtrl">
    <div class="time-attendance-hero">
        <h2>Grant Supervisor Privileges</h2>
    </div>
    <ess-notification ng-show="state.fetched === true && state.grantees.length == 0" level="warn"
                      title="No supervisor grants available."
                      message="You do not have any supervisors that you can delegate your employee's record approvals to. Please contact Senate Personnel for more information.">
    </ess-notification>

    <div loader-indicator ng-show="state.fetched === false"></div>

    <div class="content-container content-controls">
        <div ng-show="state.fetched === true && state.grantees.length > 0">
            <p class="content-info">Grant another supervisor privileges to review and/or approve your direct employee's time records.</p>
            <div class="padding-10">
                <table class="simple-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Supervisor</th>
                        <th>Status</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="grantee in state.grantees">
                        <td>{{$index + 1}}</td>
                        <td>{{grantee.firstName}} {{grantee.lastName}}</td>
                        <td>
                            <div class="horizontal-input-group">
                                <input type="checkbox" id="grant-status-yes-{{$index}}" ng-model="grantee.granted"
                                       ng-value="true" ng-click="toggleGrantStatus(grantee)" name="grant-status[{{$index}}]"/>
                                <label ng-class="{'success-bold-label': grantee.granted === true}"
                                       for="grant-status-yes-{{$index}}">Grant Access</label>
                            </div>
                        </td>
                        <td ng-class="{'half-opacity': grantee.granted === false}">
                            <div class="horizontal-input-group">
                                <input id="grant-start-date-{{$index}}" ng-checked="grantee.grantStart"
                                       ng-disabled="grantee.granted === false" type="checkbox" ng-click="setStartDate(grantee)"/>
                                <label for="grant-start-date-{{$index}}">Set Start Date</label>
                                <input ng-class="{'half-opacity': !grantee.granted || !grantee.grantStart}"
                                       ng-disabled="!grantee.granted || !grantee.grantStart" ng-model="grantee.grantStart"
                                       style="width:100px" type="text" datepicker/>
                            </div>
                        </td>
                        <td ng-class="{'half-opacity': grantee.granted === false}">
                            <div class="horizontal-input-group">
                                <input id="grant-end-date-{{$index}}" ng-checked="grantee.grantEnd"
                                       ng-disabled="grantee.granted === false" type="checkbox" ng-click="setEndDate(grantee)"/>
                                <label for="grant-end-date-{{$index}}">Set End Date</label>
                                <input ng-disabled="!grantee.granted || !grantee.grantEnd" ng-model="grantee.grantEnd"
                                       style="width:100px" type="text" datepicker/>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <hr/>
                <div loader-indicator ng-show="state.saving === true"></div>
                <div ess-notification ng-show="state.saved === true && !state.modified"
                     level="info" title="Grants have been updated."></div>
                <div class="content-info" style="text-align: center;">
                    <input type="button" class="neutral-button" ng-disabled="state.modified === false" value="Discard Changes" ng-click="reset()"/>
                    <input type="button" class="submit-button" ng-disabled="state.modified === false"
                           ng-click="saveGrants()" value="Update Grant Privileges"/>
                </div>
            </div>
        </div>
    </div>
    <div ng-show="state.granters.length > 0" class="content-container content-controls margin-top-20">
        <p class="content-info">The following employees have granted privileges to you.</p>
        <div class="padding-10">
            <table class="simple-table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Supervisor</th>
                    <th>Active</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="granter in state.granters">
                    <td>{{$index + 1}}</td>
                    <td>{{granter.firstName}} {{granter.lastName}}</td>
                    <td>{{granter.activeStr}}</td>
                    <td>{{granter.grantStartStr}}</td>
                    <td>{{granter.grantEndStr}}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div modal-container></div>
</div>
