var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationAllowancesCtrl', ['$scope', 'TravelApplicationExpensesApi',
                                                         'TravelApplicationAccommodationsApi', allowancesCtrl]);

function allowancesCtrl($scope, expensesApi, accommodationsApi) {

    this.$onInit = function () {
        $scope.dirtyApp = angular.copy($scope.data.app);
        console.log($scope.dirtyApp);
        $scope.route = $scope.dirtyApp.route;

        $scope.allowances = {
            tollsAllowance: Number($scope.dirtyApp.tollsAllowance),
            parkingAllowance: Number($scope.dirtyApp.parkingAllowance),
            alternateAllowance: Number($scope.dirtyApp.alternateAllowance),
            registrationAllowance: Number($scope.dirtyApp.registrationAllowance)
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
        expensesApi.update({id: $scope.data.app.id}, $scope.allowances, function (response) {
            $scope.data.app = response.result;
            accommodationsApi.update({id: $scope.data.app.id}, $scope.dirtyApp.accommodations, function (response) {
                $scope.data.app = response.result;
                $scope.nextState();
            }, $scope.handleErrorResponse);
        }, $scope.handleErrorResponse)
    };

    /**
     * Returns true if the traveler will be lodging during their travel, otherwise false.
     */
    $scope.isLodging = function () {
        for (var i = 0; i < $scope.dirtyApp.accommodations.length; i++) {
            var a = $scope.dirtyApp.accommodations[i];
            if (a.nights.length > 0) {
                return true;
            }
        }
    }
}

