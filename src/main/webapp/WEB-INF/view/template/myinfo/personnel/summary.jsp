<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="EmpSummaryCtrl">
    <div class="my-info-hero">
        <h1>{{state.emp.fullName}}</h1>
    </div>
    <ess-notification level="info"
                      message="If any of the information below is inaccurate, please contact Senate Personnel." >
    </ess-notification>

    <div class="grid" style="background:white;">
        <div class="col-1-2 content-container content-controls padding-10">
            <p class="content-info">Personnel Info</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Email</td><td>{{state.emp.email}}</td></tr>
                <tr><td>Work Phone</td><td>{{state.emp.workPhone}}</td></tr>
                <tr><td>Home Phone</td><td>{{state.emp.homePhone}}</td></tr>
                <tr><td>Address Line 1</td><td>{{state.emp.snapshot['ADSTREET1'].value}}</td></tr>
                <tr><td>Address Line 2</td><td>{{state.emp.snapshot['ADSTREET2'].value}}</td></tr>
                <tr><td>City</td><td>{{state.emp.snapshot['ADCITY'].value}}</td></tr>
                <tr><td>State</td><td>{{state.emp.snapshot['ADSTATE'].value}}</td></tr>
                <tr><td>Zip</td><td>{{state.emp.snapshot['ADZIPCODE'].value}}</td></tr>
                <tr><td>Marital Status</td><td>{{state.emp.snapshot['CDMARITAL'].value}}</td></tr>
                </tbody>
            </table>
            <p class="content-info">Organization Info</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Resp Center Head</td><td>{{state.emp.respCtr.respCenterHead.name}}</td></tr>
                <tr><td>Work Address</td><td>{{state.emp.workAddress.addr1}}, {{state.emp.workAddress.addr2}}
                    {{state.emp.workAddress.city}}, {{state.emp.workAddress.state}} {{state.emp.workAddress.zip5}}</td></tr>
                <tr><td>Negotiating Unit</td><td>{{state.emp.snapshot['CDNEGUNIT'].value}}</td></tr>
                <tr><td>Job Title</td><td>{{state.emp.jobTitle}}</td></tr>
                <tr>
                    <td>T&A Supervisor</td>
                    <td>{{state.emp.snapshot['NAFIRSTSUP'].value}} {{state.emp.snapshot['NALASTSUP'].value}}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="col-1-2 content-container content-controls padding-10">
            <p class="content-info">Payroll Info</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Pay Type</td><td>{{state.emp.payType}}</td></tr>
                <tr><td>EMPLID</td><td>{{state.emp.nid}}</td></tr>
                <tr>
                    <td>Continuous Service From</td>
                    <td>{{state.emp.snapshot['DTCONTSERV'].value | moment:'MM/DD/YYYY'}}</td>
                </tr>
                <tr>
                    <td>
                        <span ng-hide="state.emp.payType === 'TE'">Bi Weekly Salary</span>
                        <span ng-show="state.emp.payType === 'TE'">Hourly Rate</span>
                    </td>
                    <td>{{state.emp.snapshot['MOSALBIWKLY'].value | currency}}</td>
                </tr>
                <tr ng-show="state.emp.payType === 'TE'">
                    <td>Annual Allowance</td><td>{{state.emp.snapshot['MOAMTEXCEED'].value | currency}}</td>
                </tr>

                <tr><td>Direct Deposit</td><td>{{state.emp.snapshot['CDDIRECTDEPF'].value}}</td></tr>
                <tr><td>Agency Code</td><td>{{state.emp.respCtr.agencyCode}}</td></tr>
                </tbody>
            </table>
            <p class="content-info">Federal Tax</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Exemptions</td><td>{{state.emp.snapshot['NUFEDTAXEX'].value || 0}}</td></tr>
                <tr><td>Reported Marital Status</td><td>{{state.emp.snapshot['CDMARITALFED'].value}}</td></tr>
                <tr><td>Additional Withheld</td><td>{{state.emp.snapshot['MOADDFEDTAX'].value || 0 | currency}}</td></tr>
                </tbody>
            </table>
            <p class="content-info">State Tax</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Exemptions</td><td>{{state.emp.snapshot['NUSTATTAXEX'].value || 0}}</td></tr>
                <tr><td>Reported Marital Status</td><td>{{state.emp.snapshot['CDMARITALST'].value}}</td></tr>
                <tr><td>Additional Withheld</td><td>{{state.emp.snapshot['MOADDSTATTAX'].value || 0 | currency}}</td></tr>
                </tbody>
            </table>
            <p class="content-info">New York City Tax</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Exemptions</td><td>{{state.emp.snapshot['NUCITYTAXEX'].value || 0}}</td></tr>
                <tr><td>Reported Marital Status</td><td>{{state.emp.snapshot['CDMARITALNYC'].value}}</td></tr>
                <tr><td>Additional Withheld</td><td>{{state.emp.snapshot['MOADDCITYTAX'].value || 0 | currency}}</td></tr>
                </tbody>
            </table>
            <p class="content-info">Yonkers Tax</p>
            <table class="simple-table">
                <tbody>
                <tr><td>Exemptions</td><td>{{state.emp.snapshot['NUYONTAXEX'].value || 0}}</td></tr>
                <tr><td>Reported Marital Status</td><td>{{state.emp.snapshot['CDMARITALYON'].value}}</td></tr>
                <tr><td>Additional Withheld</td><td>{{state.emp.snapshot['MOADDYONTAX'].value || 0 | currency}}</td></tr>
                </tbody>
            </table>
        </div>
    </div>
    <div modal-container></div>
</div>
