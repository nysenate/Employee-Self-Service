var essTravel = angular.module('essTravel');

essTravel.directive('travelHistoryDetailModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/travel-history-detail-modal',
        scope: {},
        controller: 'TravelHistoryDetailCtrl'
    }
}])
    .controller('TravelHistoryDetailCtrl', ['$scope', 'modals', travelDetailCtrl]);

function travelDetailCtrl($scope, modals) {

    $scope.app = modals.params();

    $scope.exit = function () {
        modals.resolve();
    };
}
