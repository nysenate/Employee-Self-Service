var essTravel = angular.module('essTravel');

essTravel.directive('appReviewFormModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/manage/review/app-review-form-modal',
        scope: {},
        controller: 'AppReviewFormCtrl'
    }
}])
    .controller('AppReviewFormCtrl', ['$scope', 'modals', 'TravelApplicationApprovalApproveApi', 'TravelApplicationApprovalDisapproveApi', appReviewFormCtrl]);

function appReviewFormCtrl($scope, modals, approveApi, disapproveApi) {

    $scope.appApproval = modals.params();
    console.log($scope.appApproval);

    $scope.approve = function () {
        approveApi.save({approvalId: $scope.appApproval.approvalId}, function (response) {
            console.log(response);
        });
        modals.resolve();
    };

    $scope.disapprove = function () {
        disapproveApi.save({approvalId: $scope.appApproval.approvalId}, function (response) {
            console.log(response);
        });
        modals.resolve();
    };

    $scope.exit = function () {
        modals.reject();
    };
}