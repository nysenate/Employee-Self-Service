var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationAllowancesCtrl', ['$scope', 'modals', 'TravelApplicationExpensesApi', allowancesCtrl]);

function allowancesCtrl($scope, modals, expensesApi, mealAllowancesApi, lodgingAllowancesApi) {

    this.$onInit = function () {
        $scope.dirtyApp = angular.copy($scope.data.app);
    };

    $scope.next = function () {
        // Updates must be done sequentially as the entire app is overwritten with every save.
        expensesApi.update($scope.dirtyApp.allowances, function (response) {
            $scope.data.app = response.result;
            $scope.nextState();
        }, $scope.handleErrorResponse)
    };

    $scope.displayLodgingDetails = function () {
        modals.open('travel-lodging-details-modal', {app: $scope.dirtyApp}, true);
    };

    $scope.displayMealDetails = function () {
        modals.open('travel-meal-details-modal', {app: $scope.dirtyApp}, true);
    };

    $scope.displayMileageDetails = function () {
        modals.open('travel-mileage-details-modal', {app: $scope.dirtyApp}, true);
    };
}

