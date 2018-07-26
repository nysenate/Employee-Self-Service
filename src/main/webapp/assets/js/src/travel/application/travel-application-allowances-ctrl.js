var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationAllowancesCtrl', ['$scope', 'TravelApplicationExpensesApi', allowancesCtrl]);

function allowancesCtrl($scope, expensesApi) {

    $scope.allowances = {
        tollsAllowance: Number(angular.copy($scope.data.app.tollsAllowance)),
        parkingAllowance: Number(angular.copy($scope.data.app.parkingAllowance)),
        alternateAllowance: Number(angular.copy($scope.data.app.alternateAllowance)),
        registrationAllowance: Number(angular.copy($scope.data.app.registrationAllowance)),
        trailAirplaneStub: 0
    };

    $scope.route = angular.copy($scope.data.app.route);

    $scope.destinations = [];

    function Destination() {
        this.accommodation;
        this.stays = [];
    }

    function Stay() {
        this.date = '';
        this.isMealsRequested = false;
        this.isLodgingRequested = false;
        this.isLodgingEligible = false;
    }

    // Init accommodations
    angular.forEach($scope.data.app.accommodations, function (a) {
        var destination = new Destination();
        destination.accommodation = a;
        angular.forEach(a.days, function (day) {
            var stay = new Stay();
            stay.date = day.date;
            stay.isMealsRequested = day.isMealsRequested;

            // Find out if lodging is possible and requested.
            angular.forEach(a.nights, function (night) {
                if (night.date === day.date) {
                    stay.isLodgingEligible = true;
                    stay.isLodgingRequested = night.isLodgingRequested;
                }
            });

            destination.stays.push(stay);
        });
        $scope.destinations.push(destination);
    });

    $scope.anyReimbursableTravel = function () {
        for (var i = 0; i < $scope.route.outboundLegs.length; i++) {
            if ($scope.route.outboundLegs[i].modeOfTransportation.methodOfTravel === 'PERSONAL_AUTO') {
                return true;
            }
        }
        for (var y = 0; y < $scope.route.returnLegs.length; y++) {
            if ($scope.route.returnLegs[y].modeOfTransportation.methodOfTravel === 'PERSONAL_AUTO') {
                return true;
            }
        }
        return false;
    };

    $scope.isReimbursableLeg = function (leg) {
        return leg.modeOfTransportation.methodOfTravel === 'PERSONAL_AUTO';
    };

    $scope.next = function () {
        // Default empty allowances to 0.
        for (var prop in $scope.allowances) {
            if (!$scope.allowances[prop]) {
                $scope.allowances[prop] = 0;
            }
        }
        expensesApi.update({id: $scope.data.app.id}, {
            destinations: $scope.destinations,
            allowances: $scope.allowances
        }, function (response) {
            $scope.data.app = response.result;
            $scope.nextState();
        }, $scope.handleErrorResponse)
    }
}

