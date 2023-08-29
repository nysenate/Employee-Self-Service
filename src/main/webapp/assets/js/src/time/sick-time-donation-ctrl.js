angular.module('essTime')
    .controller('DonationCtrl', ['$timeout', '$scope', 'appProps', 'modals',
                                 "DonationInfoApi", "DonationHistoryApi", "SubmitDonationApi",
                                 sickTimeDonationCtrl]);
// TODO: note: effective dates should be in [start of last time sheet, today], default to today
// TODO: reload year donations on submission, and reset donation amount
// TODO: need loading

function sickTimeDonationCtrl($timeout, $scope, appProps, modals,
                              DonationInfoApi, DonationHistoryApi, SubmitDonationApi) {
    const initialState = {
        empId: appProps.user.employeeId,
        effectiveDate: new Date(),
        hoursToDonate: null,
        currYear: null,
        maxDonation: null,
        accruedSickTime: null,
        donationData: [],
        showCertificationMessage: false,
        message: ""
    };
    $scope.state = angular.extend(initialState);

    function setDonationInfo() {
        const params = {
            empId: $scope.state.empId,
            effectiveDate: $scope.state.effectiveDate
        };
        DonationInfoApi.get(params,
            function onSuccess(resp) {
                $scope.state.maxDonation = resp.result.maxDonation;
                $scope.state.accruedSickTime = resp.result.accruedSickTime;
            }
        );
    }

    $scope.setDonationHistory = function setDonationHistory() {
        const params = {
            empId: $scope.state.empId,
            year: $scope.state.currYear
        };
        DonationHistoryApi.get(params,
            function onSuccess(resp) {
                $scope.state.donationData = resp.result;
            }
        );
    }

    $scope.getYears = function() {
        const years = [];
        // Can't use 'let' for some reason
        for (var i = 2023; i <= new Date().getFullYear(); i++) {
            years.push(i);
        }
        return years;
    }

    $scope.submitDonation = function() {
        const params = {
            empId: $scope.state.empId,
            effectiveDate: $scope.state.effectiveDate,
            hoursToDonate: $scope.state.hoursToDonate
        };
        // We pass in all data in the params
        SubmitDonationApi.save(params, {},
            function onSuccess(resp) {
                $scope.state.message = resp.message;
            }
        );
        $scope.state.showCertificationMessage = false;
    }

    setDonationInfo();
    $scope.setDonationInfo = setDonationInfo;
}
