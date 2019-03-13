var essTravel = angular.module('essTravel');

essTravel.directive('travelLodgingDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-lodging-details-modal',
        controller: 'LodgingDetailsModalCtrl'
    }
}])
    .controller('LodgingDetailsModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.app = modals.params().app;
        $scope.lodgingExpenses = [];

        $scope.app.route.destinations.forEach(function (dest) {
            for (var date in dest.lodgingPerDiems) {
                if (dest.lodgingPerDiems.hasOwnProperty(date)) {
                    $scope.lodgingExpenses.push(
                        {
                            date: date,
                            address: dest.address,
                            lodgingExpense: parseFloat(dest.lodgingPerDiems[date])
                        }
                    )
                }
            }
        });
    };

    $scope.sumLodgingExpenses = function () {
        return $scope.lodgingExpenses.reduce(sumLodging, 0);

        function sumLodging(accumulator, currentValue) {
            return accumulator + parseFloat(currentValue.lodgingExpense);
        }
    };

    $scope.closeModal = function () {
        modals.resolve();
    };
}