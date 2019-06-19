var essTravel = angular.module('essTravel');

essTravel.directive('appReviewApproveConfirmModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-approve-confirm-modal',
        controller: 'ApproveConfirmModal'
    }
}])
    .controller('ApproveConfirmModal', ['$scope', 'modals', 'ApplicationReviewApi', confirmationCtrl]);

function confirmationCtrl($scope, modals, appReviewApi) {

    $scope.appReview = modals.params();
    $scope.notes = "";
    $scope.isDiscussionRequested = false;
    $scope.isSingleDayTravel = $scope.appReview.travelApplication.startDate === $scope.appReview.travelApplication.endDate;

    $scope.approve = function () {
        appReviewApi.approve($scope.appReview.appReviewId, $scope.notes, $scope.isDiscussionRequested)
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