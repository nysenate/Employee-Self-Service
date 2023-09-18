angular.module('essTime')
    .controller('DonationCtrl', ['$timeout', '$scope', 'appProps', 'modals',
                                 "DonationInfoApi", "DonationHistoryApi", "SubmitDonationApi",
                                 sickTimeDonationCtrl]);

function sickTimeDonationCtrl($timeout, $scope, appProps, modals,
                              DonationInfoApi, DonationHistoryApi, SubmitDonationApi) {
    const initialState = {
        empId: appProps.user.employeeId,
        realLastName: appProps.user.lastName,
        hoursToDonate: null,
        selectedYear: new Date().getFullYear(),
        maxDonation: null,
        accruedSickTime: null,
        donationData: [],
        inputLastName: ""
    };
    $scope.state = angular.extend(initialState);

    function setDonationInfo() {
        $scope.state.maxDonation = null;
        const params = {
            empId: $scope.state.empId,
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
            year: $scope.state.selectedYear
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
            hoursToDonate: $scope.state.hoursToDonate
        };
        SubmitDonationApi.save(params, {},
            function onSuccess() {
                $scope.setDonationInfo();
                $scope.setDonationHistory();
            }
        )
        $scope.state.hoursToDonate = null;
    }

    $scope.openContinuePopup = function () {
        modals.open('donation-continue-modal', {}, true)
            .then(function() {
                $scope.openConfirmPopup();
            }).catch(function() {})
    }

    $scope.openConfirmPopup = function () {
        modals.open('donation-modal', {}, true)
            .then(function() {
                $scope.submitDonation();
            }).catch(function() {})
    }

    $scope.$watch('state.selectedYear', $scope.setDonationHistory);
    setDonationInfo();
    $scope.setDonationInfo = setDonationInfo;
}
