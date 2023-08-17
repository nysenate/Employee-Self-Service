angular.module('essTime')
    .controller('DonationCtrl', ['$timeout', '$scope', 'appProps', 'modals',
                                 "TimeDonatedInPastYearApi", "DonationHistoryApi", "SubmitDonationApi",
                                 sickTimeDonationCtrl]);
// TODO: note: effective dates should be in [start of last time sheet, today], default to today
function sickTimeDonationCtrl($timeout, $scope, appProps, modals,
                              TimeDonatedInPastYearApi, DonationHistoryApi, SubmitDonationApi) {
    const initialState = {
        empId: appProps.user.employeeId,
        effectiveDate: new Date(),
        hoursToDonate: null,
        maxDonation: null,
        setMaxDonation: function() {
            const params = {
                empId: $scope.state.empId,
                effectiveDate: $scope.state.effectiveDate
            };
            TimeDonatedInPastYearApi.get(params,
                function onSuccess(resp) {
                    console.log(resp.result);
                    $scope.state.maxDonation = resp.result.value;
                }
            )
        }
    };
    $scope.state = angular.extend(initialState);
    $scope.state.setMaxDonation();

    // TODO: need popup to confirm submission
}
