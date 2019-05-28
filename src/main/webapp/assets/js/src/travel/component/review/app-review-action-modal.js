var essTravel = angular.module('essTravel');

essTravel.directive('appReviewActionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-action-modal',
        controller: 'AppFormReviewCtrl'
    }
}])
    .controller('AppFormReviewCtrl', ['$scope', 'modals', 'LocationService', 'ApplicationReviewApi', appFormReviewCtrl]);

function appFormReviewCtrl($scope, modals, locationService, appReviewApi) {

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
