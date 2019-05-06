var essTravel = angular.module('essTravel');

essTravel.directive('appReviewViewModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-view-modal',
        scope: {},
        controller: 'AppReviewViewCtrl'
    }
}])
    .controller('AppReviewViewCtrl', ['$scope', appReviewViewCtrl]);

function appReviewViewCtrl($scope) {

    $scope.appReview = modals.params();

    $scope.exit = function () {
        modals.resolve();
    };
}