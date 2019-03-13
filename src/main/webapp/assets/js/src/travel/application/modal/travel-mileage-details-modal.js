var essTravel = angular.module('essTravel');

essTravel.directive('travelMileageDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-mileage-details-modal',
        controller: 'MileageDetailsModalCtrl'
    }
}])
    .controller('MileageDetailsModalCtrl', ['$scope', 'modals', mileageDetailsModalCtrl]);

function mileageDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.app = modals.params().app;
        $scope.legs = $scope.app.route.outboundLegs.concat($scope.app.route.returnLegs);
    };

    $scope.closeModal = function() {
        modals.resolve();
    };
}