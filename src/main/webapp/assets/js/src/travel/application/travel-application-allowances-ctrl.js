var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationAllowancesCtrl', ['$scope', 'TravelApplicationExpensesApi', 'TravelApplicationMealAllowanceApi',
                                                         'TravelApplicationLodgingAllowanceApi', allowancesCtrl]);

function allowancesCtrl($scope, expensesApi, mealAllowancesApi, lodgingAllowancesApi) {

    this.$onInit = function () {
        $scope.dirtyApp = angular.copy($scope.data.app);
        console.log($scope.dirtyApp);
        $scope.route = $scope.dirtyApp.route;

        $scope.allowances = {
            tollsAllowance: Number($scope.dirtyApp.tollsAllowance),
            parkingAllowance: Number($scope.dirtyApp.parkingAllowance),
            alternateAllowance: Number($scope.dirtyApp.alternateAllowance),
            registrationAllowance: Number($scope.dirtyApp.registrationAllowance),
            trainAndAirplaneAllowance: Number($scope.dirtyApp.trainAndAirplaneAllowance)
        };
    };

    $scope.previousDay = function (date) {
        return moment(date).subtract(1, 'days').toDate();
    };

    $scope.next = function () {
        // Default empty allowances to 0.
        for (var prop in $scope.allowances) {
            if (!$scope.allowances[prop]) {
                $scope.allowances[prop] = 0;
            }
        }
        // Updates must be done sequentially as the entire app is overwritten with every save.
        expensesApi.update({id: $scope.data.app.id}, $scope.allowances, function (response) {
            $scope.data.app = response.result;

            mealAllowancesApi.update({id: $scope.data.app.id}, $scope.dirtyApp.mealAllowance, function (response) {
                $scope.data.app = response.result;

                lodgingAllowancesApi.update({id: $scope.data.app.id}, $scope.dirtyApp.lodgingAllowance, function (response) {
                    $scope.data.app = response.result;
                    $scope.nextState();
                }, $scope.handleErrorResponse);
            }, $scope.handleErrorResponse);
        }, $scope.handleErrorResponse)
    };

    /**
     * Returns true if the traveler will be lodging during their travel, otherwise false.
     */
    $scope.tripHasLodging = function () {
        return $scope.dirtyApp.lodgingAllowance.lodgingAllowances.length > 0;
    }
}

