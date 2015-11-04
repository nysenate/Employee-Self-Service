<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmpTransactionHistoryCtrl">
    <div class="my-info-hero">
        <h2>Employee Updates</h2>
    </div>
    <div class="padding-10">
        <ess-notification ng-show="state.timeline === false" level="info"
                          message="No relevant updates are available for your account.">
        </ess-notification>
        <ess-notification level="info" ng-show="state.timeline"
                          message="Relevant changes to your personnel record are listed below. It is
                          not a complete listing of all updates that have been posted.">
        </ess-notification>
        <div ng-repeat="(date,txArr) in state.timeline">
            <h2 class="tx-effect-date">{{date | moment:'ll'}}</h2>
            <div class="grid tx-container" ng-repeat="tx in txArr">
                <div class="col-5-12" style="padding-right: 0;">
                    <div class="tx-heading" >
                        <h3 class="tx-label">{{tx.transDesc}}</h3>
                        <span class="tx-update-date">Last updated on {{tx.updateDate | moment: 'lll'}}</span>
                    </div>
                </div>
                <div class="col-7-12">
                    <div class="tx-data" ng-switch on="tx.transCode">
                        <div ng-switch-when="LEG">
                            <p>{{tx.values.ADSTREET1.value}}
                               <br/>{{tx.values.ADCITY.value}}, {{tx.values.ADSTATE.value}} {{tx.values.ADZIPCODE.value}}</p>
                        </div>
                        <div ng-switch-when="TYP"><p>{{tx.values.CDPAYTYPE.value | payTypeFilter}}</p></div>
                        <div ng-switch-when="LOC"><p>{{tx.values.CDLOCAT.value}}</p></div>
                        <div ng-switch-when="PHO">
                            <p><span class="tx-label">Home Phone:</span> {{tx.values.ADPHONENUM.value}}</p>
                            <p><span class="tx-label">Work Phone:</span> {{tx.values.ADPHONENUMW.value}}</p>
                        </div>
                        <div ng-switch-when="SUP"><p>{{tx.values.NAFIRSTSUP.value}} {{tx.values.NALASTSUP.value}}</p></div>
                        <div ng-switch-when="MAR"><p>{{tx.values.CDMARITAL.value}}</p></div>
                        <div ng-switch-when="SAL"><p>{{tx.values.MOSALBIWKLY.value | currency}}</p></div>
                        <div ng-switch-when="EMP"><p><span class="tx-label">Employment Status:</span> {{tx.values.CDEMPSTATUS.value | activeInactive}}</p></div>
                        <%-- Excuse the duplication.. --%>
                        <div ng-switch-default>
                            <p><span class="tx-label">Name: </span>{{tx.values.NAFIRST.value}} {{tx.values.NALAST.value}}</p>
                            <p ng-if="tx.values.CDPAYTYPE.value">
                                <span class="tx-label">Pay Type:</span> {{tx.values.CDPAYTYPE.value | payTypeFilter}}</p>
                            <p ng-if="tx.values.MOSALBIWKLY.value > 0">
                                <span class="tx-label">Salary/Hourly Rate:</span> {{tx.values.MOSALBIWKLY.value | currency}}</p>
                            <p ng-if="tx.values.NALASTSUP.value">
                                <span class="tx-label">T&A Supervisor:</span> {{tx.values.NALASTSUP.value}}</p>
                            <p ng-if="tx.values.ADPHONENUM.value">
                                <span class="tx-label">Home Phone:</span> {{tx.values.ADPHONENUM.value}}</p>
                            <p ng-if="tx.values.ADPHONENUMW.value">
                                <span class="tx-label">Work Phone:</span> {{tx.values.ADPHONENUMW.value}}</p>
                            <p ng-if="tx.values.DTCONTSERV.value">
                                <span class="tx-label">Continuous Service Since:</span> {{tx.values.DTCONTSERV.value | moment:'ll'}}</p>
                            <p><span class="tx-label">Home Address:</span> {{tx.values.ADSTREET1.value}}
                                <br/>{{tx.values.ADCITY.value}}, {{tx.values.ADSTATE.value}} {{tx.values.ADZIPCODE.value}}</p>
                            <p ng-if="tx.values.CDMARITAL.value">
                                <span class="tx-label">Marital Status:</span> {{tx.values.CDMARITAL.value}}</p></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div modal-container></div>
</section>