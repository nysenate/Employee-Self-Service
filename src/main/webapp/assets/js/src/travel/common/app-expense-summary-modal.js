var essTravel = angular.module('essTravel');

essTravel.directive('appExpenseSummaryModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/app-expense-summary-modal',
        scope: {},
        controller: 'AppExpenseSummaryCtrl'
    }
}])
    .controller('AppExpenseSummaryCtrl', ['$scope', 'modals', appExpenseSummaryCtrl]);

function appExpenseSummaryCtrl($scope, modals) {

    $scope.app = modals.params();
    console.log($scope.app);

    $scope.closeModal = function () {
        modals.resolve();
    }
}