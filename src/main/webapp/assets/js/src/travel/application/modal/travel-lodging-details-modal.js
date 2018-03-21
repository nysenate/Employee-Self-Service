var essTravel = angular.module('essTravel');

essTravel.directive('travelLodgingDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-lodging-details-modal',
        controller: 'LodgingDetailsModalCtrl'
    }
}])
    .controller('LodgingDetailsModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {

    // TODO: this contains some duplication with travel-meal-details-modal.js

    var accommodations = modals.params().app.accommodations;

    function Allowance(date, address, allowance) {
        this.date = date;
        this.address = address;
        this.allowance = Number(allowance);
    }

    $scope.lodgingAllowances = [];
    $scope.total = Number(modals.params().app.lodgingAllowance);

    function init() {
        $scope.lodgingAllowances = createLodgingAllowances();
        $scope.lodgingAllowances = removeEmptyAllowances($scope.lodgingAllowances);
        sortByDateAsc($scope.lodgingAllowances)
    }

    init();

    function createLodgingAllowances() {
        var allowances = [];
        angular.forEach(accommodations, function (accommodation) {
            angular.forEach(accommodation.stays, function (stay) {
                allowances.push(new Allowance(stay.date, accommodation.address, stay.lodgingAllowance))
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
    }
}