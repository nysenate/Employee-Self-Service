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
    //display the travel application details corresponding to what they clicked on

    $scope.requestInfo = modals.params().info;
    console.log($scope.requestInfo.applicant.lastName);

    $scope.exit = function () {
        modals.resolve();
    };

    $scope.cancel = function() {
        modals.reject();
    }
}
