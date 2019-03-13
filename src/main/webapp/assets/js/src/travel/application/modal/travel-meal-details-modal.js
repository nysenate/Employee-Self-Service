var essTravel = angular.module('essTravel');

essTravel.directive('travelMealDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-meal-details-modal',
        controller: 'MealDetailsModalCtrl'
    }
}])
    .controller('MealDetailsModalCtrl', ['$scope', 'modals', mealDetailsModalCtrl]);

function mealDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.app = modals.params().app;
        $scope.mealExpenses = [];

        $scope.app.route.destinations.forEach(function (dest) {
            for (var date in dest.mealPerDiems) {
                if (dest.mealPerDiems.hasOwnProperty(date)) {
                    $scope.mealExpenses.push(
                        {
                            date: date,
                            address: dest.address,
                            mealExpense: dest.mealPerDiems[date]
                        }
                    )
                }
            }
        });
    };

    $scope.sumMealExpenses = function () {
        return $scope.mealExpenses.reduce(sumMeals, 0);

        function sumMeals(accumulator, currentValue) {
            return accumulator + parseFloat(currentValue.mealExpense);
        }
    };

    $scope.closeModal = function () {
        modals.resolve();
    };

}