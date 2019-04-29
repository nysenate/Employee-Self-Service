var essTravel = angular.module('essTravel');

essTravel.directive('appReviewFormModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/manage/review/app-review-form-modal',
        scope: {},
        controller: 'AppReviewFormCtrl'
    }
}])
    .controller('AppReviewFormCtrl', ['$scope', 'modals', 'TravelApplicationReviewApproveApi', 'TravelApplicationReviewDisapproveApi', appReviewFormCtrl]);

function appReviewFormCtrl($scope, modals, appReviewApproveApi, appReviewDisapproveApi) {

    $scope.appReview = modals.params();

    $scope.approve = function () {
        appReviewApproveApi.save({appReviewId: $scope.appReview.appReviewId}, function (response) {
            console.log(response);
        });
        modals.resolve();
    };

    $scope.disapprove = function () {
        appReviewDisapproveApi.save({appReviewId: $scope.appReview.appReviewId}, function (response) {
            console.log(response);
        });
        modals.resolve();
    };

    $scope.exit = function () {
        modals.reject();
    };
}