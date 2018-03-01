var essTravel = angular.module('essTravel');

essTravel.directive('travelMileageDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-mileage-details-modal',
        scope: {
            mileageAllowance: '='
        },
        controller: 'MileageDetailsModalCtrl'
    }
}])
    .controller('MileageDetailsModalCtrl', ['$scope', 'modals', mileageDetailsModalCtrl]);

function mileageDetailsModalCtrl($scope, modals) {

    $scope.mileageAllowance.legs = $scope.mileageAllowance.outboundLegs.concat($scope.mileageAllowance.returnLegs);

    $scope.displayRequirements = $scope.mileageAllowance.legs.length > 0 && Number($scope.mileageAllowance.total) === 0;

    $scope.closeModal = function() {
        modals.resolve();
    }
}