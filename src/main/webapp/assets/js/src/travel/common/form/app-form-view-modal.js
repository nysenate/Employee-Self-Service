var essTravel = angular.module('essTravel');

essTravel.directive('appFormViewModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/form/app-form-view-modal',
        controller: 'AppFormViewModal'
    }
}])
    .controller('AppFormViewModal', ['$scope', 'modals', appFormView]);

function appFormView($scope, modals) {

    $scope.app = modals.params();
    console.log($scope.app);

    $scope.exit = function () {
        modals.resolve();
    };

    $scope.viewExpenseSummary = function (app) {
        modals.open("app-expense-summary-modal", app, true)
            .catch(function () {});
    }
}
