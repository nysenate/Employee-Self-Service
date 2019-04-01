var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationAllowancesCtrl', ['$scope', 'modals', 'TravelApplicationByIdApi', allowancesCtrl]);

function allowancesCtrl($scope, modals, appIdApi) {

    this.$onInit = function () {
        $scope.dirtyApp = angular.copy($scope.data.app);
        console.log($scope.dirtyApp);
    };

    $scope.next = function () {
        // Updates must be done sequentially as the entire app is overwritten with every save.
        appIdApi.update({id: $scope.data.app.id}, {allowances: JSON.stringify($scope.dirtyApp.allowances)}, function (response) {
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

