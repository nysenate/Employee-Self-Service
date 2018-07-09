var essTravel = angular.module('essTravel');

essTravel.directive('travelContinueApplicationModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-continue-application-modal',
        controller: 'ContinueApplicationModalCtrl'
    }
}])
    .controller('ContinueApplicationModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {

    // Modal is rejected if they want to start a fresh application.
    $scope.rejectModal = function () {
        modals.reject();
    };

    // Modal is resolved if they want to continue their application.
    $scope.resolveModal = function() {
        modals.resolve();
    };
}