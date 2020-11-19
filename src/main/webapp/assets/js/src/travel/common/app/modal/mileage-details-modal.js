var essTravel = angular.module('essTravel');

essTravel.directive('essMileageDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/app/modal/mileage-details-modal',
        controller: 'MileageDetailsModalCtrl'
    }
}])
    .controller('MileageDetailsModalCtrl', ['$scope', 'modals', mileageDetailsModalCtrl]);

function mileageDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.amendment = modals.params().amendment;
        $scope.isOverridden = false; // not implemented.
    };

    $scope.closeModal = function() {
        modals.resolve();
    };
}
