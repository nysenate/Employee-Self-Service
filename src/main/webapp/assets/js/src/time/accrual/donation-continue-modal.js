angular.module('essTime')
    .directive('sickDonationContinueModal', ['appProps', function (appProps) {
        return {
            templateUrl: appProps.ctxPath + '/template/time/accrual/donation-continue-modal',
            controller: 'SickDonationContinueModalCtrl',
        }
    }]).controller('SickDonationContinueModalCtrl', ['$scope', 'modals', function ($scope, modals) {
    $scope.resolveContinue = function () {
        modals.resolve();
    };

    $scope.rejectContinue = function () {
        modals.reject();
    };
}]);
