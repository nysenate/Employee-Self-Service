var essTravel = angular.module('essTravel');

essTravel.directive('appReviewActionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/review/app-review-action-modal',
        controller: 'AppFormReviewCtrl'
    }
}])
    .controller('AppFormReviewCtrl', ['$scope', 'modals', appFormReviewCtrl]);

function appFormReviewCtrl($scope, modals) {

    $scope.appReview = modals.params();
    console.log($scope.appReview);

    $scope.approve = function () {
        modals.open("app-review-approve-confirm-modal", $scope.appReview);
    };

    $scope.disapprove = function () {
        modals.open("app-review-disapprove-confirm-modal", $scope.appReview);
    };

    $scope.exit = function () {
        modals.reject();
    };
}
