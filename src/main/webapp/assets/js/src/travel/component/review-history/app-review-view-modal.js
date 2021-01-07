var essTravel = angular.module('essTravel');

essTravel.directive('appReviewViewModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review-history/app-review-view-modal',
        controller: 'AppReviewViewCtrl'
    }
}])
    .controller('AppReviewViewCtrl', ['$scope', 'modals', appReviewViewCtrl]);

function appReviewViewCtrl($scope, modals) {

    $scope.appReview = modals.params();

    $scope.exit = function () {
        modals.resolve();
    };

    $scope.viewExpenseSummary = function () {
        modals.open("app-expense-summary-modal", $scope.appReview.travelApplication, true);
    }
}