var essTravel = angular.module('essTravel');

essTravel.directive('travelMealDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-meal-details-modal',
        scope: {
            mealAllowance: '='
        },
        controller: 'MealDetailsModalCtrl'
    }
}])
    .controller('MealDetailsModalCtrl', ['$scope', 'modals', mealDetailsModalCtrl]);

function mealDetailsModalCtrl($scope, modals) {
    // Sort by date asc
    $scope.mealAllowance.mealDays.sort(function (a, b) {
        if (a.date < b.date) return -1;
        if (a.date > b.date) return 1;
        return 0;
    });

    $scope.closeModal = function() {
        modals.resolve();
    };
}