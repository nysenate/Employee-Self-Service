var essTravel = angular.module('essTravel');

essTravel.directive('travelFormModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/travel-form-modal',
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
