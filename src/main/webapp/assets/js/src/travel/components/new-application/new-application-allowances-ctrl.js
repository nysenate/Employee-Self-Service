var essTravel = angular.module('essTravel');

essTravel.controller('NewApplicationAllowancesCtrl', ['$scope', 'modals', 'TravelApplicationByIdApi', allowancesCtrl]);

function allowancesCtrl($scope, modals, appIdApi) {

    this.$onInit = function () {
        $scope.dirtyApp = angular.copy($scope.data.app);
        console.log($scope.dirtyApp);
    };

    $scope.next = function () {
        var patches = {
            allowances: JSON.stringify($scope.dirtyApp.allowances),
            mealPerDiems: JSON.stringify($scope.dirtyApp.mealPerDiems),
            lodgingPerDiems: JSON.stringify($scope.dirtyApp.lodgingPerDiems),
            mileagePerDiems: JSON.stringify($scope.dirtyApp.mileagePerDiems)
        };
        appIdApi.update({id: $scope.data.app.id}, patches, function (response) {
                $scope.data.app = response.result;
                $scope.nextState();
            }, $scope.handleErrorResponse)
    };

    $scope.previousDay = function (date) {
        return moment(date).subtract(1, 'days').toDate();
    };

    $scope.tripHasLodging = function () {
        return $scope.dirtyApp.lodgingPerDiems.allLodgingPerDiems.length > 0;
    };

    $scope.tripHasMileage = function () {
        return $scope.dirtyApp.mileagePerDiems.qualifyingLegs.length > 0;
    }
}

