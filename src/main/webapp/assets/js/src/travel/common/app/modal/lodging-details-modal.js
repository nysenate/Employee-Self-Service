var essTravel = angular.module('essTravel');

essTravel.directive('essLodgingDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/app/modal/lodging-details-modal',
        controller: 'LodgingDetailsModalCtrl'
    }
}])
    .controller('LodgingDetailsModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.app = modals.params().app;
        $scope.isOverridden = $scope.app.perDiemOverrides.isLodgingOverridden;
    };

    $scope.closeModal = function () {
        modals.resolve();
    };
}