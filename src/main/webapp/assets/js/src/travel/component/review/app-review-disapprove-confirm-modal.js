var essTravel = angular.module('essTravel');

essTravel.directive('appReviewDisapproveConfirmModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-disapprove-confirm-modal',
        controller: 'DisapproveConfirmModal'
    }
}])
    .controller('DisapproveConfirmModal', ['$scope', 'modals', 'ApplicationReviewApi', disapproveConfirmCtrl]);

function disapproveConfirmCtrl($scope, modals, appReviewApi) {

    $scope.appReview = modals.params();
    $scope.notes = "";
    $scope.isSingleDayTravel = $scope.appReview.travelApplication.startDate === $scope.appReview.travelApplication.endDate;

    $scope.disapprove = function () {
        appReviewApi.disapprove($scope.appReview.appReviewId, $scope.notes);
        modals.resolve();
        modals.resolve();
    };

    $scope.cancel = function () {
        modals.reject();
    };
}