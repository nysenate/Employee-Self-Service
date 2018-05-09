var essTravel = angular.module('essTravel');

essTravel.directive('travelLodgingDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-lodging-details-modal',
        controller: 'LodgingDetailsModalCtrl'
    }
}])
    .controller('LodgingDetailsModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {

    $scope.app = modals.params().app;

    $scope.closeModal = function() {
        modals.resolve();
    };
}