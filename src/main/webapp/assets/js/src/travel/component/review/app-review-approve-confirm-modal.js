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
    $scope.isSingleDayTravel = $scope.appReview.travelApplication.startDate === $scope.appReview.travelApplication.endDate;

    $scope.approve = function () {
        appReviewApi.approve($scope.appReview.appReviewId, $scope.notes);
        modals.resolve();
        modals.resolve();
    };

    $scope.cancel = function () {
        modals.reject();
    };
}