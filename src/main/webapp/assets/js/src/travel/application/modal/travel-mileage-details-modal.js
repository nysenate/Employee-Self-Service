var essTravel = angular.module('essTravel');

essTravel.directive('travelMileageDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/travel-mileage-details-modal',
        controller: 'MileageDetailsModalCtrl'
    }
}])
    .controller('MileageDetailsModalCtrl', ['$scope', 'modals', mileageDetailsModalCtrl]);

function mileageDetailsModalCtrl($scope, modals) {

    $scope.mileageAllowance = {
        legs: [],
        total: 0,
        rate: 0
    };

    function init() {
        var route = modals.params().app.route;
        $scope.mileageAllowance.rate = route.mileageRate;
        $scope.mileageAllowance.total = route.mileageAllowance;
        $scope.mileageAllowance.legs = findQualifyingLegs(route.outgoingLegs)
            .concat(findQualifyingLegs(route.returnLegs));
    }

    init();

    function findQualifyingLegs(legs) {
        var qualifyingLegs = [];
        angular.forEach(legs, function(leg) {
            if (leg.qualifies) {
                qualifyingLegs.push(leg);
            }
        });
        return qualifyingLegs;
    }

    $scope.closeModal = function() {
        modals.resolve();
    }
}