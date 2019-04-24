var essTravel = angular.module('essTravel');

essTravel.directive('essMileageDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/mileage-details-modal',
        controller: 'MileageDetailsModalCtrl'
    }
}])
    .controller('MileageDetailsModalCtrl', ['$scope', 'modals', mileageDetailsModalCtrl]);

function mileageDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.app = modals.params().app;
    };

    $scope.closeModal = function() {
        modals.resolve();
    };
}
