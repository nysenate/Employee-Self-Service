var essTravel = angular.module('essTravel');

essTravel.directive('appReviewFormModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/manage/review/app-review-form-modal',
        scope: {},
        controller: 'AppReviewFormCtrl'
    }
}])
    .controller('AppReviewFormCtrl', ['$scope', 'modals', 'TravelApplicationApprovalIdApi', appReviewFormCtrl]);

function appReviewFormCtrl($scope, modals, appApprovalIdApi) {

    $scope.appApproval = modals.params();
    console.log($scope.appApproval);

    $scope.approve = function () {
        appApprovalIdApi.save({approvalId: $scope.appApproval.approvalId}, function (response) {
            console.log(response);
        });
        modals.resolve();
    };

    $scope.disapprove = function () {
        console.log("DISAPPROVING");
        modals.reject();
    };

    $scope.exit = function () {
        modals.reject();
    };
}