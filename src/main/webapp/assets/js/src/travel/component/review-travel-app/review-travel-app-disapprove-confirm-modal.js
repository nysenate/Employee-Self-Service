var essTravel = angular.module('essTravel');

essTravel.directive('appReviewDisapproveConfirmModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review-travel-app/review-travel-app-disapprove-confirm-modal',
        controller: 'DisapproveConfirmModal'
    }
}])
    .controller('DisapproveConfirmModal', ['$scope', 'modals', 'ApplicationReviewApi', disapproveConfirmCtrl]);

function disapproveConfirmCtrl($scope, modals, appReviewApi) {

    $scope.appReview = modals.params().review;
    $scope.role = modals.params().role;
    $scope.notes = "";
    $scope.isSingleDayTravel = $scope.appReview.travelApplication.activeAmendment.startDate
        === $scope.appReview.travelApplication.activeAmendment.endDate;

    $scope.disapprove = function () {
        appReviewApi.disapprove($scope.appReview.appReviewId, $scope.role.name, $scope.notes)
            .$promise
            .then(function () {
                modals.resolve();
                modals.resolve();
            })
            .catch($scope.handleErrorResponse);
    };

    $scope.cancel = function () {
        modals.reject();
    };
}