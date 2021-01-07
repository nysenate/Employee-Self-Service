var essTravel = angular.module('essTravel');

essTravel.directive('appReviewApproveConfirmModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/review-travel-app-approve-confirm-modal',
        controller: 'ApproveConfirmModal'
    }
}])
    .controller('ApproveConfirmModal', ['$scope', 'modals', 'ApplicationReviewApi', confirmationCtrl]);

function confirmationCtrl($scope, modals, appReviewApi) {

    $scope.appReview = modals.params().review;
    $scope.role = modals.params().role;
    $scope.notes = "";
    $scope.isSingleDayTravel = $scope.appReview.travelApplication.activeAmendment.startDate
        === $scope.appReview.travelApplication.activeAmendment.endDate;

    $scope.approve = function () {
        appReviewApi.approve($scope.appReview.appReviewId, $scope.role.name, $scope.notes)
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