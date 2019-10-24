var essTravel = angular.module('essTravel');

essTravel.directive('appReviewActionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-action-modal',
        controller: 'AppFormReviewCtrl'
    }
}])
    .controller('AppFormReviewCtrl', ['$scope', 'modals', appFormReviewCtrl]);

function appFormReviewCtrl($scope, modals) {

    $scope.appReview = modals.params().review;
    $scope.role = modals.params().role;
    console.log($scope.appReview);

    $scope.approve = function () {
        modals.open("app-review-approve-confirm-modal", {review: $scope.appReview, role: $scope.role});
    };

    $scope.disapprove = function () {
        modals.open("app-review-disapprove-confirm-modal", {review: $scope.appReview, role: $scope.role});
    };

    $scope.exit = function () {
        modals.reject();
    };
}
