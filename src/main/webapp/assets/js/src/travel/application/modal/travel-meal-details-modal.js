var essTravel = angular.module('essTravel');

essTravel.directive('travelMealDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-meal-details-modal',
        controller: 'MealDetailsModalCtrl'
    }
}])
    .controller('MealDetailsModalCtrl', ['$scope', 'modals', mealDetailsModalCtrl]);

function mealDetailsModalCtrl($scope, modals) {

    var accommodations = modals.params().app.accommodations;

    function Allowance(date, address, allowance) {
        this.date = date;
        this.address = address;
        this.allowance = Number(allowance);
    }

    $scope.mealAllowances = [];
    $scope.total = Number(modals.params().app.mealAllowance);

    function init() {
        $scope.mealAllowances = createMealAllowances();
        $scope.mealAllowances = removeEmptyAllowances($scope.mealAllowances);
        sortByDateAsc($scope.mealAllowances);
    }

    init();

    function createMealAllowances() {
        var allowances = [];
        angular.forEach(accommodations, function (accommodation) {
            angular.forEach(accommodation.stays, function (stay) {
                // Only display stays from accommodations where meals were requested.
                if (accommodation.isMealsRequested) {
                    allowances.push(new Allowance(stay.date, accommodation.address, stay.mealAllowance))
                }
            });
        });
        return allowances;
    }

    function removeEmptyAllowances(allowances) {
        return allowances.filter(function (a) {
            return a.allowance > 0;
        });
    }

    function sortByDateAsc(allowances) {
        allowances.sort(function (a, b) {
            if (a.date < b.date) return -1;
            if (a.date > b.date) return 1;
            return 0;
        });
    }

    $scope.closeModal = function() {
        modals.resolve();
    };
}