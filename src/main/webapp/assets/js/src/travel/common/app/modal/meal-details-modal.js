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
        $scope.app = modals.params().app;
        $scope.isOverridden = $scope.app.mealPerDiems.isOverridden;
    };

    $scope.closeModal = function () {
        modals.resolve();
    };
}