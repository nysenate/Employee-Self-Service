var essTravel = angular.module('essTravel');

essTravel.directive('appReviewFormModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-form-modal',
        scope: {},
        controller: 'AppReviewFormCtrl'
    }
}])
    .controller('AppReviewFormCtrl', ['$scope', 'modals', 'ApplicationReviewApi', appReviewFormCtrl]);

function appReviewFormCtrl($scope, modals, appReviewApi) {

    $scope.appReview = modals.params();

    $scope.approve = function () {
        appReviewApi.approve($scope.appReview.appReviewId);
        modals.resolve();
    };

    $scope.disapprove = function () {
        appReviewApi.disapprove($scope.appReview.appReviewId);
        modals.resolve();
    };

    $scope.exit = function () {
        modals.reject();
    };
}