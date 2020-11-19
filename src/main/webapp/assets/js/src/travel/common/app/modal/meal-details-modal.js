var essTravel = angular.module('essTravel');

essTravel.directive('essMealDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/app/modal/meal-details-modal',
        controller: 'MealDetailsModalCtrl'
    }
}])
    .controller('MealDetailsModalCtrl', ['$scope', 'modals', mealDetailsModalCtrl]);

function mealDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.amendment = modals.params().amendment;
        $scope.isOverridden = $scope.amendment.mealPerDiems.isOverridden;
        $scope.NOT_AVAILABLE = "N/A";
    };

    $scope.closeModal = function () {
        modals.resolve();
    };
}