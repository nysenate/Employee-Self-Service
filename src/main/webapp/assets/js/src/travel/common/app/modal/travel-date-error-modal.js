var essTravel = angular.module('essTravel');

essTravel.directive('travelDateErrorModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/app/modal/travel-date-error-modal',
        controller: 'TravelDateErrorModal'
    }
}])
    .controller('TravelDateErrorModal', ['$scope', 'modals', travelDateErrorCtrl]);

function travelDateErrorCtrl($scope, modals) {

    $scope.ok = function() {
        modals.resolve();
    }
}