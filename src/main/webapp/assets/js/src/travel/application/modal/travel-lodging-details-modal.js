var essTravel = angular.module('essTravel');

essTravel.directive('travelLodgingDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-lodging-details-modal',
        scope: {
            lodgingAllowance: '='
        },
        controller: 'LodgingDetailsModalCtrl'
    }
}])
    .controller('LodgingDetailsModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {
    // Sort by date asc
    $scope.lodgingAllowance.lodgingNights.sort(function (a, b) {
        if (a.date < b.date) return -1;
        if (a.date > b.date) return 1;
        return 0;
    });

    $scope.closeModal = function() {
        modals.resolve();
    }
}