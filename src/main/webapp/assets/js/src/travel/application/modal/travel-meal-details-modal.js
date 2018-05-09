var essTravel = angular.module('essTravel');

essTravel.directive('travelMealDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-meal-details-modal',
        controller: 'MealDetailsModalCtrl'
    }
}])
    .controller('MealDetailsModalCtrl', ['$scope', 'modals', mealDetailsModalCtrl]);

function mealDetailsModalCtrl($scope, modals) {

    $scope.app = modals.params().app;

    $scope.closeModal = function() {
        modals.resolve();
    };
}