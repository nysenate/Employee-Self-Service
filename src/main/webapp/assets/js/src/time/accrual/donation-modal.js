angular.module('essTime')
    .directive('sickDonationConfirmationModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/accrual/donation-modal',
        controller: 'SickDonationConfirmationModalCtrl',
    }
}]).controller('SickDonationConfirmationModalCtrl', ['$scope', 'modals', function ($scope, modals) {
    $scope.resolveDonation = function () {
        $scope.state.inputLastName = "";
        modals.resolve();
    };

    $scope.rejectDonation = function () {
        $scope.state.inputLastName = "";
        modals.reject();
    };
}]);
