angular.module('essTime')
    .controller('DonationCtrl', ['$timeout', '$scope', 'appProps', 'modals',
                                 "MaxDonationApi", "DonationHistoryApi", "SubmitDonationApi",
                                 sickTimeDonationCtrl]);
// TODO: note: effective dates should be in [start of last time sheet, today], default to today
function sickTimeDonationCtrl($timeout, $scope, appProps, modals,
                              MaxDonationApi, DonationHistoryApi, SubmitDonationApi) {
    const initialState = {
        empId: appProps.user.employeeId,
        effectiveDate: new Date(),
        hoursToDonate: null,
        currYear: new Date().getFullYear(),
        maxDonation: null,
        donationData: []
    };
    $scope.state = angular.extend(initialState);

    function setMaxDonation() {
        const params = {
            empId: $scope.state.empId,
            effectiveDate: $scope.state.effectiveDate
        };
        MaxDonationApi.get(params,
            function onSuccess(resp) {
                $scope.state.maxDonation = resp.result.value;
            }
        );
    }

    function setDonationHistory() {
        const params = {
            empId: $scope.state.empId,
            year: $scope.state.currYear
        };
        DonationHistoryApi.get(params,
            function onSuccess(resp) {
                $scope.state.donationData = resp.result
            }
        );
    }

    $scope.getYears = function () {
        const years = [];
        // Can't use 'let' for some reason
        for (var i = 2023; i <= new Date().getFullYear(); i++) {
            years.push(i);
        }
        return years;
    }

    setMaxDonation();
    setDonationHistory();
    $scope.setMaxDonation = setMaxDonation;
    $scope.setDonationHistory = setDonationHistory;

    // TODO: need popup to confirm submission, loading
}
